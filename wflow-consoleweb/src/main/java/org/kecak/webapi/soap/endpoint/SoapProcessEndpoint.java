package org.kecak.webapi.soap.endpoint;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.apps.app.service.AppService;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.PagedList;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.WorkflowVariable;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.util.WorkflowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.annotation.Nonnull;
import java.util.*;


@Endpoint
public class SoapProcessEndpoint {
	private static final String NAMESPACE_URI = "http://kecak.org/soap/process/schemas";
	private final Namespace namespace = Namespace.getNamespace("xps", NAMESPACE_URI);

	private XPathExpression<Element> appIdExpression;
	private XPathExpression<Element> appVersionExpression;
	private XPathExpression<Element> processIdExpression;
	private XPathExpression<Element> processDefIdExpression;
	private XPathExpression<Element> workflowVariableExpression;
	private XPathExpression<Element> allVersionExpression;
	private XPathExpression<Element> packageIdExpression;
	private XPathExpression<Element> sortExpression;
	private XPathExpression<Element> descExpression;
	private XPathExpression<Element> startExpression;
	private XPathExpression<Element> rowsExpression;
	private XPathExpression<Element> checkWhiteListExpression;
	private XPathExpression<Element> variableExpression;
	private XPathExpression<Element> valueExpression;

	@Autowired
	private AppService appService;

