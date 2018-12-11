package com.kecak.soap.ws;

import com.kecak.soap.service.SoapProcessService;
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


@Endpoint
public class SoapProcessEndpoint {
	private static final String NAMESPACE_URI = "http://kecak.kinnarastudio.com/soap/process/schemas";
	private XPathExpression<Element> appIdExpression;
	private XPathExpression<Element> appVersionExpression;
	private XPathExpression<Element> processIdExpression;
	private XPathExpression<Element> workflowVariableExpression;

	private SoapProcessService soapProcessService;
	
	@Autowired
	public SoapProcessEndpoint(SoapProcessService soapProcessService) {
		this.soapProcessService = soapProcessService;
		
		Namespace namespace = Namespace.getNamespace("xp", NAMESPACE_URI);

		XPathFactory xpathFactory = XPathFactory.instance();
		try {
			appIdExpression = xpathFactory.compile("//xp:appId", Filters.element(), null, namespace);
		} catch (NullPointerException e) {
			appIdExpression = null;
		}

		try {
			appVersionExpression = xpathFactory.compile("//xp:appVersion", Filters.element(), null, namespace);
		} catch (NullPointerException e) {
			appVersionExpression = null;
		}

		try {
			processIdExpression = xpathFactory.compile("//xp:processId", Filters.element(), null, namespace);
		} catch (NullPointerException e) {
			processIdExpression = null;
		}


		workflowVariableExpression = null;

//		workflowVariableExpression = xpathFactory.compile("//apps:workflowVariable", Filters.element(), null, namespace);
		
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessStartRequest")
	public void handleProcessStart(@RequestPayload Element processStart) {
		final String processDefId = processIdExpression.evaluate(processStart).get(0).getValue();
		final String appId = appIdExpression.evaluate(processStart).get(0).getValue();
		final Long appVersion = appVersionExpression == null || appVersionExpression.evaluate(processStart).get(0) == null ? 0l : Long.parseLong(appVersionExpression.evaluate(processStart).get(0).getValue());
		soapProcessService.processStart(appId, appVersion, processDefId, null);
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "OtherRequest")
	public void handleOtherOperation(@RequestPayload Element otherOperation) {
		final String processId = processIdExpression.evaluate(otherOperation).get(0).getValue();
		final String appId = appIdExpression.evaluate(otherOperation).get(0).getValue();
		final Long appVersion = appVersionExpression == null || appVersionExpression.evaluate(otherOperation).get(0) == null ? 0l : Long.parseLong(appVersionExpression.evaluate(otherOperation).get(0).getValue());

		LogUtil.info(getClass().getName(), "Other operation appId [" + appId + "] appVersion ["+appVersion+"] processId ["+processId+"]");
	}
}
