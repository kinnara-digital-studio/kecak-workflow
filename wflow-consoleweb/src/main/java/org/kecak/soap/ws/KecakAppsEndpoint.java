package org.kecak.soap.ws;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.kecak.soap.service.KecakSoapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;


@Endpoint
public class KecakAppsEndpoint {
	private static final String NAMESPACE_URI = "http://kecak.kinnarastudio.com/soap/apps/schemas";
	   private XPathExpression<Element> appIdExpression;
	   private XPathExpression<Element> processDefIdExpression;
	   private XPathExpression<Element> workflowVariableExpression;
	   private KecakSoapService kecakSoapService;
	
	@Autowired
	public KecakAppsEndpoint(KecakSoapService kecakSoapService) {
		this.kecakSoapService = kecakSoapService;
		
		Namespace namespace = Namespace.getNamespace("apps", NAMESPACE_URI);

		XPathFactory xpathFactory = XPathFactory.instance();
		appIdExpression = xpathFactory.compile("//apps:appId", Filters.element(), null, namespace);
		processDefIdExpression = xpathFactory.compile("//apps:processDefId", Filters.element(), null, namespace);
//		workflowVariableExpression = xpathFactory.compile("//apps:workflowVariable", Filters.element(), null, namespace);
		
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessRequest")
	public void handleProcessRequest(@RequestPayload Element processRequest) {
		String processDefId = processDefIdExpression.evaluate(processRequest).get(0).getValue();
		String appId = appIdExpression.evaluate(processRequest).get(0).getValue();
		kecakSoapService.processStart(appId, processDefId, null);
	}
}