	@Autowired
    private WorkflowManager workflowManager;

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
			processDefIdExpression = xpathFactory.compile("//xps:processDefId", Filters.element(), null, namespace);
		} catch (NullPointerException e) {
			processDefIdExpression = null;
		}


		try {
			workflowVariableExpression = xpathFactory.compile("//xps:variables", Filters.element(), null, namespace);
		} catch (NullPointerException e) {
			workflowVariableExpression = null;
		}

        try {
            allVersionExpression = xpathFactory.compile("//xps:allVersion", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            allVersionExpression = null;
        }
        try {
            packageIdExpression = xpathFactory.compile("//xps:packageId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            packageIdExpression = null;
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
            checkWhiteListExpression = xpathFactory.compile("//xps:checkWhiteList", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            checkWhiteListExpression = null;
        }
        try {
            variableExpression = xpathFactory.compile("//xps:variable", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
			variableExpression = null;
        }
        try {
            valueExpression = xpathFactory.compile("//xps:value", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
			valueExpression = null;
        }
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessStartRequest")
	public void handleProcessStartAsync(@RequestPayload Element processStartAsyncElement) {
        LogUtil.info(getClass().getName(), "Executing SOAP Web Service : User [" + WorkflowUtil.getCurrentUsername() + "] is executing [" + processStartAsyncElement.getName() + "]");

		final String processId = processIdExpression.evaluate(processStartAsyncElement).get(0).getValue();
		final String appId = appIdExpression.evaluate(processStartAsyncElement).get(0).getValue();
		final Long appVersion = appVersionExpression == null
				|| appVersionExpression.evaluate(processStartAsyncElement) == null
				|| appVersionExpression.evaluate(processStartAsyncElement).get(0) == null
				? 0l : Long.parseLong(appVersionExpression.evaluate(processStartAsyncElement).get(0).getValue());
		@Nonnull final Map<String, String> variables = workflowVariableExpression.evaluate(processStartAsyncElement).stream()
				.flatMap(elementFields -> elementFields.getChildren("map", namespace).stream())
				.flatMap(elementMap -> elementMap.getChildren("item", namespace).stream())
				.collect(HashMap::new, (m, e) -> {
					String key = e.getChildText("key", namespace);
					String value = e.getChildText("value", namespace);

					if(key == null || value == null)
						return;

					m.merge(key, value, (v1, v2) -> String.join(";", v1, v2));

//					m.put(key, value);
				}, Map::putAll);

        String processDefId = appService.getWorkflowProcessForApp(appId, String.valueOf(appVersion), processId).getId();
        WorkflowProcessResult result = workflowManager.processStart(processDefId, variables);
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "OtherRequest")
	public void handleOtherOperation(@RequestPayload Element otherOperation) {
        LogUtil.info(getClass().getName(), "Executing SOAP Web Service : User [" + WorkflowUtil.getCurrentUsername() + "] is executing [" + otherOperation.getName() + "]");

		final String processId = processIdExpression.evaluate(otherOperation).get(0).getValue();
		final String appId = appIdExpression.evaluate(otherOperation).get(0).getValue();
		final Long appVersion = appVersionExpression == null
				|| appVersionExpression.evaluate(otherOperation) == null
				|| appVersionExpression.evaluate(otherOperation).get(0) == null
				? 0L : Long.parseLong(appVersionExpression.evaluate(otherOperation).get(0).getValue());
		LogUtil.info(getClass().getName(), "Other operation appId [" + appId + "] appVersion ["+appVersion+"] processId ["+processId+"]");
	}

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessAbortRequest")
    public @ResponsePayload Element handleProcessAbort(@RequestPayload Element processAbortElement) {
        LogUtil.info(getClass().getName(), "Executing SOAP Web Service : User [" + WorkflowUtil.getCurrentUsername() + "] is executing [" + processAbortElement.getName() + "]");

        final String processId = processIdExpression.evaluate(processAbortElement).get(0).getValue();
        appService.getAppDefinitionForWorkflowProcess(processId);
        boolean aborted = workflowManager.processAbort(processId);
        LogUtil.info(getClass().getName(), "Process " + processId + " aborted: " + aborted);
        Element response = new Element("ProcessAbortResponse",namespace);
        response.addContent(new Element("processId",namespace).setText(processId));
        response.addContent(new Element("status",namespace).setText("aborted"));
        return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessLatestRequest")
	public @ResponsePayload Element handleProcessLatest(@RequestPayload Element processLatestElement) {
		LogUtil.info(getClass().getName(), "Executing SOAP Web Service : User [" + WorkflowUtil.getCurrentUsername() + "] is executing [" + processLatestElement.getName() + "]");

		final String processDefId = processDefIdExpression.evaluate(processLatestElement).get(0).getValue();
		String id = workflowManager.getConvertedLatestProcessDefId(processDefId.replaceAll(":", "#").replaceAll("#[0-9]+#", "#latest#"));

		Element response = new Element("ProcessLatestResponse",namespace);
		response.addContent(new Element("id",namespace).setText(id));
		response.addContent(new Element("encodedId",namespace).setText(id.replaceAll("#", ":")));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessListRequest")
	public @ResponsePayload Element handleProcessList(@RequestPayload Element processListElement) {
	    final String allVersion = allVersionExpression.evaluate(processListElement).get(0).getValue();
	    final String sort = sortExpression.evaluate(processListElement).get(0).getValue();
	    final String descString = descExpression.evaluate(processListElement).get(0).getValue();
	    final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
	    final String startString = startExpression.evaluate(processListElement).get(0).getValue();
	    final int start = (startString == null)?null:Integer.parseInt(startString);
	    final String rowsString =  rowsExpression.evaluate(processListElement).get(0).getValue();
	    final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
	    final String packageId = packageIdExpression.evaluate(processListElement).get(0).getValue();
	    final String checkWhiteListString = checkWhiteListExpression.evaluate(processListElement).get(0).getValue();
	    final boolean checkWhiteList = (checkWhiteListString == null)?null:Boolean.parseBoolean(checkWhiteListString);
        PagedList<WorkflowProcess> processList = null;

        if (allVersion != null && allVersion.equals("yes")) {
            processList = workflowManager.getProcessList(sort, desc, start, rows, packageId, true, checkWhiteList);
        } else {
            processList = workflowManager.getProcessList(sort, desc, start, rows, packageId, false, checkWhiteList);
        }
        Integer total = processList.getTotal();
        Element response = new Element("ProcessListResponse",namespace);
        Element data = new Element("data",namespace);
        for (WorkflowProcess process : processList) {
            String label = process.getName() + " ver " + process.getVersion();
            Element item = new Element("item",namespace);
            item.addContent(new Element("id",namespace).setText(process.getId()));
            item.addContent(new Element("packageId",namespace).setText(process.getPackageId()));
            item.addContent(new Element("packageName",namespace).setText(process.getPackageName()));
            item.addContent(new Element("name",namespace).setText(process.getName()));
            item.addContent(new Element("version",namespace).setText(process.getVersion()));
            item.addContent(new Element("label",namespace).setText(label));
            data.addContent(item);
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(total.toString()));
        response.addContent(new Element("start",namespace).setText(String.valueOf(start)));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(String.valueOf(desc)));
        return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessListPackageRequest")
	public @ResponsePayload Element handleProcessListPackage(@RequestPayload Element processListPackageElement) {
	    final String checkWhiteListString = checkWhiteListExpression.evaluate(processListPackageElement).get(0).getValue();
	    final boolean checkWhiteList = (checkWhiteListString == null)?null:Boolean.parseBoolean(checkWhiteListString);

		PagedList<WorkflowProcess> processList = workflowManager.getProcessList(null, null, null, null, null, null, checkWhiteList);

		Element response = new Element("ProcessListPackageResponse",namespace);
		Element dataElement = new Element("data",namespace);
		Map<String, Map> processMap = new TreeMap();
		// get process names and totals
		for (WorkflowProcess process : processList) {
			String label = process.getPackageName();
			Map data = (Map) processMap.get(label);
			if (data == null) {
				data = new HashMap();
				data.put("packageId", process.getPackageId());
				data.put("packageName", process.getPackageName());
				data.put("processId", process.getId());
				data.put("processName", process.getName());
				data.put("processVersion", process.getVersion());

				data.put("id", process.getPackageId());
				data.put("label", label);
			}

			Integer count = (Integer) data.get("count");
			if (count == null) {
				count = new Integer(0);
			}
			++count;
			data.put("count", count);
			processMap.put(label, data);
		}

		for (Iterator i = processMap.keySet().iterator(); i.hasNext();) {
			String processName = (String) i.next();
			Map data = (Map) processMap.get(processName);
			Element item = new Element("item",namespace);
			data.forEach((key, value) -> {
				item.addContent(new Element(key.toString(),namespace).setText(value.toString()));
			});
			dataElement.addContent(item);
		}
		response.addContent(dataElement);
        return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessVariableRequest")
	public @ResponsePayload Element handleProcessVariable(@RequestPayload Element processVariableElement) {
		final String processId = processIdExpression.evaluate(processVariableElement).get(0).getValue();
		final String variable = variableExpression.evaluate(processVariableElement).get(0).getValue();
		final String value = valueExpression.evaluate(processVariableElement).get(0).getValue();

		Element response = new Element("ProcessVariableResponse",namespace);
		appService.getAppDefinitionForWorkflowProcess(processId);
		workflowManager.processVariable(processId, variable, value);
		LogUtil.info(getClass().getName(), "Activity variable " + variable + " set to " + value);
		response.addContent(new Element("status",namespace).setText("variableSet"));
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessViewRequest")
	public @ResponsePayload Element handleProcessView(@RequestPayload Element processViewElement) {
		String processDefId = processDefIdExpression.evaluate(processViewElement).get(0).getValue();
		//decode process def id (to default value)
		processDefId = processDefId.replaceAll(":", "#");
		Element response = new Element("ProcessViewResponse",namespace);
		WorkflowProcess process = workflowManager.getProcess(processDefId);
		if (process != null) {
			response.addContent(new Element("processId",namespace).setText(process.getId()));
			response.addContent(new Element("packageId",namespace).setText(process.getPackageId()));
			response.addContent(new Element("packageName",namespace).setText(process.getPackageName()));
			response.addContent(new Element("name",namespace).setText(process.getName()));
			response.addContent(new Element("version",namespace).setText(process.getVersion()));
		}
		return response;
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "ProcessVariableListRequest")
	public @ResponsePayload Element handleProcessVariableList(@RequestPayload Element processVariableListElement) {
		String processId = processIdExpression.evaluate(processVariableListElement).get(0).getValue();
		Element response = new Element("ProcessVariableListResponse",namespace);
		response.addContent(new Element("processId",namespace).setText(processId));
		Collection<WorkflowVariable> variableList = workflowManager.getProcessVariableList(processId);
		for (WorkflowVariable variable : variableList) {
			LogUtil.info(getClass().getName(),variable.getId() + " - " + variable.getVal());
			Element item = new Element("variable",namespace);
			item.addContent(new Element(variable.getId(),namespace).setText(variable.getVal().toString()));
			response.addContent(item);
		}
		return response;
	}
}
