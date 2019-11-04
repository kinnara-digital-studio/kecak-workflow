package org.kecak.webapi.soap.endpoint;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.apps.app.dao.*;
import org.joget.apps.app.model.*;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataListAction;
import org.joget.apps.datalist.service.DataListService;
import org.joget.apps.form.lib.DefaultFormBinder;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.*;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.workflow.model.WorkflowPackage;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;


@Endpoint
public class SoapAppEndpoint {
    private static final String NAMESPACE_URI = "http://kecak.org/soap/process/schemas";
    private final Namespace namespace = Namespace.getNamespace("xps", NAMESPACE_URI);

    private XPathExpression<Element> appIdExpression;
    private XPathExpression<Element> versionExpression;
    private XPathExpression<Element> formIdExpression;
    private XPathExpression<Element> urlExpression;
    private XPathExpression<Element> filterExpression;
    private XPathExpression<Element> nameExpression;
    private XPathExpression<Element> sortExpression;
    private XPathExpression<Element> descExpression;
    private XPathExpression<Element> startExpression;
    private XPathExpression<Element> rowsExpression;
    private XPathExpression<Element> packageXpdlExpression;
    private XPathExpression<Element> localeExpression;

    @Autowired
    private AppService appService;
    @Autowired
    private AppDefinitionDao appDefinitionDao;
    @Autowired
    private DataListService dataListService;
    @Autowired
    private EnvironmentVariableDao environmentVariableDao;
    @Autowired
    private DatalistDefinitionDao datalistDefinitionDao;
    @Autowired
    private FormDefinitionDao formDefinitionDao;
    @Autowired
    private WorkflowUserManager workflowUserManager;
    @Autowired
    private WorkflowManager workflowManager;
    @Autowired
    private PluginManager pluginManager;
    @Autowired
    private PluginDefaultPropertiesDao pluginDefaultPropertiesDao;
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UserviewDefinitionDao userviewDefinitionDao;

