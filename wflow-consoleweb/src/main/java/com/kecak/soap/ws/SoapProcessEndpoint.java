package com.kecak.soap.ws;

import com.kecak.soap.service.SoapProcessesService;
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
public class SoapProcessesEndpoint {
	private static final String NAMESPACE_URI = "http://kecak.kinnarastudio.com/soap/processes/schemas";
	private XPathExpression<Element> appIdExpression;
	private XPathExpression<Element> appVersionExpression;
	private XPathExpression<Element> processDefIdExpression;
	private XPathExpression<Element> workflowVariableExpression;

	private SoapProcessesService soapProcessesService;
	
	@Autowired
	public SoapProcessesEndpoint(SoapProcessesService soapProcessesService) {
		this.soapProcessesService = soapProcessesService;
		
		Namespace namespace = Namespace.getNamespace("p", NAMESPACE_URI);

		XPathFactory xpathFactory = XPathFactory.instance();
		try {
			appIdExpression = xpathFactory.compile("//p:appId", Filters.element(), null, namespace);
		} catch (NullPointerException e) {
			appIdExpression = null;
		}

		try {
			appVersionExpression = xpathFactory.compile("//p:appVersion", Filters.element(), null, namespace);
		} catch (NullPointerException e) {
			appVersionExpression = null;
		}

		try {
			processDefIdExpression = xpathFactory.compile("//p:processDefId", Filters.element(), null, namespace);
		} catch (NullPointerException e) {
			processDefIdExpression = null;
		}


		workflowVariableExpression = null;

//		workflowVariableExpression = xpathFactory.compile("//apps:workflowVariable", Filters.element(), null, namespace);
		
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessStart")
	public void handleProcessStart(@RequestPayload Element processRequest) {
		final String processDefId = processDefIdExpression.evaluate(processRequest).get(0).getValue();
		final String appId = appIdExpression.evaluate(processRequest).get(0).getValue();
		final Long appVersion = appVersionExpression == null || appVersionExpression.evaluate(processRequest).get(0) == null ? 0l : Long.parseLong(appVersionExpression.evaluate(processRequest).get(0).getValue());
		soapProcessesService.processStart(appId, appVersion, processDefId, null);
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "OtherOperation")
	public void handleOtherOperation(@RequestPayload Element processRequest) {
		final String processDefId = processDefIdExpression.evaluate(processRequest).get(0).getValue();
		final String appId = appIdExpression.evaluate(processRequest).get(0).getValue();
		final Long appVersion = appVersionExpression == null || appVersionExpression.evaluate(processRequest).get(0) == null ? 0l : Long.parseLong(appVersionExpression.evaluate(processRequest).get(0).getValue());

		LogUtil.info(getClass().getName(), "Other operation");
	}
}
