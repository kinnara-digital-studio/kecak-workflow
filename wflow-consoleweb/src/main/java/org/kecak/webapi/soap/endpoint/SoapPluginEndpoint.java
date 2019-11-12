package org.kecak.webapi.soap.endpoint;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.apps.app.dao.PluginDefaultPropertiesDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.PluginDefaultProperties;
import org.joget.apps.app.service.AppService;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.HiddenPlugin;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.plugin.property.service.PropertyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;


@Endpoint
public class SoapPluginEndpoint {
    private static final String NAMESPACE_URI = "http://kecak.org/soap/process/schemas";
    private final Namespace namespace = Namespace.getNamespace("xps", NAMESPACE_URI);

    private XPathExpression<Element> classNameExpression;
    private XPathExpression<Element> appIdExpression;
    private XPathExpression<Element> versionExpression;
    private XPathExpression<Element> valueExpression;
    private XPathExpression<Element> startExpression;
    private XPathExpression<Element> rowsExpression;

    @Autowired
    PluginManager pluginManager;
    @Autowired
    AppService appService;
    @Autowired
    PluginDefaultPropertiesDao pluginDefaultPropertiesDao;

    public SoapPluginEndpoint() {

        XPathFactory xpathFactory = XPathFactory.instance();
        try {
            classNameExpression = xpathFactory.compile("//xps:className", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            classNameExpression = null;
        }

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
            valueExpression = xpathFactory.compile("//xps:value", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            valueExpression = null;
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
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PluginListRequest")
    public @ResponsePayload Element handlePluginList(@RequestPayload Element pluginListElement) {
        final String className = classNameExpression.evaluate(pluginListElement).get(0).getValue();
        final String startString = startExpression.evaluate(pluginListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(pluginListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("PluginListResponse",namespace);
        Collection<Plugin> pluginList = null;
        try {
            if (className != null && !className.trim().isEmpty()) {
                pluginList = pluginManager.list(Class.forName(className));
            } else {
                pluginList = pluginManager.list();
            }

            Element data = new Element("data",namespace);
            int size = pluginList.size();
            int counter = 0;

            for (Plugin plugin : pluginList) {
                if (counter >= start && counter < start + rows) {
                    Element item = new Element("item",namespace);
                    item.addContent(new Element("id",namespace).setText(ClassUtils.getUserClass(plugin).getName()));
                    item.addContent(new Element("name",namespace).setText(plugin.getI18nLabel()));
                    item.addContent(new Element("description",namespace).setText(plugin.getI18nDescription()));
                    item.addContent(new Element("version",namespace).setText(plugin.getVersion()));
                    data.addContent(item);
                }
                counter++;
            }
            response.addContent(data);
            response.addContent(new Element("total",namespace).setText(String.valueOf(size)));
            response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PluginListOsgiRequest")
    public @ResponsePayload Element handlePluginListOsgi(@RequestPayload Element pluginListOsgiElement) {
        final String className = classNameExpression.evaluate(pluginListOsgiElement).get(0).getValue();
        final String startString = startExpression.evaluate(pluginListOsgiElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(pluginListOsgiElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("PluginListOsgiResponse",namespace);
        Collection<Plugin> pluginList = null;

        try {
            if (className != null && !className.trim().isEmpty()) {
                pluginList = pluginManager.listOsgiPlugin(Class.forName(className));
            } else {
                pluginList = pluginManager.listOsgiPlugin(null);
            }
            Element data = new Element("data",namespace);
            int size = pluginList.size();
            int counter = 0;

            for (Plugin plugin : pluginList) {
                if (counter >= start && counter < start + rows) {
                    Element item = new Element("item",namespace);
                    item.addContent(new Element("id",namespace).setText(ClassUtils.getUserClass(plugin).getName()));
                    item.addContent(new Element("name",namespace).setText(plugin.getI18nLabel()));
                    item.addContent(new Element("description",namespace).setText(plugin.getI18nDescription()));
                    item.addContent(new Element("version",namespace).setText(plugin.getVersion()));
                    data.addContent(item);
                }
                counter++;
            }

            response.addContent(data);
            response.addContent(new Element("total",namespace).setText(String.valueOf(size)));
            response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        } catch (Exception e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PluginElementsRequest")
    public @ResponsePayload Element handlePluginElements(@RequestPayload Element pluginElementsElement) {
        final String className = classNameExpression.evaluate(pluginElementsElement).get(0).getValue();
        Element response = new Element("PluginElementsResponse",namespace);

        try {
            // get available elements from the plugin manager
            Collection<Plugin> elementList = pluginManager.list(Class.forName(className));
            Element empty = new Element("item",namespace);
            empty.addContent(new Element("value",namespace).setText(""));
            empty.addContent(new Element("label",namespace).setText(""));
            response.addContent(empty);

            for (Plugin p : elementList) {
                if (!(p instanceof HiddenPlugin)) {
                    PropertyEditable element = (PropertyEditable) p;
                    Element item = new Element("item",namespace);
                    item.addContent(new Element("value",namespace).setText(element.getClassName()));
                    item.addContent(new Element("label",namespace).setText(p.getI18nLabel()));
                    response.addContent(item);
                }
            }
        } catch (Exception ex) {
            LogUtil.error(this.getClass().getName(), ex, "getElements Error!");
        }

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PluginPropertyOptionsRequest")
    public @ResponsePayload Element handlePluginPropertyOptions(@RequestPayload Element pluginPropertyOptionsElement) {
        final String value = valueExpression.evaluate(pluginPropertyOptionsElement).get(0).getValue();
        Element response = new Element("PluginPropertyOptionsResponse",namespace);

        PropertyEditable element = (PropertyEditable) pluginManager.getPlugin(value);

        if (element != null) {
            response.addContent(element.getPropertyOptions());
        }

        return response;
    }

    private Element jsonToElement(String elementName, String json){
        Element response = new Element(elementName,namespace);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            Map<String, Object> map = mapper.readValue(json, Map.class);
            map.forEach((k,v)->{
                if(v instanceof String) {
                    response.addContent(new Element(k, namespace).setText(v.toString()));
                } else if(v instanceof Map){
                    Element vElement = new Element(k,namespace);
                    ((Map) v).forEach((k2,v2)->{
                        vElement.addContent(new Element(k2.toString(),namespace).setText(v2.toString()));
                    });
                    response.addContent(vElement);
                }
            });
        } catch (IOException e) {
            LogUtil.error(this.getClass().getName(), e, e.getMessage());
        }
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PluginDefaultPropertiesRequest")
    public @ResponsePayload Element handlePluginDefaultProperties(@RequestPayload Element pluginDefaultPropertiesElement) {
        final String appId = appIdExpression.evaluate(pluginDefaultPropertiesElement).get(0).getValue();
        final String version = versionExpression.evaluate(pluginDefaultPropertiesElement).get(0).getValue();
        final String value = valueExpression.evaluate(pluginDefaultPropertiesElement).get(0).getValue();
        Element response = new Element("PluginDefaultPropertiesResponse",namespace);
        String json = "";
        if (appId != null && !appId.trim().isEmpty()) {
            AppDefinition appDef = appService.getAppDefinition(appId, version);

            Plugin plugin = pluginManager.getPlugin(value);
            if (plugin != null) {
                PluginDefaultProperties pluginDefaultProperties = pluginDefaultPropertiesDao.loadById(value, appDef);

                if (pluginDefaultProperties != null) {
                    json = pluginDefaultProperties.getPluginProperties();
                    json = PropertyUtil.propertiesJsonLoadProcessing(json);
                    response = jsonToElement("PluginDefaultPropertiesResponse",json);
                }
            }
        }
//        response.addContent(json);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "PluginPropertyOptionsWithAppDefRequest")
    public @ResponsePayload Element handlePluginPropertyOptionsWithAppDef(@RequestPayload Element pluginPropertyOptionsWithAppDefElement) {
        final String appId = appIdExpression.evaluate(pluginPropertyOptionsWithAppDefElement).get(0).getValue();
        final String version = versionExpression.evaluate(pluginPropertyOptionsWithAppDefElement).get(0).getValue();
        final String value = valueExpression.evaluate(pluginPropertyOptionsWithAppDefElement).get(0).getValue();
        Element response = new Element("PluginPropertyOptionsWithAppDefResponse",namespace);
        if (appId != null && !appId.trim().isEmpty()) {
            appService.getAppDefinition(appId, version);
        }

        String json = "";
        PropertyEditable element = (PropertyEditable) pluginManager.getPlugin(value);
        if (element != null) {
            json = element.getPropertyOptions();
            response = jsonToElement("PluginPropertyOptionsWithAppDefResponse",json);
        }
//        response.addContent(json);
        return response;
    }

}
