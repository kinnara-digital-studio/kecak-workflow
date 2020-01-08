package com.pttimah.kecak.webapi.endpoint;

import com.pttimah.kecak.model.BankAccountMaster;
import com.pttimah.kecak.service.SoapCustomPtTimahService;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.commons.util.LogUtil;
import org.joget.workflow.util.WorkflowUtil;
import com.pttimah.kecak.model.ReturnMessage;
import com.pttimah.kecak.model.VendorMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author aristo
 *
 * Endpoint for PT. Timah Project
 */
@Endpoint
public class SoapCustomPtTimahEndpoint {
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

    private XPathExpression<Element> inputByExpression;
    private XPathExpression<Element> inputDateExpression;
    private XPathExpression<Element> poNumberExpression;
    private XPathExpression<Element> invoiceNumberExpression;
    private XPathExpression<Element> invoiceDateExpression;
    private XPathExpression<Element> bankNameExpression;
    private XPathExpression<Element> jumlahTagihanExpression;
    private XPathExpression<Element> ppnMasukanExpression;
    private XPathExpression<Element> ppnWapuExpression;
    private XPathExpression<Element> hutangWapuExpression;
    private XPathExpression<Element> uangMukaExpression;
    private XPathExpression<Element> pph22Expression;
    private XPathExpression<Element> pph23Expression;
    private XPathExpression<Element> pph21Expression;
    private XPathExpression<Element> jumlahDibayarExpression;
    private XPathExpression<Element> vendor_NumberExpression;
    private XPathExpression<Element> vendor_NameExpression;
    private XPathExpression<Element> attachmentExpression;
    private XPathExpression<Element> documentNumberExpression;
    private XPathExpression<Element> sapDocumentNumberExpression;

    @Autowired
    private SoapCustomPtTimahService soapCustomPtTimahService;

