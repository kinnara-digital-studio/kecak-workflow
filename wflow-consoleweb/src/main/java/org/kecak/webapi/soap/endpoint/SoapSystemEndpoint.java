package org.kecak.webapi.soap.endpoint;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.apps.app.dao.AuditTrailDao;
import org.joget.apps.app.model.AuditTrail;
import org.joget.apps.app.model.HashVariablePlugin;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.spring.model.ResourceBundleMessage;
import org.joget.commons.spring.model.ResourceBundleMessageDao;
import org.joget.commons.util.DynamicDataSourceManager;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.ResourceBundleUtil;
import org.joget.commons.util.TimeZoneUtil;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.io.IOException;
import java.util.*;


@Endpoint
public class SoapSystemEndpoint {
    private static final String NAMESPACE_URI = "http://kecak.org/soap/process/schemas";
    private final Namespace namespace = Namespace.getNamespace("xps", NAMESPACE_URI);

    private XPathExpression<Element> localeExpression;
    private XPathExpression<Element> filterExpression;
    private XPathExpression<Element> sortExpression;
    private XPathExpression<Element> descExpression;
    private XPathExpression<Element> startExpression;
    private XPathExpression<Element> rowsExpression;
    private XPathExpression<Element> datasourceExpression;
    private XPathExpression<Element> driverExpression;
    private XPathExpression<Element> urlExpression;
    private XPathExpression<Element> userExpression;
    private XPathExpression<Element> passwordExpression;

    @Autowired
    private ResourceBundleMessageDao rbmDao;
    @Autowired
    private PluginManager pluginManager;

    public SoapSystemEndpoint() {

        XPathFactory xpathFactory = XPathFactory.instance();
        try {
            localeExpression = xpathFactory.compile("//xps:locale", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            localeExpression = null;
        }

        try {
            filterExpression = xpathFactory.compile("//xps:filter", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            filterExpression = null;
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
            datasourceExpression = xpathFactory.compile("//xps:datasource", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            datasourceExpression = null;
        }
        try {
            driverExpression = xpathFactory.compile("//xps:driver", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            driverExpression = null;
        }
        try {
            urlExpression = xpathFactory.compile("//xps:url", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            urlExpression = null;
        }
        try {
            userExpression = xpathFactory.compile("//xps:user", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            userExpression = null;
        }
        try {
            passwordExpression = xpathFactory.compile("//xps:password", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            passwordExpression = null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "consoleMessageListRequest")
    public @ResponsePayload Element handleconsoleMessageList(@RequestPayload Element consoleMessageListElement) {
        final String filterString = filterExpression.evaluate(consoleMessageListElement).get(0).getValue();
        final String locale = localeExpression.evaluate(consoleMessageListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(consoleMessageListElement).get(0).getValue();
        final String descString = descExpression.evaluate(consoleMessageListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(consoleMessageListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(consoleMessageListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("consoleMessageListResponse",namespace);

        String condition = "";
        List<String> param = new ArrayList<String>();

        if (locale != null && locale.trim().length() != 0) {
            condition += "e.locale = ? ";
            param.add(locale);
        }

        if (filterString != null && filterString.trim().length() != 0) {
            if (!condition.isEmpty()) {
                condition += " and";
            }
            condition += " (e.key like ? or e.message like ?)";
            param.add("%" + filterString + "%");
            param.add("%" + filterString + "%");
        }

        if (condition.length() > 0) {
            condition = "WHERE " + condition;
        }

        List<ResourceBundleMessage> messageList = rbmDao.getMessages(condition, param.toArray(new String[param.size()]), sort, desc, start, rows);
        Long count = rbmDao.count(condition, param.toArray(new String[param.size()]));
        Element data = new Element("data",namespace);
        if (messageList != null && messageList.size() > 0) {
            for (ResourceBundleMessage message : messageList) {
                Element item = new Element("item",namespace);
                item.addContent(new Element("id",namespace).setText(message.getId()));
                item.addContent(new Element("key",namespace).setText(message.getKey()));
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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "hashOptionsRequest")
    public @ResponsePayload Element handlehashOptions() throws IOException {
        Element response = new Element("hashOptionsResponse",namespace);

        try {
            Collection<Plugin> pluginList = pluginManager.list(HashVariablePlugin.class);

            List<String> syntaxs = new ArrayList<String> ();
            for (Plugin p : pluginList) {
                HashVariablePlugin hashVariablePlugin = (HashVariablePlugin) p;
                if (hashVariablePlugin.availableSyntax() != null) {
                    syntaxs.addAll(hashVariablePlugin.availableSyntax());
                }
            }
            Collections.sort(syntaxs);
            for (String key : syntaxs) {
                response.addContent(new Element("item",namespace).setText(key));
            }
        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, "");
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "workflowTestConnectionRequest")
    public @ResponsePayload Element handleworkflowTestConnection(@RequestPayload Element workflowTestConnection) {
        final String datasource = datasourceExpression.evaluate(workflowTestConnection).get(0).getValue();
        final String driver = driverExpression.evaluate(workflowTestConnection).get(0).getValue();
        final String url = urlExpression.evaluate(workflowTestConnection).get(0).getValue();
        final String user = userExpression.evaluate(workflowTestConnection).get(0).getValue();
        String password = passwordExpression.evaluate(workflowTestConnection).get(0).getValue();
        Element response = new Element("workflowTestConnectionResponse",namespace);
        if (DynamicDataSourceManager.SECURE_VALUE.equals(password)) {
            password = DynamicDataSourceManager.getProperty(DynamicDataSourceManager.SECURE_FIELD);
        }

        boolean success = DynamicDataSourceManager.testConnection(driver, url, user, password);
        response.addContent(new Element("datasource",namespace).setText(datasource));
        response.addContent(new Element("success",namespace).setText(String.valueOf(success)));
        return response;
    }

}
