package org.kecak.soap.endpoint;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.util.WorkflowUtil;
import org.kecak.soap.model.BankAccountMaster;
import org.kecak.soap.model.ReturnMessage;
import org.kecak.soap.model.VendorMaster;
import org.kecak.soap.service.SoapCustomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Endpoint
public class SoapCustomEndpoint {
    private static final String NAMESPACE_URI = "http://kecak.org/soap/custom/schemas";

    private final Namespace namespace = Namespace.getNamespace("xs", NAMESPACE_URI);

    private XPathExpression<Element> appIdExpression;
    private XPathExpression<Element> appVersionExpression;
    private XPathExpression<Element> vendorDataExpression;
    private XPathExpression<Element> vendorNumberExpression;
    private XPathExpression<Element> vendorNameExpression;
    private XPathExpression<Element> accountNumberExpression;
    private XPathExpression<Element> processIdExpression;
    private XPathExpression<Element> workflowVariableExpression;

    @Autowired
    private SoapCustomService soapCustomService;

    public SoapCustomEndpoint() {
        XPathFactory xpathFactory = XPathFactory.instance();

        try {
            appIdExpression = xpathFactory.compile("//xs:appId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            appIdExpression = null;
        }

        try {
            appVersionExpression = xpathFactory.compile("//xs:appVersion", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            appVersionExpression = null;
        }

        try {
            vendorDataExpression = xpathFactory.compile("//xs:vendorData", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            vendorDataExpression = null;
        }

        try {
            vendorNumberExpression = xpathFactory.compile("//xs:vendorNumber", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            vendorNumberExpression = null;
        }

        try {
            vendorNameExpression = xpathFactory.compile("//xs:vendorName", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            vendorNameExpression = null;
        }

        try {
            accountNumberExpression = xpathFactory.compile("//xs:accountNumber", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            accountNumberExpression = null;
        }

        try {
            processIdExpression = xpathFactory.compile("//xs:processId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            processIdExpression = null;
        }

        try {
            workflowVariableExpression = xpathFactory.compile("//xs:variables", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            workflowVariableExpression = null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessStartRequest")
    public @ResponsePayload Element handleProcessStartRequest(@RequestPayload Element processStartElement) {
        final String processDefId = processIdExpression.evaluate(processStartElement).get(0).getValue();
        final String appId = appIdExpression.evaluate(processStartElement).get(0).getValue();
        final Long appVersion = appVersionExpression == null || appVersionExpression.evaluate(processStartElement).get(0) == null ? 0l : Long.parseLong(appVersionExpression.evaluate(processStartElement).get(0).getValue());
        @Nonnull final Map<String, String> variables = workflowVariableExpression.evaluate(processStartElement).stream()
                .flatMap(elementFields -> elementFields.getChildren("variable", namespace).stream())
                .collect(HashMap::new, (m, e) -> {
                    String key = e.getChildText("key", namespace);
                    String value = e.getChildText("value", namespace);

                    if(key == null || value == null)
                        return;

                    m.merge(key, value, (v1, v2) -> String.join(";", v1, v2));
                }, Map::putAll);

        ReturnMessage returnMessage = soapCustomService.processStart(appId, appVersion, processDefId, variables);

        Element returnElement = new Element("VendorMasterResponse", namespace);
        returnElement.addContent(new Element("status", namespace).setText(returnMessage.getStatus()));
        returnElement.addContent(new Element("message1", namespace).setText(returnMessage.getMessage1()));
        returnElement.addContent(new Element("message2", namespace).setText(returnMessage.getMessage2()));
        returnElement.addContent(new Element("message3", namespace).setText(returnMessage.getMessage3()));
        returnElement.addContent(new Element("message4", namespace).setText(returnMessage.getMessage4()));
        returnElement.addContent(new Element("message5", namespace).setText(returnMessage.getMessage5()));

        return returnElement;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "VendorMasterRequest")
    public @ResponsePayload Element handleVendorMasterRequest(@RequestPayload Element vendorMasterElement) {
        LogUtil.info(getClass().getName(), "Executing SOAP Web Service : User [" + WorkflowUtil.getCurrentUsername() + "] is executing [" + vendorMasterElement.getName() + "]");

        @Nonnull final String appId = appIdExpression.evaluate(vendorMasterElement).get(0).getValue();
        @Nonnull final Long appVersion = appVersionExpression == null || appVersionExpression.evaluate(vendorMasterElement).get(0) == null ? 0l : Long.parseLong(appVersionExpression.evaluate(vendorMasterElement).get(0).getValue());

        LogUtil.info(getClass().getName(), "appId ["+appId+"] appVersion ["+appVersion+"]");

        VendorMaster vendorMaster = new VendorMaster();
        vendorMaster.setId(vendorMasterElement.getChild("vendorData", namespace).getChild("vendorNumber", namespace).getText());
        vendorMaster.setName(vendorMasterElement.getChild("vendorData", namespace).getChild("vendorName", namespace).getText());
        vendorMaster.setBankAccountList(vendorMasterElement.getChild("vendorData", namespace).getChild("bankAccount", namespace).getChildren("accountNumber", namespace).stream()
                .map(Element::getText)
                .map(BankAccountMaster::new)
                .collect(Collectors.toList()));


        ReturnMessage returnMessage = soapCustomService.submitVendorMasterData(appId, appVersion, vendorMaster);

        Element returnElement = new Element("VendorMasterResponse", namespace);
        returnElement.addContent(new Element("status", namespace).setText(returnMessage.getStatus()));
        returnElement.addContent(new Element("message1", namespace).setText(returnMessage.getMessage1()));
        returnElement.addContent(new Element("message2", namespace).setText(returnMessage.getMessage2()));
        returnElement.addContent(new Element("message3", namespace).setText(returnMessage.getMessage3()));
        returnElement.addContent(new Element("message4", namespace).setText(returnMessage.getMessage4()));
        returnElement.addContent(new Element("message5", namespace).setText(returnMessage.getMessage5()));

        return returnElement;
    }

    protected void addContent(@Nonnull final Element returnElement, @Nonnull String name, @Nonnull String content) {
        Element element = returnElement.addContent(name);
        element.setText(content);
    }
}
