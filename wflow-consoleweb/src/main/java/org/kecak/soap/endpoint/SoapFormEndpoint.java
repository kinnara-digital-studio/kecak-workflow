package org.kecak.soap.endpoint;

import org.kecak.soap.service.SoapFormService;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.commons.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@Endpoint
public class SoapFormEndpoint {
    private static final String NAMESPACE_URI = "http://kecak.org/soap/form/schemas";
    private final Namespace namespace = Namespace.getNamespace("xfs", NAMESPACE_URI);

    private XPathExpression<Element> appIdExpression;
    private XPathExpression<Element> appVersionExpression;
    private XPathExpression<Element> formDefIdExpression;
    private XPathExpression<Element> fieldsExpression;


    @Autowired
    private SoapFormService soapFormService;

    public SoapFormEndpoint() {
        XPathFactory xpathFactory = XPathFactory.instance();
        try {
            appIdExpression = xpathFactory.compile("//xfs:appId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            appIdExpression = null;
        }

        try {
            appVersionExpression = xpathFactory.compile("//xfs:appVersion", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            appVersionExpression = null;
        }

        try {
            formDefIdExpression = xpathFactory.compile("//xfs:formDefId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            formDefIdExpression = null;
        }

        try {
            fieldsExpression = xpathFactory.compile("//xfs:fields", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            fieldsExpression = null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "FormSubmitRequest")
    public void handleFormSubmitRequest(@RequestPayload Element formSubmitElement) {
        LogUtil.info(getClass().getName(), "Executing SOAP Web Service ["+formSubmitElement.getName()+"]");

        @Nonnull final String appId = appIdExpression.evaluate(formSubmitElement).get(0).getValue();
        @Nonnull final Long appVersion = appVersionExpression == null || appVersionExpression.evaluate(formSubmitElement).get(0) == null ? 0l : Long.parseLong(appVersionExpression.evaluate(formSubmitElement).get(0).getValue());
        @Nonnull final String formDefId = formDefIdExpression.evaluate(formSubmitElement).get(0).getValue();

        @Nonnull final Map<String, String> fields = fieldsExpression.evaluate(formSubmitElement).stream()
                .flatMap(elementFields -> elementFields.getChildren().stream())
                .flatMap(elementMap -> elementMap.getChildren().stream())
                .collect(HashMap::new, (m, e) -> {
                    String key = e.getChildText("key", namespace);
                    String value = e.getChildText("value", namespace);
                    if(key != null && value != null)
                        m.put(key, value);
                }, Map::putAll);

        soapFormService.formSubmit(appId, appVersion, formDefId, fields);
    }
}