    public SoapCustomPtTimahEndpoint() {
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

        try {
            inputByExpression = xpathFactory.compile("//xs:Input_By", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            inputByExpression = null;
        }

        try {
            inputDateExpression = xpathFactory.compile("//xs:Input_Date", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            inputDateExpression = null;
        }

        try {
            poNumberExpression = xpathFactory.compile("//xs:PO_Number", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            poNumberExpression = null;
        }

        try {
            invoiceNumberExpression = xpathFactory.compile("//xs:Invoice_Number", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            invoiceNumberExpression = null;
        }

        try {
            invoiceDateExpression = xpathFactory.compile("//xs:Invoice_Date", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            invoiceDateExpression = null;
        }

        try {
            bankNameExpression = xpathFactory.compile("//xs:Bank_Name", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            bankNameExpression = null;
        }

        try {
            jumlahTagihanExpression = xpathFactory.compile("//xs:Jumlah_Tagihan", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            jumlahTagihanExpression = null;
        }

        try {
            ppnMasukanExpression = xpathFactory.compile("//xs:PPN_Masukan", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            ppnMasukanExpression = null;
        }

        try {
            ppnWapuExpression = xpathFactory.compile("//xs:PPN_WAPU", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            ppnWapuExpression = null;
        }

        try {
            hutangWapuExpression = xpathFactory.compile("//xs:Hutang_WAPU", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            hutangWapuExpression = null;
        }

        try {
            uangMukaExpression = xpathFactory.compile("//xs:Uang_Muka", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            uangMukaExpression = null;
        }

        try {
            pph22Expression = xpathFactory.compile("//xs:PPH22", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            pph22Expression = null;
        }

        try {
            pph23Expression = xpathFactory.compile("//xs:PPH23", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            pph23Expression = null;
        }

        try {
            pph21Expression = xpathFactory.compile("//xs:PPH21", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            pph21Expression = null;
        }

        try {
            jumlahDibayarExpression = xpathFactory.compile("//xs:Jumlah_Dibayar", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            jumlahDibayarExpression = null;
        }

        try {
            vendor_NumberExpression = xpathFactory.compile("//xs:Vendor_Number", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            vendor_NumberExpression = null;
        }

        try {
            vendor_NameExpression = xpathFactory.compile("//xs:Vendor_Name", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            vendor_NameExpression = null;
        }

        try {
            attachmentExpression = xpathFactory.compile("//xs:Attachment", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            attachmentExpression = null;
        }

        try {
            processIdExpression = xpathFactory.compile("//xs:processId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            processIdExpression = null;
        }

        try {
            documentNumberExpression = xpathFactory.compile("//xs:documentNumber", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            documentNumberExpression = null;
        }

        try {
            sapDocumentNumberExpression = xpathFactory.compile("//xs:sapDocumentNumber", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            sapDocumentNumberExpression = null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "StartSlipRequest")
    public @ResponsePayload Element handleStartSlipRequest(@RequestPayload Element processStartElement) {
        LogUtil.info(getClass().getName(), "Executing SOAP Web Service : User [" + WorkflowUtil.getCurrentUsername() + "] is executing [" + processStartElement.getName() + "]");

        /**
         * Peekaboo... <inputBy></inputBy> contains taksasi/definitif status !!???
         */
        final String keterangan1 = getValue(inputByExpression, processStartElement);

        final String inputDate = getValue(inputDateExpression, processStartElement);
        final String poNumber = getValue(poNumberExpression, processStartElement);
        final String invoiceNumber = getValue(invoiceNumberExpression, processStartElement);
        final String invoiceDate = getValue(invoiceDateExpression, processStartElement);
        final String bankName = getValue(bankNameExpression, processStartElement);
        final String jumlahTagihan = getValue(jumlahTagihanExpression, processStartElement);
        final String ppnMasukan = getValue(ppnMasukanExpression, processStartElement);
        final String ppnWapu = getValue(ppnWapuExpression, processStartElement);
        final String hutangWapu = getValue(hutangWapuExpression, processStartElement);
        final String uangMuka = getValue(uangMukaExpression, processStartElement);
        final String pph22 = getValue(pph22Expression, processStartElement);
        final String pph23 = getValue(pph23Expression, processStartElement);
        final String pph21 = getValue(pph21Expression, processStartElement);
        final String jumlahDibayar = getValue(jumlahDibayarExpression, processStartElement);

        /**
         * Peekaboo... <Vendor_Name></Vendor_Name> contains both vendor number and vendor name delimited by /
         */
        final String vendorNumber = getValue(vendor_NameExpression, processStartElement).replaceAll("/.+$", "");
        final String vendorName = getValue(vendor_NameExpression, processStartElement).replaceAll("[^/]+/", "");

        Element attachmentElement = attachmentExpression.evaluateFirst(processStartElement);
        final Map<String, String> attachment = new HashMap<>();

        if(attachmentElement != null) {
            List<Element> pdfElementList = attachmentElement.getChildren("PDF_File", namespace);
            List<Element> rowElementList = attachmentElement.getChildren("row", namespace);

            if(pdfElementList != null && rowElementList != null) {
                for (int i = 0, size = pdfElementList.size(); i < size; i++) {
                    if (pdfElementList.get(i) != null && rowElementList.get(i) != null && rowElementList.get(i).getChildText("Line", namespace) != null)
                        attachment.put(pdfElementList.get(i).getText(), rowElementList.get(i).getChildText("Line", namespace));
                }
            }
        }

//        final Map<String, String> attachment = (attachment == null ? Stream.<Element>empty() : attachmentList.stream())
//                .collect(HashMap::new, (map, element) -> {
//                    String key = element.getChild("PDF_File", namespace).getText();
//                    String value = element.getChild("row", namespace).getChild("Line", namespace).getText();
//                    map.put(key, value);
//                }, Map::putAll);

        ReturnMessage returnMessage = soapCustomPtTimahService.startSlip(SoapCustomPtTimahService.APP_ID_SLIP, 0L, SoapCustomPtTimahService.PROCESS_ID_SLIP,
                "PO_BIJIH", "", inputDate, poNumber, invoiceNumber, invoiceDate, vendorNumber, vendorName,
                jumlahTagihan, bankName, ppnMasukan, ppnWapu, hutangWapu, uangMuka, pph21, pph22, pph23, jumlahDibayar,
                keterangan1, "", "", "", attachment);

        Element returnElement = new Element("StartSlipResponse", namespace);
        returnElement.addContent(new Element("status", namespace).setText(String.valueOf(returnMessage.getStatus())));
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
        @Nonnull Long appVersion = appVersionExpression == null || appVersionExpression.evaluate(vendorMasterElement).get(0) == null ? 0l : Long.parseLong(appVersionExpression.evaluate(vendorMasterElement).get(0).getValue());

        LogUtil.info(getClass().getName(), "appId ["+appId+"] appVersion ["+appVersion+"]");

        VendorMaster vendorMaster = new VendorMaster();
        vendorMaster.setId(vendorMasterElement.getChild("vendorData", namespace).getChild("vendorNumber", namespace).getText());
        vendorMaster.setName(vendorMasterElement.getChild("vendorData", namespace).getChild("vendorName", namespace).getText());
        vendorMaster.setBankAccountList(vendorMasterElement.getChild("vendorData", namespace).getChild("bankAccount", namespace).getChildren("accountNumber", namespace).stream()
                .map(Element::getText)
                .map(BankAccountMaster::new)
                .collect(Collectors.toList()));

        ReturnMessage returnMessage = soapCustomPtTimahService.submitVendorMasterData(appId, appVersion, vendorMaster);

        Element returnElement = new Element("VendorMasterResponse", namespace);
        returnElement.addContent(new Element("status", namespace).setText(String.valueOf(returnMessage.getStatus())));
        returnElement.addContent(new Element("message1", namespace).setText(returnMessage.getMessage1()));
        returnElement.addContent(new Element("message2", namespace).setText(returnMessage.getMessage2()));
        returnElement.addContent(new Element("message3", namespace).setText(returnMessage.getMessage3()));
        returnElement.addContent(new Element("message4", namespace).setText(returnMessage.getMessage4()));
        returnElement.addContent(new Element("message5", namespace).setText(returnMessage.getMessage5()));

        return returnElement;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SpdSijDocumentNumberRequest")
    public @ResponsePayload Element handleUpdateSapDocumentNumberRequest(@RequestPayload Element updateSapDocumentNumberElement) {
        LogUtil.info(getClass().getName(), "Executing SOAP Web Service : User [" + WorkflowUtil.getCurrentUsername() + "] is executing [" + updateSapDocumentNumberElement.getName() + "]");

        String appId = appIdExpression.evaluateFirst(updateSapDocumentNumberElement).getValue();
        @Nonnull long appVersion = Optional.ofNullable(appVersionExpression)
                .map(x -> x.evaluateFirst(updateSapDocumentNumberElement))
                .map(Element::getValue)
                .map(Long::parseLong)
                .orElse(0L);

        String documentNumber = documentNumberExpression.evaluateFirst(updateSapDocumentNumberElement).getValue();
        String sapDocumentNumber = sapDocumentNumberExpression.evaluateFirst(updateSapDocumentNumberElement).getValue();

        ReturnMessage returnMessage = soapCustomPtTimahService.submitUpdateSapDocumentNumber(appId, appVersion, documentNumber, sapDocumentNumber);

        Element returnElement = new Element("VendorMasterResponse", namespace);
        returnElement.addContent(new Element("status", namespace).setText(String.valueOf(returnMessage.getStatus())));
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

    protected String getValue(XPathExpression<Element> expression, Element processStartElement) {
        Element element = expression.evaluateFirst(processStartElement);
        if(element == null) {
            LogUtil.warn(getClass().getName(), "Expression ["+expression.getExpression()+"] cannot be found in xml tag ["+processStartElement.getName()+"]");
            return "";
        } else
            return element.getValue();
    }
}
