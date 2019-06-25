package org.kecak.webapi.soap.endpoint;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.report.model.ReportRow;
import org.joget.report.service.ReportManager;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.*;


@Endpoint
public class SoapSLAEndpoint {
    private static final String NAMESPACE_URI = "http://kecak.org/soap/process/schemas";
    private final Namespace namespace = Namespace.getNamespace("xps", NAMESPACE_URI);

    private XPathExpression<Element> appIdExpression;
    private XPathExpression<Element> appVersionExpression;
    private XPathExpression<Element> processDefIdExpression;

    @Autowired
    private ReportManager reportManager;

    public SoapSLAEndpoint() {

        XPathFactory xpathFactory = XPathFactory.instance();
        try {
            appIdExpression = xpathFactory.compile("//xps:appId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            appIdExpression = null;
        }

        try {
            appVersionExpression = xpathFactory.compile("//xps:appVersion", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            appVersionExpression = null;
        }

        try {
            processDefIdExpression = xpathFactory.compile("//xps:processDefId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            processDefIdExpression = null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SLAListRequest")
    public @ResponsePayload Element handleSLAList(@RequestPayload Element SLAListElement) {
        final String appId = appIdExpression.evaluate(SLAListElement).get(0).getValue();
        final Long appVersion = appVersionExpression == null
                || appVersionExpression.evaluate(SLAListElement) == null
                || appVersionExpression.evaluate(SLAListElement).get(0) == null
                ? 0l : Long.parseLong(appVersionExpression.evaluate(SLAListElement).get(0).getValue());
        final String processDefId = processDefIdExpression.evaluate(SLAListElement).get(0).getValue();
        Element response = new Element("SLAListResponse",namespace);

        Collection<ReportRow> activitySla = reportManager.getWorkflowActivitySlaReport(appId, String.valueOf(appVersion), processDefId, null, null, null, null);

        Element data = new Element("data",namespace);
        for (ReportRow row : activitySla) {
            Element item = new Element("item",namespace);
            item.addContent(new Element("activityDefId",namespace).setText(row.getId()));
            item.addContent(new Element("activityName",namespace).setText(row.getName()));
            item.addContent(new Element("minDelay",namespace).setText(String.valueOf(row.getMinDelay())));
            item.addContent(new Element("maxDelay",namespace).setText(String.valueOf(row.getMaxDelay())));
            item.addContent(new Element("ratioWithDelay",namespace).setText(String.valueOf(row.getRatioWithDelay())));
            item.addContent(new Element("ratioOnTime",namespace).setText(String.valueOf(row.getRatioOnTime())));
            item.addContent(new Element("serviceLevelMonitor",namespace).setText(WorkflowUtil.getServiceLevelIndicator(row.getRatioOnTime())));
            data.addContent(item);
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(String.valueOf(activitySla.size())));
        return response;
    }

}
