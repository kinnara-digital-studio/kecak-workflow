package org.kecak.apps.email;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.model.EmailProcessorPlugin;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.spring.model.EmailApprovalContentDao;
import org.joget.commons.util.LogUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.directory.model.service.DirectoryUtil;
import org.joget.plugin.base.ExtDefaultPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.service.PropertyUtil;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class EmailProcessor {
    public static final String FROM = "from";
    public static final String SUBJECT = "subject";

    private EmailApprovalContentDao emailApprovalContentDao;
    private WorkflowManager workflowManager;
    private AppService appService;
    private WorkflowUserManager workflowUserManager;
    private DirectoryManager directoryManager;
    private PluginManager pluginManager;
    private AppDefinitionDao appDefinitionDao;

    /**
     *
     * @param body
     * @param exchange
     */
    public void parseEmail(@Body final String body, final Exchange exchange) {
        // get sender email address
        final String from = exchange.getIn().getHeader(FROM).toString();
        LogUtil.info(this.getClass().getName(), "[FROM] : " + from);

        String fromEmail = from.replaceAll("^.*<|>.*$", "");
        String username = getUsername(fromEmail);
        workflowUserManager.setCurrentThreadUser(username);

        final String subject = exchange.getIn().getHeader(SUBJECT).toString().replace("\t", "__").replace("\n", "__").replace(" ", "__");

        Optional.ofNullable(appDefinitionDao.findPublishedApps(null, null, null, null))
                .map(Collection::stream)
                .orElse(Stream.empty())

                // set current app definition
                .peek(AppUtil::setCurrentAppDefinition)

                .forEach(appDefinition -> Optional.ofNullable(appDefinition.getPluginDefaultPropertiesList())
                        .map(Collection::stream)
                        .orElse(Stream.empty())

                        // process every plugin default property
                        .forEach(pluginDefaultProperty -> Stream.of(pluginDefaultProperty)
                                .map(p -> pluginManager.getPlugin(p.getId()))
                                .filter(p -> p instanceof EmailProcessorPlugin && p instanceof ExtDefaultPlugin)
                                .map(p -> (ExtDefaultPlugin) p)
                                .forEach(p -> {
                                    Map<String, Object> pluginProperties = PropertyUtil.getPropertiesValueFromJson(pluginDefaultProperty.getPluginProperties());
                                    p.setProperties(pluginProperties);

                                    Map<String, Object> parameterProperties = new HashMap<>(pluginProperties);
                                    parameterProperties.put(EmailProcessorPlugin.PROPERTY_APP_DEFINITION, appDefinition);
                                    parameterProperties.put(EmailProcessorPlugin.PROPERTY_FROM, fromEmail);
                                    parameterProperties.put(EmailProcessorPlugin.PROPERTY_SUBJECT, subject);
                                    parameterProperties.put(EmailProcessorPlugin.PROPERTY_BODY, body);
                                    parameterProperties.put(EmailProcessorPlugin.PROPERTY_EXCHANGE, exchange);

                                    LogUtil.info(getClass().getName(), "Processing Email Plugin [" + p.getName() + "] for application [" + appDefinition.getAppId() + "]");
                                    ((EmailProcessorPlugin) p).parse(parameterProperties);
                                })));
    }

    /**
     *
     * @param sender
     * @return
     */
    private String getUsername(String sender) {
        // get sender
        InternetAddress ia = null;
        try {
            ia = new InternetAddress(sender);
        } catch (AddressException e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
        }

        if (ia == null) {
            LogUtil.warn(getClass().getName(), "Address not found for sender [" + sender + "]");
            return null;
        }

        String email = ia.getAddress();
//            directoryManager = (DirectoryManager) AppUtil.getApplicationContext().getBean("directoryManager");

        User user = directoryManager.getUserList(email, null, null, 0, 1)
                .stream()
                .findFirst()
                // get data based on email without domain address
                .orElseGet(() -> directoryManager.getUserList(email.replaceAll("@.+", ""), null, null, null, null)
                        .stream()
                        .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                        .findFirst()
                        .orElse(null));

        if (user == null) {
            LogUtil.info(getClass().getName(), "No directory user for email [" + email + "]");
            return DirectoryUtil.ROLE_ANONYMOUS;
        }

        return user.getUsername();
    }

    public EmailApprovalContentDao getEmailApprovalContentDao() {
        return emailApprovalContentDao;
    }

    public void setEmailApprovalContentDao(EmailApprovalContentDao emailApprovalContentDao) {
        this.emailApprovalContentDao = emailApprovalContentDao;
    }

    public WorkflowManager getWorkflowManager() {
        return workflowManager;
    }

    public void setWorkflowManager(WorkflowManager workflowManager) {
        this.workflowManager = workflowManager;
    }

    public AppService getAppService() {
        return appService;
    }

    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    public WorkflowUserManager getWorkflowUserManager() {
        return workflowUserManager;
    }

    public void setWorkflowUserManager(WorkflowUserManager workflowUserManager) {
        this.workflowUserManager = workflowUserManager;
    }

    public DirectoryManager getDirectoryManager() {
        return directoryManager;
    }

    public void setDirectoryManager(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void setAppDefinitionDao(AppDefinitionDao appDefinitionDao) {
        this.appDefinitionDao = appDefinitionDao;
    }
}
