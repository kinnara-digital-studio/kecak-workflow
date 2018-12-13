package org.kecak.soap.endpoint;

import org.kecak.soap.service.SoapProcessService;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Endpoint
public class SoapProcessEndpoint {
	private static final String NAMESPACE_URI = "http://kecak.org/soap/process/schemas";
	private final Namespace namespace = Namespace.getNamespace("xps", NAMESPACE_URI);

	private XPathExpression<Element> appIdExpression;
	private XPathExpression<Element> appVersionExpression;
	private XPathExpression<Element> processIdExpression;
	private XPathExpression<Element> workflowVariableExpression;

	@Autowired
	private SoapProcessService soapProcessService;

	public SoapProcessEndpoint() {

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
			processIdExpression = xpathFactory.compile("//xps:processId", Filters.element(), null, namespace);
		} catch (NullPointerException e) {
			processIdExpression = null;
		}

		try {
			workflowVariableExpression = xpathFactory.compile("//xps:variables", Filters.element(), null, namespace);
		} catch (NullPointerException e) {
			workflowVariableExpression = null;
		}


//		workflowVariableExpression = xpathFactory.compile("//apps:workflowVariable", Filters.element(), null, namespace);
		
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessStartRequest")
	public void handleProcessStart(@RequestPayload Element processStartElement) {
		LogUtil.info(getClass().getName(), "handleProcessStart");
		final String processDefId = processIdExpression.evaluate(processStartElement).get(0).getValue();
		final String appId = appIdExpression.evaluate(processStartElement).get(0).getValue();
		final Long appVersion = appVersionExpression == null || appVersionExpression.evaluate(processStartElement).get(0) == null ? 0l : Long.parseLong(appVersionExpression.evaluate(processStartElement).get(0).getValue());
		@Nonnull final Map<String, String> variables = workflowVariableExpression.evaluate(processStartElement).stream()
				.peek(e -> LogUtil.info(getClass().getName(), "Processing element 1 ["+e.getName()+"]"))
				.flatMap(elementFields -> elementFields.getChildren().stream())
				.peek(e -> LogUtil.info(getClass().getName(), "Processing element 2 ["+e.getName()+"]"))
				.flatMap(elementMap -> elementMap.getChildren().stream())
				.peek(e -> LogUtil.info(getClass().getName(), "Processing element 3 ["+e.getName()+"]"))
				.collect(HashMap::new, (m, e) -> {
					String key = e.getChildText("key", namespace);
					String value = e.getChildText("value", namespace);
					LogUtil.info(getClass().getName(), "key ["+key+"] value ["+value+"]");
					if(key != null && value != null)
						m.put(key, value);
				}, Map::putAll);
		soapProcessService.processStart(appId, appVersion, processDefId, variables);
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "OtherRequest")
	public void handleOtherOperation(@RequestPayload Element otherOperation) {
		LogUtil.info(getClass().getName(), "handleOtherOperation");
		final String processId = processIdExpression.evaluate(otherOperation).get(0).getValue();
		final String appId = appIdExpression.evaluate(otherOperation).get(0).getValue();
		final Long appVersion = appVersionExpression == null || appVersionExpression.evaluate(otherOperation).get(0) == null ? 0l : Long.parseLong(appVersionExpression.evaluate(otherOperation).get(0).getValue());
		LogUtil.info(getClass().getName(), "Other operation appId [" + appId + "] appVersion ["+appVersion+"] processId ["+processId+"]");
	}
}