    public SoapAppEndpoint() {

        XPathFactory xpathFactory = XPathFactory.instance();
        try {
            appIdExpression = xpathFactory.compile("//xps:appId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            appIdExpression = null;
        }
        try {
            versionExpression = xpathFactory.compile("//xps:version", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            versionExpression = null;
        }
        try {
            formIdExpression = xpathFactory.compile("//xps:formId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            formIdExpression = null;
        }
        try {
            urlExpression = xpathFactory.compile("//xps:url", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            urlExpression = null;
        }
        try {
            filterExpression = xpathFactory.compile("//xps:filter", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            filterExpression = null;
        }
        try {
            nameExpression = xpathFactory.compile("//xps:name", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            nameExpression = null;
        }
        try {
            sortExpression = xpathFactory.compile("//xps:sort", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            sortExpression = null;
        }
        try {
            descExpression = xpathFactory.compile("//xps:desc", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            descExpression = null;
        }
        try {
            startExpression = xpathFactory.compile("//xps:start", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            startExpression = null;
        }
        try {
            rowsExpression = xpathFactory.compile("//xps:rows", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            rowsExpression = null;
        }
        try {
            localeExpression = xpathFactory.compile("//xps:locale", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            localeExpression = null;
        }
        try {
            packageXpdlExpression = xpathFactory.compile("//xps:packageXpdl", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            packageXpdlExpression = null;
        }

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppFormColumnsRequest")
    public @ResponsePayload Element handleAppFormColumns(@RequestPayload Element appFormColumnsElement) {
        final String appId = appIdExpression.evaluate(appFormColumnsElement).get(0).getValue();
        final String appVersion = versionExpression.evaluate(appFormColumnsElement).get(0).getValue();
        final String formId = formIdExpression.evaluate(appFormColumnsElement).get(0).getValue();
        Element response = new Element("AppFormColumnsResponse",namespace);
        AppDefinition appDef = appService.getAppDefinition(appId, appVersion);
        try {
            Collection<Map<String, String>> columns = FormUtil.getFormColumns(appDef, formId);
            for (@SuppressWarnings("rawtypes") Map c : columns) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("label",namespace).setText(c.get("label").toString()));
                item.addContent(new Element("value",namespace).setText(c.get("value").toString()));
                response.addContent(item);
            }
        } catch (Exception e) {
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppsInstallRequest")
    public @ResponsePayload Element handleAppsInstall(@RequestPayload Element appsInstallElement) throws IOException {
        final String url = urlExpression.evaluate(appsInstallElement).get(0).getValue();
        Element response = new Element("AppsInstallResponse",namespace);
        // get URL InputStream
        HttpClientBuilder builder = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy());
        CloseableHttpClient client = builder.build();
        try {
            HttpGet get = new HttpGet(url);
            HttpResponse httpResponse = client.execute(get);
            InputStream in = httpResponse.getEntity().getContent();

            if (httpResponse.getStatusLine().getStatusCode() == HttpServletResponse.SC_OK) {
                // read InputStream
                byte[] fileContent = readInputStream(in);

                // import app
                final AppDefinition appDef = appService.importApp(fileContent);
                if (appDef != null) {
                    TransactionTemplate transactionTemplate = (TransactionTemplate)AppUtil.getApplicationContext().getBean("transactionTemplate");
                    transactionTemplate.execute(new TransactionCallback<Object>() {
                        public Object doInTransaction(TransactionStatus ts) {
                            appService.publishApp(appDef.getId(), null);
                            return false;
                        }
                    });

                    response.addContent(new Element("appId",namespace).setText(appDef.getAppId()));
                    response.addContent(new Element("appName",namespace).setText(appDef.getName()));
                    response.addContent(new Element("appVersion",namespace).setText(appDef.getVersion().toString()));
                }
            }
        } finally {
            client.close();
        }
        return response;
    }
    /**
     * Reads a specified InputStream, returning its contents in a byte array
     * @param in
     * @return
     * @throws IOException
     */
    protected byte[] readInputStream(InputStream in) throws IOException {
        byte[] fileContent;
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            BufferedInputStream bin = new BufferedInputStream(in);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = bin.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            fileContent = out.toByteArray();
            return fileContent;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                LogUtil.error(getClass().getName(), ex, ex.getMessage());
            }
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppsPublishedProcessesRequest")
    public @ResponsePayload Element handleAppsPublishedProcesses(@RequestPayload Element appsPublishedProcessesElement) {
        final String appId = appIdExpression.evaluate(appsPublishedProcessesElement).get(0).getValue();
        Element response = new Element("AppsPublishedProcessesResponse",namespace);
        // get list of published processes
        Map<AppDefinition, Collection<WorkflowProcess>> appProcessMap = appService.getPublishedProcesses(appId);
        Element apps = new Element("apps",namespace);
        for (Iterator<AppDefinition> i=appProcessMap.keySet().iterator(); i.hasNext();) {
            AppDefinition appDef = i.next();
            Collection<WorkflowProcess> processList = appProcessMap.get(appDef);
            Element item = new Element("item",namespace);
            item.addContent(new Element("id",namespace).setText(appDef.getAppId()));
            item.addContent(new Element("name",namespace).setText(appDef.getName()));
            item.addContent(new Element("version",namespace).setText(appDef.getVersion().toString()));
            Element processes = new Element("processes",namespace);
            for (WorkflowProcess processDef: processList) {
                Element el = new Element("item",namespace);
                el.addContent(new Element("id",namespace).setText(processDef.getId()));
                el.addContent(new Element("idWithoutVersion",namespace).setText(processDef.getIdWithoutVersion()));
                el.addContent(new Element("name",namespace).setText(processDef.getName()));
                el.addContent(new Element("processVersion",namespace).setText(processDef.getVersion()));
                el.addContent(new Element("appVersion",namespace).setText(appDef.getVersion().toString()));
                String url = WorkflowUtil.getHttpServletRequest().getContextPath() + "/web/client/app/" + appDef.getId() + "/" + appDef.getVersion() + "/process/" + processDef.getIdWithoutVersion() + "?start=true";
                el.addContent(new Element("url",namespace).setText(url));
                processes.addContent(el);
            }
            item.addContent(processes);
            apps.addContent(item);
        }
        response.addContent(apps);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppsPublishedUserviewsRequest")
    public @ResponsePayload Element handleAppsPublishedUserviews(@RequestPayload Element appsPublishedUserviewsElement) {
        final String appId = appIdExpression.evaluate(appsPublishedUserviewsElement).get(0).getValue();
        Element response = new Element("AppsPublishedUserviewsResponse",namespace);
        Collection<AppDefinition> appDefinitionList = appService.getPublishedApps(appId);
        Element apps = new Element("apps",namespace);
        for (AppDefinition appDef: appDefinitionList) {
            Element app = new Element("item",namespace);
            app.addContent(new Element("id",namespace).setText(appDef.getAppId()));
            app.addContent(new Element("name",namespace).setText(StringUtil.stripAllHtmlTag(appDef.getName())));
            app.addContent(new Element("version",namespace).setText(appDef.getVersion().toString()));
            Element userviews = new Element("userviews",namespace);
            for (UserviewDefinition userviewDef: appDef.getUserviewDefinitionList()) {
                Element u = new Element("item",namespace);
                u.addContent(new Element("id",namespace).setText(userviewDef.getId()));
                u.addContent(new Element("name",namespace).setText(StringUtil.stripAllHtmlTag(AppUtil.processHashVariable(userviewDef.getName(), null, null, null, appDef))));
                u.addContent(new Element("version",namespace).setText(userviewDef.getAppVersion().toString()));
                u.addContent(new Element("description",namespace).setText(StringUtil.stripAllHtmlTag(userviewDef.getDescription())));
                String url = WorkflowUtil.getHttpServletRequest().getContextPath() + "/web/userview/" + appDef.getId() + "/" + userviewDef.getId();
                u.addContent(new Element("url",namespace).setText(url));
                userviews.addContent(u);
            }
            app.addContent(userviews);
            apps.addContent(app);
        }
        response.addContent(apps);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppListRequest")
    public @ResponsePayload Element handleAppList(@RequestPayload Element appListRequestElement) {
        final String name = nameExpression.evaluate(appListRequestElement).get(0).getValue();
        final String sort = sortExpression.evaluate(appListRequestElement).get(0).getValue();
        final String descString = descExpression.evaluate(appListRequestElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(appListRequestElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(appListRequestElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AppListResponse",namespace);
        Collection<AppDefinition> appDefinitionList = appDefinitionDao.findLatestVersions(null, null, name, sort, desc, start, rows);
        Long count = appDefinitionDao.countLatestVersions(null, null, name);

        Element data = new Element("data",namespace);
        for (AppDefinition appDef : appDefinitionList) {
            Element item = new Element("item",namespace);
            item.addContent(new Element("id",namespace).setText(appDef.getId()));
            item.addContent(new Element("name",namespace).setText(appDef.getName()));
            item.addContent(new Element("version",namespace).setText(appDef.getVersion().toString()));
            data.addContent(item);
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(count.toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppVersionListRequest")
    public @ResponsePayload Element handleAppVersionList(@RequestPayload Element appVersionListElement) {
        final String appId = appIdExpression.evaluate(appVersionListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(appVersionListElement).get(0).getValue();
        final String descString = descExpression.evaluate(appVersionListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(appVersionListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(appVersionListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AppVersionListResponse",namespace);
        Collection<AppDefinition> appDefList = appDefinitionDao.findVersions(appId, sort, desc, start, rows);
        Long count = appDefinitionDao.countVersions(appId);
        Element data = new Element("data",namespace);
        if (appDefList != null && appDefList.size() > 0) {
            for (AppDefinition appDef : appDefList) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("version",namespace).setText(appDef.getVersion().toString()));
                item.addContent(new Element("published",namespace).setText((appDef.isPublished()) ? "<div class=\"tick\"></div>" : ""));
                item.addContent(new Element("dateCreated",namespace).setText(TimeZoneUtil.convertToTimeZone(appDef.getDateCreated(), null, AppUtil.getAppDateFormat())));
                item.addContent(new Element("dateModified",namespace).setText(TimeZoneUtil.convertToTimeZone(appDef.getDateModified(), null, AppUtil.getAppDateFormat())));
                item.addContent(new Element("description",namespace).setText(appDef.getDescription()));
                data.addContent(item);
            }
        }

        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(count.toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppBuilderActionsRequest")
    public @ResponsePayload Element handleAppBuilderActions(@RequestPayload Element appBuilderActionsElement) {
        final String appId = appIdExpression.evaluate(appBuilderActionsElement).get(0).getValue();
        final String appVersion = versionExpression.evaluate(appBuilderActionsElement).get(0).getValue();
        Element response = new Element("AppBuilderActionsResponse",namespace);
        appService.getAppDefinition(appId, appVersion);
        // get available binders
        DataListAction[] actions = dataListService.getAvailableActions();
        Element collections = new Element("actions",namespace);
        for (DataListAction action : actions) {
            Plugin p = (Plugin) action;
            Element item = new Element("item",namespace);
            item.addContent(new Element("name",namespace).setText(p.getName()));
            item.addContent(new Element("label",namespace).setText(p.getI18nLabel()));
            item.addContent(new Element("className",namespace).setText(action.getClassName()));
            if (action instanceof PropertyEditable) {
                String propertyOptions = ((PropertyEditable) action).getPropertyOptions();
                if (propertyOptions != null && !propertyOptions.isEmpty()) {
                    item.addContent(new Element("propertyOptions",namespace).setText(propertyOptions));
                }
            }
            item.addContent(new Element("type",namespace).setText("text"));
            collections.addContent(item);
        }
        response.addContent(collections);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppEnvVariableListRequest")
    public @ResponsePayload Element handleAppEnvVariableList(@RequestPayload Element appEnvVariableListElement) {
        final String appId = appIdExpression.evaluate(appEnvVariableListElement).get(0).getValue();
        final String version = versionExpression.evaluate(appEnvVariableListElement).get(0).getValue();
        final String filterString = filterExpression.evaluate(appEnvVariableListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(appEnvVariableListElement).get(0).getValue();
        final String descString = descExpression.evaluate(appEnvVariableListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(appEnvVariableListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(appEnvVariableListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AppEnvVariableListResponse",namespace);
        AppDefinition appDef = appService.getAppDefinition(appId, version);

        Collection<EnvironmentVariable> environmentVariableList = null;
        Long count = null;

        if (appDef != null) {
            environmentVariableList = environmentVariableDao.getEnvironmentVariableList(filterString, appDef, sort, desc, start, rows);
            count = environmentVariableDao.getEnvironmentVariableListCount(filterString, appDef);
        }
        Element data = new Element("data",namespace);
        if (environmentVariableList != null && environmentVariableList.size() > 0) {
            for (EnvironmentVariable environmentVariable : environmentVariableList) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(environmentVariable.getId()));
                item.addContent(new Element("value",namespace).setText(environmentVariable.getValue()));
                item.addContent(new Element("remarks",namespace).setText(environmentVariable.getRemarks()));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(count.toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppDatalistListRequest")
    public @ResponsePayload Element handleAppDatalistList(@RequestPayload Element appDatalistListElement) {
        final String appId = appIdExpression.evaluate(appDatalistListElement).get(0).getValue();
        final String version = versionExpression.evaluate(appDatalistListElement).get(0).getValue();
        final String filterString = filterExpression.evaluate(appDatalistListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(appDatalistListElement).get(0).getValue();
        final String descString = descExpression.evaluate(appDatalistListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(appDatalistListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(appDatalistListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AppDatalistListResponse",namespace);
        AppDefinition appDef = appService.getAppDefinition(appId, version);

        Collection<DatalistDefinition> datalistDefinitionList = null;
        Long count = null;

        if (appDef != null) {
            datalistDefinitionList = datalistDefinitionDao.getDatalistDefinitionList(filterString, appDef, sort, desc, start, rows);
            count = datalistDefinitionDao.getDatalistDefinitionListCount(filterString, appDef);
        }

        Element data = new Element("data",namespace);
        if (datalistDefinitionList != null && datalistDefinitionList.size() > 0) {
            for (DatalistDefinition datalistDefinition : datalistDefinitionList) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(datalistDefinition.getId()));
                item.addContent(new Element("name",namespace).setText(datalistDefinition.getName()));
                item.addContent(new Element("description",namespace).setText(datalistDefinition.getDescription()));
                item.addContent(new Element("dateCreated",namespace).setText(TimeZoneUtil.convertToTimeZone(datalistDefinition.getDateCreated(), null, AppUtil.getAppDateFormat())));
                item.addContent(new Element("dateModified",namespace).setText(TimeZoneUtil.convertToTimeZone(datalistDefinition.getDateModified(), null, AppUtil.getAppDateFormat())));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(count.toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppDatalistOptionsRequest")
    public @ResponsePayload Element handleAppDatalistOptions(@RequestPayload Element appDatalistOptionsElement) {
        final String appId = appIdExpression.evaluate(appDatalistOptionsElement).get(0).getValue();
        final String version = versionExpression.evaluate(appDatalistOptionsElement).get(0).getValue();
        final String descString = descExpression.evaluate(appDatalistOptionsElement).get(0).getValue();
        final String startString = startExpression.evaluate(appDatalistOptionsElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(appDatalistOptionsElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        String sort = sortExpression.evaluate(appDatalistOptionsElement).get(0).getValue();
        boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        Element response = new Element("AppDatalistOptionsResponse",namespace);
        Collection<DatalistDefinition> datalistDefinitionList = null;

        if (sort == null) {
            sort = "name";
            desc = false;
        }

        AppDefinition appDef = appService.getAppDefinition(appId, version);
        datalistDefinitionList = datalistDefinitionDao.getDatalistDefinitionList(null, appDef, sort, desc, start, rows);
        Element item = new Element("item",namespace);
        item.addContent(new Element("value",namespace).setText(""));
        item.addContent(new Element("label",namespace).setText(""));
        response.addContent(item);
        for (DatalistDefinition datalistDef : datalistDefinitionList) {
            Element item2 = new Element("item",namespace);
            item2.addContent(new Element("value",namespace).setText(datalistDef.getId()));
            item2.addContent(new Element("label",namespace).setText(datalistDef.getName()));
            response.addContent(item2);
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppFormTableNameListRequest")
    public @ResponsePayload Element handleAppFormTableNameList(@RequestPayload Element appFormTableNameListElement) {
        final String appId = appIdExpression.evaluate(appFormTableNameListElement).get(0).getValue();
        final String version = versionExpression.evaluate(appFormTableNameListElement).get(0).getValue();
        Element response = new Element("AppFormTableNameListResponse",namespace);
        AppDefinition appDef = appService.getAppDefinition(appId, version);
        Collection<String> tableNameList = formDefinitionDao.getTableNameList(appDef);
        tableNameList.forEach((value) -> {
            response.addContent(new Element("item",namespace).setText(value));
        });
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppFormsRequest")
    public @ResponsePayload Element handleAppForms(@RequestPayload Element appFormsElement) {
        final String appId = appIdExpression.evaluate(appFormsElement).get(0).getValue();
        final String version = versionExpression.evaluate(appFormsElement).get(0).getValue();
        final String name = nameExpression.evaluate(appFormsElement).get(0).getValue();
        final String descString = descExpression.evaluate(appFormsElement).get(0).getValue();
        final String startString = startExpression.evaluate(appFormsElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(appFormsElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        final String sort = sortExpression.evaluate(appFormsElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        Element response = new Element("AppFormsResponse",namespace);

        Collection<FormDefinition> formDefinitionList = null;
        Long count = null;

        AppDefinition appDef = appService.getAppDefinition(appId, version);
        formDefinitionList = formDefinitionDao.getFormDefinitionList(name, appDef, sort, desc, start, rows);
        count = formDefinitionDao.getFormDefinitionListCount(null, appDef);

        Element data = new Element("data",namespace);
        for (FormDefinition formDef : formDefinitionList) {
            Element item = new Element("item",namespace);
            item.addContent(new Element("id",namespace).setText(formDef.getId()));
            item.addContent(new Element("name",namespace).setText(formDef.getName()));
            item.addContent(new Element("tableName",namespace).setText(formDef.getTableName()));
            item.addContent(new Element("dateCreated",namespace).setText(TimeZoneUtil.convertToTimeZone(formDef.getDateCreated(), null, AppUtil.getAppDateFormat())));
            item.addContent(new Element("dateModified",namespace).setText(TimeZoneUtil.convertToTimeZone(formDef.getDateModified(), null, AppUtil.getAppDateFormat())));
            data.addContent(item);
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(count.toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppFormsOptionsRequest")
    public @ResponsePayload Element handleAppFormsOptions(@RequestPayload Element appFormsOptionsElement) {
        final String appId = appIdExpression.evaluate(appFormsOptionsElement).get(0).getValue();
        final String version = versionExpression.evaluate(appFormsOptionsElement).get(0).getValue();
        final String descString = descExpression.evaluate(appFormsOptionsElement).get(0).getValue();
        final String startString = startExpression.evaluate(appFormsOptionsElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(appFormsOptionsElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        String sort = sortExpression.evaluate(appFormsOptionsElement).get(0).getValue();
        boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        Element response = new Element("AppFormsOptionsResponse",namespace);

        Collection<FormDefinition> formDefinitionList = null;

        if (sort == null) {
            sort = "name";
            desc = false;
        }

        AppDefinition appDef = appService.getAppDefinition(appId, version);
        formDefinitionList = formDefinitionDao.getFormDefinitionList(null, appDef, sort, desc, start, rows);

        Element item = new Element("item",namespace);
        item.addContent(new Element("value",namespace).setText(""));
        item.addContent(new Element("label",namespace).setText(""));
        response.addContent(item);
        for (FormDefinition formDef : formDefinitionList) {
            Element item2 = new Element("item",namespace);
            item2.addContent(new Element("value",namespace).setText(formDef.getId()));
            item2.addContent(new Element("label",namespace).setText(formDef.getName()));
            response.addContent(item2);
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppPackageDeployRequest")
    public @ResponsePayload Element handleAppPackageDeploy(@RequestPayload Element appPackageDeployElement) {
        final String appId = appIdExpression.evaluate(appPackageDeployElement).get(0).getValue();
        final String version = versionExpression.evaluate(appPackageDeployElement).get(0).getValue();
        Element attachmentElement = packageXpdlExpression.evaluateFirst(appPackageDeployElement);
        Element response = new Element("AppPackageDeployResponse",namespace);
//        final Map<String, String> attachment = new HashMap<>();
        String xpdl = "";
        List<Element> xpdlElementList = attachmentElement.getChildren("XPDL_File", namespace);
        List<Element> rowElementList = attachmentElement.getChildren("row", namespace);

        if(xpdlElementList != null && rowElementList != null) {
            for (int i = 0, size = xpdlElementList.size(); i < size; i++) {
                if (xpdlElementList.get(i) != null && rowElementList.get(i) != null && rowElementList.get(i).getChildText("Line", namespace) != null)
//                        attachment.put(xpdlElementList.get(i).getText(), rowElementList.get(i).getChildText("Line", namespace));
                    xpdl = rowElementList.get(i).getChildText("Line", namespace);
            }
        }

        String error = null;

        appService.getAppDefinition(appId, version);
        // TODO: authenticate user
        boolean authenticated = !workflowUserManager.isCurrentUserAnonymous();

        if (authenticated) {
            if (error == null) {
                try {
                    // deploy package
                    appService.deployWorkflowPackage(appId, version, hexStringToByteArray(xpdl), true);
                    response.addContent(new Element("status",namespace).setText("complete"));
                } catch (Exception e) {
                    response.addContent(new Element("errorMsg",namespace).setText(e.getMessage().replace(":", "")));
                }
            } else {
                response.addContent(new Element("errorMsg",namespace).setText(error));
            }
        } else {
            response.addContent(new Element("errorMsg",namespace).setText("unauthenticated"));
        }
        return response;
    }

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppPackageXpdlRequest")
    public @ResponsePayload Element handleAppPackageXpdl(@RequestPayload Element appPackageXpdlElement) throws IOException {
        final String appId = appIdExpression.evaluate(appPackageXpdlElement).get(0).getValue();
        final String version = versionExpression.evaluate(appPackageXpdlElement).get(0).getValue();
        AppDefinition appDef = appService.getAppDefinition(appId, version);
        Element response = new Element("AppPackageXpdlResponse",namespace);
        if (appDef == null) {
            response.addContent(new Element("error","App Definition not found"));
            return response;
        }
        PackageDefinition packageDef = appDef.getPackageDefinition();
        if (packageDef != null) {
            byte[] content = workflowManager.getPackageContent(packageDef.getId(), packageDef.getVersion().toString());
            String xpdl = new String(content, "UTF-8");
            response.addContent(xpdl);
        } else {
            // read default xpdl
            InputStream input = null;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                // get resource input stream
                String url = "/org/joget/apps/app/model/default.xpdl";
                input = pluginManager.getPluginResource(DefaultFormBinder.class.getName(), url);
                if (input != null) {
                    // write output
                    byte[] bbuf = new byte[65536];
                    int length = 0;
                    while ((input != null) && ((length = input.read(bbuf)) != -1)) {
                        out.write(bbuf, 0, length);
                    }
                    // form xpdl
                    String xpdl = new String(out.toByteArray(), "UTF-8");

                    // replace package ID and name
                    xpdl = xpdl.replace("${packageId}", appId);
                    xpdl = xpdl.replace("${packageName}", appDef.getName());
                    response.addContent(xpdl);
                }
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        }
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppPluginDefaultListRequest")
    public @ResponsePayload Element handleAppPluginDefaultList(@RequestPayload Element appPluginDefaultListElement) {
        final String appId = appIdExpression.evaluate(appPluginDefaultListElement).get(0).getValue();
        final String version = versionExpression.evaluate(appPluginDefaultListElement).get(0).getValue();
        final String filterString = filterExpression.evaluate(appPluginDefaultListElement).get(0).getValue();
        final String descString = descExpression.evaluate(appPluginDefaultListElement).get(0).getValue();
        final String startString = startExpression.evaluate(appPluginDefaultListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(appPluginDefaultListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        String sort = sortExpression.evaluate(appPluginDefaultListElement).get(0).getValue();
        boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        Element response = new Element("AppPluginDefaultListResponse",namespace);

        AppDefinition appDef = appService.getAppDefinition(appId, version);

        Collection<PluginDefaultProperties> pluginDefaultPropertiesList = null;
        Long count = null;

        if (appDef != null) {
            pluginDefaultPropertiesList = pluginDefaultPropertiesDao.getPluginDefaultPropertiesList(filterString, appDef, sort, desc, start, rows);
            count = pluginDefaultPropertiesDao.getPluginDefaultPropertiesListCount(filterString, appDef);
        }

        Element data = new Element("data",namespace);
        if (pluginDefaultPropertiesList != null && pluginDefaultPropertiesList.size() > 0) {
            for (PluginDefaultProperties pluginDefaultProperties : pluginDefaultPropertiesList) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(pluginDefaultProperties.getId()));
                Plugin p = pluginManager.getPlugin(pluginDefaultProperties.getId());
                item.addContent(new Element("pluginName",namespace).setText(p.getI18nLabel()));
                item.addContent(new Element("pluginDescription",namespace).setText(p.getI18nDescription()));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(count.toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppMessageListRequest")
    public @ResponsePayload Element handleAppMessageList(@RequestPayload Element appMessageListElement) {
        final String appId = appIdExpression.evaluate(appMessageListElement).get(0).getValue();
        final String version = versionExpression.evaluate(appMessageListElement).get(0).getValue();
        final String filterString = filterExpression.evaluate(appMessageListElement).get(0).getValue();
        final String descString = descExpression.evaluate(appMessageListElement).get(0).getValue();
        final String startString = startExpression.evaluate(appMessageListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(appMessageListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        String sort = sortExpression.evaluate(appMessageListElement).get(0).getValue();
        boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        String locale = localeExpression.evaluate(appMessageListElement).get(0).getValue();
        Element response = new Element("AppMessageListResponse",namespace);

        AppDefinition appDef = appService.getAppDefinition(appId, version);

        Collection<Message> messageList = null;
        Long count = null;

        if (locale != null && locale.trim().isEmpty()) {
            locale = null;
        }

        if (appDef != null) {
            messageList = messageDao.getMessageList(filterString, locale, appDef, sort, desc, start, rows);
            count = messageDao.getMessageListCount(filterString, locale, appDef);
        }
        Element data = new Element("data",namespace);
        if (messageList != null && messageList.size() > 0) {
            for (Message message : messageList) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(message.getId()));
                item.addContent(new Element("messageKey",namespace).setText(message.getMessageKey()));
                item.addContent(new Element("locale",namespace).setText(message.getLocale()));
                item.addContent(new Element("message",namespace).setText(message.getMessage()));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(count.toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppUserviewListRequest")
    public @ResponsePayload Element handleAppUserviewList(@RequestPayload Element appUserviewListElement) {
        final String appId = appIdExpression.evaluate(appUserviewListElement).get(0).getValue();
        final String version = versionExpression.evaluate(appUserviewListElement).get(0).getValue();
        final String filterString = filterExpression.evaluate(appUserviewListElement).get(0).getValue();
        final String descString = descExpression.evaluate(appUserviewListElement).get(0).getValue();
        final String startString = startExpression.evaluate(appUserviewListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(appUserviewListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        String sort = sortExpression.evaluate(appUserviewListElement).get(0).getValue();
        boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        Element response = new Element("AppUserviewListResponse",namespace);

        AppDefinition appDef = appService.getAppDefinition(appId, version);

        Collection<UserviewDefinition> userviewDefinitionList = null;
        Long count = null;

        if (appDef != null) {
            userviewDefinitionList = userviewDefinitionDao.getUserviewDefinitionList(filterString, appDef, sort, desc, start, rows);
            count = userviewDefinitionDao.getUserviewDefinitionListCount(filterString, appDef);
        }
        Element data = new Element("data",namespace);
        if (userviewDefinitionList != null && userviewDefinitionList.size() > 0) {
            for (UserviewDefinition userviewDefinition : userviewDefinitionList) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(userviewDefinition.getId()));
                item.addContent(new Element("name",namespace).setText(userviewDefinition.getName()));
                item.addContent(new Element("description",namespace).setText(userviewDefinition.getDescription()));
                item.addContent(new Element("dateCreated",namespace).setText(TimeZoneUtil.convertToTimeZone(userviewDefinition.getDateCreated(), null, AppUtil.getAppDateFormat())));
                item.addContent(new Element("dateModified",namespace).setText(TimeZoneUtil.convertToTimeZone(userviewDefinition.getDateModified(), null, AppUtil.getAppDateFormat())));
                data.addContent(item);
            }
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(count.toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AppUserviewOptionsRequest")
    public @ResponsePayload Element handleAppUserviewOptions(@RequestPayload Element appUserviewOptionsElement) {
        final String appId = appIdExpression.evaluate(appUserviewOptionsElement).get(0).getValue();
        final String version = versionExpression.evaluate(appUserviewOptionsElement).get(0).getValue();
        final String descString = descExpression.evaluate(appUserviewOptionsElement).get(0).getValue();
        final String startString = startExpression.evaluate(appUserviewOptionsElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(appUserviewOptionsElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        String sort = sortExpression.evaluate(appUserviewOptionsElement).get(0).getValue();
        boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        Element response = new Element("AppUserviewOptionsResponse",namespace);

        Collection<UserviewDefinition> userviewDefinitionList = null;

        if (sort == null) {
            sort = "name";
            desc = false;
        }

        AppDefinition appDef = appService.getAppDefinition(appId, version);
        userviewDefinitionList = userviewDefinitionDao.getUserviewDefinitionList(null, appDef, sort, desc, start, rows);

        Element item = new Element("item",namespace);
        item.addContent(new Element("value",namespace).setText(""));
        item.addContent(new Element("label",namespace).setText(""));
        response.addContent(item);
        for (UserviewDefinition userviewDef : userviewDefinitionList) {
            Element item2 = new Element("item",namespace);
            item2.addContent(new Element("value",namespace).setText(userviewDef.getId()));
            item2.addContent(new Element("label",namespace).setText(userviewDef.getName()));
            response.addContent(item2);
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "WorkflowPackageListRequest")
    public @ResponsePayload Element handleWorkflowPackageList() {
        Element response = new Element("WorkflowPackageListResponse",namespace);
        Collection<WorkflowPackage> packageList = workflowManager.getPackageList();

        Element data = new Element("data",namespace);
        for (WorkflowPackage workflowPackage : packageList) {
            Element item = new Element("item",namespace);
            item.addContent(new Element("packageId",namespace).setText(workflowPackage.getPackageId()));
            item.addContent(new Element("packageName",namespace).setText(workflowPackage.getPackageName()));
            data.addContent(item);
        }
        response.addContent(data);
        return response;
    }

}
