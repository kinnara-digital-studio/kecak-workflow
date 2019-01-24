package org.kecak.apps.processor;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.joget.apps.app.dao.AppDefinitionDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PackageActivityForm;
import org.joget.apps.app.model.PluginDefaultProperties;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.spring.model.EmailApprovalContent;
import org.joget.commons.spring.model.EmailApprovalContentDao;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.StringUtil;
import org.joget.directory.model.User;
import org.joget.directory.model.service.DirectoryManager;
import org.joget.plugin.base.ExtDefaultPlugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.service.PropertyUtil;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.kecak.apps.app.model.EmailProcessorPlugin;

import javax.annotation.Nonnull;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class EmailProcessor {

    public static final String MAIL_SUBJECT_PATTERN = "{unuse}processId:{processId}";

    public static final String FROM = "from";
    public static final String SUBJECT = "subject";

    private EmailApprovalContentDao emailApprovalContentDao;
    private WorkflowManager workflowManager;
    private AppService appService;
    private WorkflowUserManager workflowUserManager;
    private DirectoryManager directoryManager;
    private PluginManager pluginManager;
    private AppDefinitionDao appDefinitionDao;

    public void parseEmail(@Body final String body, final Exchange exchange) {
        //GET EMAIL ADDRESS
        final String from = exchange.getIn().getHeader(FROM).toString().replaceAll("^<|>$", "");
        LogUtil.info(this.getClass().getName(), "[FROM] : " + from);
        
        String username = getUsername(from);
        workflowUserManager.setCurrentThreadUser(username);

        final String subject = exchange.getIn().getHeader(SUBJECT).toString().replace("\t", "__").replace("\n", "__").replace(" ", "__");

        Pattern pattern = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}");
        Matcher matcher = pattern.matcher(MAIL_SUBJECT_PATTERN);

        String subjectRegex = createRegex(MAIL_SUBJECT_PATTERN);
        Pattern pattern2 = Pattern.compile("^" + subjectRegex + "$");
        Matcher matcher2 = pattern2.matcher(subject);

        String processId = null;
        while (matcher2.find()) {
            int count = 1;
            while (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher2.group(count);
                if ("processId".equals(key)) {
                    processId = value;
                }
                count++;
            }
        }

        if (processId != null) {
            parseEmailContent(processId, username, body);
        } else {
            LogUtil.info(getClass().getName(), "Empty process ID");
        }

        Collection<AppDefinition> appDefinitions = appDefinitionDao.findPublishedApps(null, null, null, null);
        if(appDefinitions == null) {
            return;
        }

        appDefinitions.forEach(appDefinition -> {
            Collection<PluginDefaultProperties> pluginDefaultProperties = appDefinition.getPluginDefaultPropertiesList();
            if(pluginDefaultProperties != null) {
                pluginDefaultProperties.forEach(pluginDefaultProperty -> {
                    Stream.of(pluginDefaultProperty)
                            .map(p -> pluginManager.getPlugin(p.getId()))
                            .filter(p -> p instanceof EmailProcessorPlugin && p instanceof ExtDefaultPlugin)
                            .map(p -> (ExtDefaultPlugin)p)
                            .forEach(p -> {
                                Map<String, Object> pluginProperties = PropertyUtil.getPropertiesValueFromJson(pluginDefaultProperty.getPluginProperties());
                                p.setProperties(pluginProperties);

                                Map<String, Object> parameterProperties = new HashMap<>(pluginProperties);
                                parameterProperties.put(EmailProcessorPlugin.PROPERTY_APP_DEFINITION, appDefinition);
                                parameterProperties.put(EmailProcessorPlugin.PROPERTY_FROM, from);
                                parameterProperties.put(EmailProcessorPlugin.PROPERTY_SUBJECT, subject);
                                parameterProperties.put(EmailProcessorPlugin.PROPERTY_BODY, body);
                                parameterProperties.put(EmailProcessorPlugin.PROPERTY_EXCHANGE, exchange);

                                LogUtil.info(getClass().getName(), "Processing Email Plugin ["+p.getName()+"] for application ["+appDefinition.getAppId()+"]");
                                ((EmailProcessorPlugin) p).parse(parameterProperties);
                            });
                });
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void parseEmailContent(String processId, String username, String emailContent) {
        String content = emailContent.replaceAll("\\r?\\n", " ");
        content = content.replaceAll("\\_\\_", " ");
        content = StringUtil.decodeURL(content);
        
        String processDefId = null;
        String activityDefId = null;
        String activityId = null;

//        workflowUserManager.setCurrentThreadUser(username);

        WorkflowAssignment workflowAssignment = workflowManager.getAssignmentByProcess(processId);
        if (workflowAssignment != null) {
            processDefId = workflowAssignment.getProcessDefId();
            activityDefId = workflowAssignment.getActivityDefId();
            activityId = workflowAssignment.getActivityId();
        } else {
            LogUtil.info(this.getClass().getName(), "WorkflowAssignment is NULL");
            LogUtil.info(this.getClass().getName(), "User ["+workflowUserManager.getCurrentUsername()+"] Process Def ID [" + processDefId + "] Process ID [" + processId + "] Activity Def ID ["+activityDefId+"] Activity ID [" + activityId + "]");
            LogUtil.info(this.getClass().getName(), "[EMAIL CONTENT]:" + emailContent);
        }

        EmailApprovalContent emailApprovalContent = null;
        if (processDefId != null && activityDefId != null) {
            int startIndex = processDefId.indexOf("#");
            int length = processDefId.indexOf("#", startIndex + 1);
            if (startIndex > -1) {
                String processVersion = processDefId.substring(startIndex + 1, length);
                processDefId = processDefId.replace("#" + processVersion + "#", "#latest#");
            }
            emailApprovalContent = emailApprovalContentDao.getEmailApprovalContent(processDefId, activityDefId);
        }
        
        if (emailApprovalContent != null) {
            if (emailApprovalContent.getContent() != null) {
                String emailContentPattern = emailApprovalContent.getContent();
                emailContentPattern = emailContentPattern.replaceAll("\\r?\\n", " ");
//                LOGGER.info("Email Content Pattern:[" + emailContentPattern + "]");
                
                String patternRegex = createRegex(emailContentPattern);
                LogUtil.info(this.getClass().getName(), "[Content REGEX] "+patternRegex);
                Pattern pattern = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}");
                Matcher matcher = pattern.matcher(emailContentPattern);

                Pattern pattern2 = Pattern.compile("^" + patternRegex + "$");
                Matcher matcher2 = pattern2.matcher(content);

                @SuppressWarnings("rawtypes")
                Map<String, String> variables = new HashMap<>();
                Map<String, String> fields = new HashMap<>();
//                FormData formData = new FormData();
                
                while (matcher2.find()) {
                    int count = 1;
                    while (matcher.find()) {
                        String key = matcher.group(1);
                        String value = matcher2.group(count);
                        if (key.startsWith("var_")) {
                            key = key.replaceAll("var_", "");
                            LogUtil.info(this.getClass().getName(), "[Var] "+key);
                            variables.put(key, value.trim());
                        } else if (key.startsWith("form_")) {
                            key = key.replaceAll("form_", "");
                            LogUtil.info(this.getClass().getName(), "[Form] "+key+" ,[VALUE] "+value);
                            if(value == null || value.trim().equals("")){
                                value = "-";
                            }
                            fields.put(key, value);
                        }
                        count++;
                    }
                }
                completeActivity(username, processId, activityId, fields, variables, content);
            }
        }
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    private void completeActivity(String username, String processId, String activityId, @Nonnull final Map<String, String> fields, @Nonnull final Map<String, String> variables, String message) {
        if (username != null) {
            String currentUsername = workflowUserManager.getCurrentUsername();
            try {
                // set current user
                workflowUserManager.setCurrentThreadUser(username);
                // find assignment
                WorkflowAssignment assignment = null;
                if (activityId != null) {
                    assignment = workflowManager.getAssignment(activityId);
                }
                if (assignment == null || processId != null) {
                    assignment = workflowManager.getAssignmentByProcess(processId);
                }

                if (assignment != null) {
                    AppDefinition currentAppDef = AppUtil.getCurrentAppDefinition();
                    try {
                        String assignmentId = assignment.getActivityId();
                        AppDefinition appDef = appService.getAppDefinitionForWorkflowActivity(assignmentId);

                        //if has form data to submit
                        final FormData formData = new FormData();
                        PackageActivityForm activityForm = appService.viewAssignmentForm(appDef, assignment, formData, "", "");
                        Form form = activityForm.getForm();
                        if(form != null) {
                            for (Map.Entry<String, String> entry : fields.entrySet()) {
                                Element element = FormUtil.findElement(entry.getKey(), form, formData, true);
                                if(element != null) {
                                    String parameterName = FormUtil.getElementParameterName(element);
                                    formData.addRequestParameterValues(parameterName, new String[] {entry.getValue()});
                                }
                            }

                            if(!formData.getRequestParams().isEmpty()) {
                                FormData resultFormData = appService.submitForm(activityForm.getForm(), formData, false);
                                for(Map.Entry<String, String> entry : resultFormData.getFormErrors().entrySet()) {
                                    LogUtil.error(getClass().getName(), null, "Error submitting form [" + form.getPropertyString(FormUtil.PROPERTY_ID) + "] field [" + entry.getKey()+"] message [" + entry.getValue()+"]");
                                }
                            }
                        }

                        if (!assignment.isAccepted()) {
                            workflowManager.assignmentAccept(assignmentId);
                        }

                        workflowManager.assignmentComplete(assignmentId, variables);
//	                    sendAutoReply(sender, subject);
                    } finally {
                        AppUtil.setCurrentAppDefinition(currentAppDef);
                    }
//	                addActivityLog(sender, processId, activityId, subject, message, variables, formData.getRequestParams());
                } else {
                    LogUtil.error(getClass().getName(), null, "Assignment not found for process [" + processId + "] or activityId [" + activityId + "]");
                }

            } finally {
                workflowUserManager.setCurrentThreadUser(currentUsername);
            }
        } else {
            LogUtil.error(getClass().getName(), null, "No user found for sender ["+ username + "]");
        }
    }

    public String getUsername(String sender) {
        // get sender
        InternetAddress ia = null;
        try {
            ia = new InternetAddress(sender);
        } catch (AddressException e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
        }
        if (ia != null) {
            String email = ia.getAddress();
            directoryManager = (DirectoryManager) AppUtil.getApplicationContext().getBean("directoryManager");
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
                LogUtil.info(getClass().getName(), "No directory user for EMAIL ["+email+"]");
                return null;
            } else {
                return user.getUsername();
            }
        }
        return null;
    }

    public String createRegex(String raw) {
        String result = escapeString(raw, null);
        result = result.replaceAll("\\\\\\{unuse\\\\\\}", "__([\\\\s\\\\S]*)").replaceAll("\\\\\\{[a-zA-Z0-9_]+\\\\\\}", "(.*?)");
        if (result.startsWith("__")) {
            result = result.substring(2);
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public String escapeString(String inStr, Map<String, String> replaceMap) {
        if (replaceMap != null) {
            Iterator it = replaceMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
                inStr = inStr.replaceAll(pairs.getKey(), escapeRegex(pairs.getValue()));
            }
        }

        return escapeRegex(inStr);
    }

    public String escapeRegex(String inStr) {
        return (inStr != null) ? inStr.replaceAll("([\\\\*+\\[\\](){}\\$.?\\^|])", "\\\\$1") : null;
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
}
