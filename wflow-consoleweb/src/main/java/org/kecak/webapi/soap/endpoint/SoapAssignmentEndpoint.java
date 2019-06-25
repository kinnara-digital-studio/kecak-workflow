package org.kecak.webapi.soap.endpoint;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.PagedList;
import org.joget.commons.util.TimeZoneUtil;
import org.joget.workflow.model.WorkflowAssignment;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.workflow.model.WorkflowVariable;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.util.WorkflowUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.annotation.Nonnull;
import java.util.*;


@Endpoint
public class SoapAssignmentEndpoint {
    private static final String NAMESPACE_URI = "http://kecak.org/soap/process/schemas";
    private final Namespace namespace = Namespace.getNamespace("xps", NAMESPACE_URI);

    private XPathExpression<Element> packageIdExpresssion;
    private XPathExpression<Element> processIdExpression;
    private XPathExpression<Element> activityIdExpression;
    private XPathExpression<Element> processDefIdExpression;
    private XPathExpression<Element> variablesExpression;
    private XPathExpression<Element> variableExpression;
    private XPathExpression<Element> valueExpression;
    private XPathExpression<Element> sortExpression;
    private XPathExpression<Element> descExpression;
    private XPathExpression<Element> startExpression;
    private XPathExpression<Element> rowsExpression;
    private XPathExpression<Element> checkWhiteListExpression;

    @Autowired
    private AppService appService;
    @Autowired
    private WorkflowManager workflowManager;


    public SoapAssignmentEndpoint() {

        XPathFactory xpathFactory = XPathFactory.instance();
        try {
            packageIdExpresssion = xpathFactory.compile("//xps:packageId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            packageIdExpresssion = null;
        }
        try {
            processIdExpression = xpathFactory.compile("//xps:processId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            processIdExpression = null;
        }
        try {
            activityIdExpression = xpathFactory.compile("//xps:activityId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            activityIdExpression = null;
        }
        try {
            processDefIdExpression = xpathFactory.compile("//xps:processDefId", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            processDefIdExpression = null;
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
            variablesExpression = xpathFactory.compile("//xps:variables", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            variablesExpression = null;
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
        try {
            checkWhiteListExpression = xpathFactory.compile("//xps:checkWhiteList", Filters.element(), null, namespace);
        } catch (NullPointerException e) {
            checkWhiteListExpression = null;
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentAcceptRequest")
    public @ResponsePayload Element handleAssignmentAccept(@RequestPayload Element assignmentAcceptElement) {
        final String activityId = activityIdExpression.evaluate(assignmentAcceptElement).get(0).getValue();
        Element response = new Element("AssignmentAcceptResponse",namespace);
        appService.getAppDefinitionForWorkflowActivity(activityId);
        workflowManager.assignmentAccept(activityId);
        WorkflowAssignment assignment = workflowManager.getAssignment(activityId);
        LogUtil.info(getClass().getName(), "Assignment " + activityId + " accepted");
        response.addContent(new Element("assignment",namespace).setText(assignment.getActivityId()));
        response.addContent(new Element("status",namespace).setText("accepted"));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentCompleteRequest")
    public @ResponsePayload Element handleAssignmentComplete(@RequestPayload Element assignmentCompleteElement) {
        final String activityId = activityIdExpression.evaluate(assignmentCompleteElement).get(0).getValue();
        Element response = new Element("AssignmentCompleteResponse",namespace);
        appService.getAppDefinitionForWorkflowActivity(activityId);
        WorkflowAssignment assignment = workflowManager.getAssignment(activityId);

        String processId = (assignment != null) ? assignment.getProcessId() : "";

        if (assignment != null && !assignment.isAccepted()) {
            workflowManager.assignmentAccept(activityId);
        }

        workflowManager.assignmentComplete(activityId);
        LogUtil.info(getClass().getName(), "Assignment " + activityId + " completed");
        response.addContent(new Element("status",namespace).setText("completed"));
        response.addContent(new Element("processId",namespace).setText(processId));
        response.addContent(new Element("activityId",namespace).setText(activityId));

        // check for auto continuation
        String processDefId = assignment.getProcessDefId();
        String activityDefId = assignment.getActivityDefId();
        String packageId = WorkflowUtil.getProcessDefPackageId(processDefId);
        String packageVersion = WorkflowUtil.getProcessDefVersion(processDefId);
        boolean continueNextAssignment = appService.isActivityAutoContinue(packageId, packageVersion, processDefId, activityDefId);
        if (continueNextAssignment) {
            WorkflowAssignment nextAssignment = workflowManager.getAssignmentByProcess(processId);
            if (nextAssignment != null) {
                response.addContent(new Element("nextActivityId",namespace).setText(nextAssignment.getActivityId()));
            }
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentCompleteWithVariableRequest")
    public @ResponsePayload Element handleAssignmentCompleteWithVariable(@RequestPayload Element assignmentCompleteWithVariableElement) {
        final String activityId = activityIdExpression.evaluate(assignmentCompleteWithVariableElement).get(0).getValue();
        @Nonnull final Map<String, String> variables = variablesExpression.evaluate(assignmentCompleteWithVariableElement).stream()
                .flatMap(elementFields -> elementFields.getChildren("map", namespace).stream())
                .flatMap(elementMap -> elementMap.getChildren("item", namespace).stream())
                .collect(HashMap::new, (m, e) -> {
                    String key = e.getChildText("key", namespace);
                    String value = e.getChildText("value", namespace);

                    if(key == null || value == null)
                        return;

                    m.merge(key, value, (v1, v2) -> String.join(";", v1, v2));
                }, Map::putAll);

        Element response = new Element("AssignmentCompleteWithVariableResponse",namespace);
        appService.getAppDefinitionForWorkflowActivity(activityId);
        WorkflowAssignment assignment = workflowManager.getAssignment(activityId);
        String processId = (assignment != null) ? assignment.getProcessId() : "";

        if (assignment != null && !assignment.isAccepted()) {
            workflowManager.assignmentAccept(activityId);
        }
        workflowManager.assignmentComplete(activityId, variables);
        LogUtil.info(getClass().getName(), "Assignment " + activityId + " completed");
        response.addContent(new Element("assignment",namespace).setText(assignment.getAssigneeId()));
        response.addContent(new Element("status",namespace).setText("completed"));
        response.addContent(new Element("processId",namespace).setText(processId));
        response.addContent(new Element("activityId",namespace).setText(activityId));

        // check for automatic continuation
        String processDefId = assignment.getProcessDefId();
        String activityDefId = assignment.getActivityDefId();
        String packageId = WorkflowUtil.getProcessDefPackageId(processDefId);
        String packageVersion = WorkflowUtil.getProcessDefVersion(processDefId);
        boolean continueNextAssignment = appService.isActivityAutoContinue(packageId, packageVersion, processDefId, activityDefId);
        if (continueNextAssignment) {
            WorkflowAssignment nextAssignment = workflowManager.getAssignmentByProcess(processId);
            if (nextAssignment != null) {
                response.addContent(new Element("nextActivityId",namespace).setText(nextAssignment.getActivityId()));
            }
        }
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentListRequest")
    public @ResponsePayload Element handleAssignmentList(@RequestPayload Element assignmentListElement) {
        final String packageId = packageIdExpresssion.evaluate(assignmentListElement).get(0).getValue();
        final String processDefId = processDefIdExpression.evaluate(assignmentListElement).get(0).getValue();
        final String processId = processIdExpression.evaluate(assignmentListElement).get(0).getValue();
        final String sort = sortExpression.evaluate(assignmentListElement).get(0).getValue();
        final String descString = descExpression.evaluate(assignmentListElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(assignmentListElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(assignmentListElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AssignmentListResponse",namespace);
        PagedList<WorkflowAssignment> assignmentList = workflowManager.getAssignmentPendingAndAcceptedList(packageId, processDefId, processId, sort, desc, start, rows);
        Integer total = assignmentList.getTotal();
        Element data = new Element("data",namespace);

        String format = AppUtil.getAppDateFormat();
        for (WorkflowAssignment assignment : assignmentList) {
            Element item = new Element("item",namespace);
            item.addContent(new Element("processId",namespace).setText(assignment.getProcessId()));
            item.addContent(new Element("activityId",namespace).setText(assignment.getActivityId()));
            item.addContent(new Element("processName",namespace).setText(assignment.getProcessName()));
            item.addContent(new Element("activityName",namespace).setText(assignment.getActivityName()));
            item.addContent(new Element("processVersion",namespace).setText(assignment.getProcessVersion()));
            item.addContent(new Element("dateCreated",namespace).setText(TimeZoneUtil.convertToTimeZone(assignment.getDateCreated(), null, format)));
            item.addContent(new Element("acceptedStatus",namespace).setText(String.valueOf(assignment.isAccepted())));
            item.addContent(new Element("due",namespace).setText(assignment.getDueDate() != null ? TimeZoneUtil.convertToTimeZone(assignment.getDueDate(), null, format) : "-"));
            double serviceLevelMonitor = workflowManager.getServiceLevelMonitorForRunningActivity(assignment.getActivityId());
            item.addContent(new Element("serviceLevelMonitor",namespace).setText( WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor)));
            item.addContent(new Element("id",namespace).setText(assignment.getActivityId()));
            item.addContent(new Element("label",namespace).setText(assignment.getActivityName()));
            item.addContent(new Element("description",namespace).setText(assignment.getDescription()));
            data.addContent(item);
        }

        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(total.toString()));
        response.addContent(new Element("start",namespace).setText(startString));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(descString));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentListCountRequest")
    public @ResponsePayload Element handleAssignmentListCount(@RequestPayload Element assignmentListCountElement) {
        final String packageId = packageIdExpresssion.evaluate(assignmentListCountElement).get(0).getValue();
        final String processDefId = processDefIdExpression.evaluate(assignmentListCountElement).get(0).getValue();
        final String processId = processIdExpression.evaluate(assignmentListCountElement).get(0).getValue();
        Element response = new Element("AssignmentListCountResponse",namespace);
        Integer total = new Integer(workflowManager.getAssignmentSize(packageId, processDefId, processId));
        response.addContent(new Element("total",namespace).setText(total.toString()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentListAcceptedRequest")
    public @ResponsePayload Element handleAssignmentListAccepted(@RequestPayload Element assignmentListAcceptedElement) {
        final String processId = processIdExpression.evaluate(assignmentListAcceptedElement).get(0).getValue();
        final String sort = sortExpression.evaluate(assignmentListAcceptedElement).get(0).getValue();
        final String descString = descExpression.evaluate(assignmentListAcceptedElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(assignmentListAcceptedElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(assignmentListAcceptedElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AssignmentListAcceptedResponse",namespace);
        PagedList<WorkflowAssignment> assignmentList = workflowManager.getAssignmentAcceptedList(processId, sort, desc, start, rows);
        Integer total = assignmentList.getTotal();
        Element data = new Element("data",namespace);
        String format = AppUtil.getAppDateFormat();
        for (WorkflowAssignment assignment : assignmentList) {
            Element item = new Element("item",namespace);
            item.addContent(new Element("processId",namespace).setText(assignment.getProcessId()));
            item.addContent(new Element("activityId",namespace).setText(assignment.getActivityId()));
            item.addContent(new Element("processName",namespace).setText(assignment.getProcessName()));
            item.addContent(new Element("activityName",namespace).setText(assignment.getActivityName()));
            item.addContent(new Element("processVersion",namespace).setText(assignment.getProcessVersion()));
            item.addContent(new Element("dateCreated",namespace).setText(TimeZoneUtil.convertToTimeZone(assignment.getDateCreated(), null, format)));
            item.addContent(new Element("due",namespace).setText(assignment.getDueDate() != null ? TimeZoneUtil.convertToTimeZone(assignment.getDueDate(), null, format) : "-"));
            double serviceLevelMonitor = workflowManager.getServiceLevelMonitorForRunningActivity(assignment.getActivityId());
            item.addContent(new Element("serviceLevelMonitor",namespace).setText( WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor)));
            item.addContent(new Element("id",namespace).setText(assignment.getActivityId()));
            item.addContent(new Element("label",namespace).setText(assignment.getActivityName()));
            item.addContent(new Element("description",namespace).setText(assignment.getDescription()));
            data.addContent(item);
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(total.toString()));
        response.addContent(new Element("start",namespace).setText(startString));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(descString));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentListAcceptedCountRequest")
    public @ResponsePayload Element handleAssignmentListAcceptedCount() {
        Element response = new Element("AssignmentListAcceptedCountResponse",namespace);
        Integer total = new Integer(workflowManager.getAssignmentSize(Boolean.TRUE, null));
        response.addContent(new Element("total",namespace).setText(total.toString()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentListAcceptedProcessRequest")
    public @ResponsePayload Element handleAssignmentListAcceptedProcess(@RequestPayload Element assignmentListAcceptedProcessElement) {
        final String checkWhiteListString = checkWhiteListExpression.evaluate(assignmentListAcceptedProcessElement).get(0).getValue();
        final boolean checkWhiteList = (checkWhiteListString == null)?null:Boolean.parseBoolean(checkWhiteListString);
        Element response = new Element("AssignmentListAcceptedProcessResponse",namespace);
        Collection<WorkflowProcess> processList = workflowManager.getProcessList(null, null, null, null, null, true, checkWhiteList);

        Element dataElement = new Element("data",namespace);
        for (WorkflowProcess process : processList) {
            int size = workflowManager.getAssignmentSize(Boolean.TRUE, process.getId());
            if (size > 0) {
                String label = process.getName() + " ver " + process.getVersion() + " (" + size + ")";
                Element item = new Element("item",namespace);
                item.addContent(new Element("processDefId",namespace).setText(process.getId()));
                item.addContent(new Element("processName",namespace).setText(process.getName()));
                item.addContent(new Element("processVersion",namespace).setText(process.getVersion()));
                item.addContent(new Element("label",namespace).setText(label));
                String url = "/json/workflow/assignment/list/accepted?processId=" + process.getEncodedId();
                item.addContent(new Element("url",namespace).setText(url));
                item.addContent(new Element("count",namespace).setText(new Integer(size).toString()));
                dataElement.addContent(item);
            }
        }
        response.addContent(dataElement);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentListPendingRequest")
    public @ResponsePayload Element handleAssignmentListPending(@RequestPayload Element assignmentListPendingElement) {
        final String processId = processIdExpression.evaluate(assignmentListPendingElement).get(0).getValue();
        final String sort = sortExpression.evaluate(assignmentListPendingElement).get(0).getValue();
        final String descString = descExpression.evaluate(assignmentListPendingElement).get(0).getValue();
        final boolean desc = (descString == null)?null:Boolean.parseBoolean(descString);
        final String startString = startExpression.evaluate(assignmentListPendingElement).get(0).getValue();
        final int start = (startString == null)?null:Integer.parseInt(startString);
        final String rowsString =  rowsExpression.evaluate(assignmentListPendingElement).get(0).getValue();
        final int rows =  (rowsString == null)?null:Integer.parseInt(rowsString);
        Element response = new Element("AssignmentListPendingResponse",namespace);
        PagedList<WorkflowAssignment> assignmentList = workflowManager.getAssignmentPendingList(processId, sort, desc, start, rows);
        Integer total = assignmentList.getTotal();
        Element data = new Element("data",namespace);
        String format = AppUtil.getAppDateFormat();
        for (WorkflowAssignment assignment : assignmentList) {
            Element item = new Element("item",namespace);
            item.addContent(new Element("processId",namespace).setText(assignment.getProcessId()));
            item.addContent(new Element("activityId",namespace).setText(assignment.getActivityId()));
            item.addContent(new Element("processName",namespace).setText(assignment.getProcessName()));
            item.addContent(new Element("activityName",namespace).setText(assignment.getActivityName()));
            item.addContent(new Element("processVersion",namespace).setText(assignment.getProcessVersion()));
            item.addContent(new Element("dateCreated",namespace).setText(TimeZoneUtil.convertToTimeZone(assignment.getDateCreated(), null, format)));
            item.addContent(new Element("due",namespace).setText(assignment.getDueDate() != null ? TimeZoneUtil.convertToTimeZone(assignment.getDueDate(), null, format) : "-"));
            double serviceLevelMonitor = workflowManager.getServiceLevelMonitorForRunningActivity(assignment.getActivityId());
            item.addContent(new Element("serviceLevelMonitor",namespace).setText( WorkflowUtil.getServiceLevelIndicator(serviceLevelMonitor)));
            item.addContent(new Element("id",namespace).setText(assignment.getActivityId()));
            item.addContent(new Element("label",namespace).setText(assignment.getActivityName()));
            item.addContent(new Element("description",namespace).setText(assignment.getDescription()));
            data.addContent(item);
        }
        response.addContent(data);
        response.addContent(new Element("total",namespace).setText(total.toString()));
        response.addContent(new Element("start",namespace).setText(startString));
        response.addContent(new Element("sort",namespace).setText(sort));
        response.addContent(new Element("desc",namespace).setText(descString));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentListPendingCountRequest")
    public @ResponsePayload Element handleAssignmentListPendingCount() {
        Element response = new Element("AssignmentListPendingCountResponse",namespace);
        Integer total = new Integer(workflowManager.getAssignmentSize(Boolean.FALSE, null));
        response.addContent(new Element("total",namespace).setText(total.toString()));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentListPendingProcessRequest")
    public @ResponsePayload Element handleAssignmentListPendingProcess(@RequestPayload Element assignmentListAcceptedProcessElement) {
        final String checkWhiteListString = checkWhiteListExpression.evaluate(assignmentListAcceptedProcessElement).get(0).getValue();
        final boolean checkWhiteList = (checkWhiteListString == null)?null:Boolean.parseBoolean(checkWhiteListString);
        Element response = new Element("AssignmentListPendingProcessResponse",namespace);
        Collection<WorkflowProcess> processList = workflowManager.getProcessList(null, null, null, null, null, true, checkWhiteList);

        Element dataElement = new Element("data",namespace);
        for (WorkflowProcess process : processList) {
            int size = workflowManager.getAssignmentSize(Boolean.FALSE, process.getId());
            if (size > 0) {
                String label = process.getName() + " ver " + process.getVersion() + " (" + size + ")";
                Element item = new Element("item",namespace);
                item.addContent(new Element("processDefId",namespace).setText(process.getId()));
                item.addContent(new Element("processName",namespace).setText(process.getName()));
                item.addContent(new Element("processVersion",namespace).setText(process.getVersion()));
                item.addContent(new Element("label",namespace).setText(label));
                String url = "/json/workflow/assignment/list/pending?processId=" + process.getEncodedId();
                item.addContent(new Element("url",namespace).setText(url));
                item.addContent(new Element("count",namespace).setText(new Integer(size).toString()));
                dataElement.addContent(item);
            }
        }
        response.addContent(dataElement);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentProcessViewRequest")
    public @ResponsePayload Element handleAssignmentProcessView(@RequestPayload Element assignmentProcessViewElement) {
        final String processId = processIdExpression.evaluate(assignmentProcessViewElement).get(0).getValue();
        Element response = new Element("AssignmentProcessViewResponse",namespace);
        WorkflowAssignment assignment = workflowManager.getAssignmentByProcess(processId);
        if (assignment == null) {
            return response.addContent(new Element("error","Assignment not found"));
        }
        response.addContent(new Element("activityId",namespace).setText(assignment.getActivityId()));
        response.addContent(new Element("activityDefId",namespace).setText(assignment.getActivityDefId()));
        response.addContent(new Element("processId",namespace).setText(assignment.getProcessId()));
        response.addContent(new Element("processDefId",namespace).setText(assignment.getProcessDefId()));
        response.addContent(new Element("processVersion",namespace).setText(assignment.getProcessVersion()));
        response.addContent(new Element("processName",namespace).setText(assignment.getProcessName()));
        response.addContent(new Element("activityName",namespace).setText(assignment.getActivityName()));
        response.addContent(new Element("description",namespace).setText(assignment.getDescription()));
        response.addContent(new Element("participant",namespace).setText(assignment.getParticipant()));
        response.addContent(new Element("assigneeId",namespace).setText(assignment.getAssigneeId()));
        response.addContent(new Element("assigneeName",namespace).setText(assignment.getAssigneeName()));
        String format = AppUtil.getAppDateFormat();
        response.addContent(new Element("dateCreated",namespace).setText(TimeZoneUtil.convertToTimeZone(assignment.getDateCreated(), null, format)));
        if (assignment.getDueDate() != null) {
            response.addContent(new Element("dueDate",namespace).setText(TimeZoneUtil.convertToTimeZone(assignment.getDueDate(), null, format)));
        }
        Collection<WorkflowVariable> variableList = workflowManager.getActivityVariableList(assignment.getActivityId());
        Element var = new Element("variable",namespace);
        for (WorkflowVariable variable : variableList) {
            var.addContent(new Element(variable.getId(),namespace).setText(variable.getVal().toString()));
        }
        response.addContent(var);

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentWithdrawRequest")
    public @ResponsePayload Element handleAssignmentWithdraw(@RequestPayload Element assignmentWithdrawElement) {
        final String activityId = activityIdExpression.evaluate(assignmentWithdrawElement).get(0).getValue();
        Element response = new Element("AssignmentWithdrawResponse",namespace);
        appService.getAppDefinitionForWorkflowActivity(activityId);
        workflowManager.assignmentWithdraw(activityId);
        WorkflowAssignment assignment = workflowManager.getAssignment(activityId);
        LogUtil.info(getClass().getName(), "Assignment " + activityId + " withdrawn");
        response.addContent(new Element("assignment",namespace).setText(assignment.getActivityId()));
        response.addContent(new Element("status",namespace).setText("withdrawn"));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentVariableRequest")
    public @ResponsePayload Element handleAssignmentVariable(@RequestPayload Element assignmentVariableElement) {
        final String activityId = activityIdExpression.evaluate(assignmentVariableElement).get(0).getValue();
        final String variable = variableExpression.evaluate(assignmentVariableElement).get(0).getValue();
        final String value = valueExpression.evaluate(assignmentVariableElement).get(0).getValue();
        Element response = new Element("AssignmentVariableResponse",namespace);
        appService.getAppDefinitionForWorkflowActivity(activityId);
        workflowManager.assignmentVariable(activityId, variable, value);
        LogUtil.info(getClass().getName(), "Assignment variable " + variable + " set to " + value);
        response.addContent(new Element("status",namespace).setText("variableSet"));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AssignmentViewRequest")
    public @ResponsePayload Element handleAssignmentView(@RequestPayload Element assignmentViewElement) {
        final String activityId = activityIdExpression.evaluate(assignmentViewElement).get(0).getValue();
        Element response = new Element("AssignmentViewResponse",namespace);
        WorkflowAssignment assignment = workflowManager.getAssignment(activityId);
        if (assignment == null) {
            response.addContent(new Element("error",namespace).setText("Assignment not found"));
        }
        response.addContent(new Element("activityId",namespace).setText(assignment.getActivityId()));
        response.addContent(new Element("activityDefId",namespace).setText(assignment.getActivityDefId()));
        response.addContent(new Element("processId",namespace).setText(assignment.getProcessId()));
        response.addContent(new Element("processDefId",namespace).setText(assignment.getProcessDefId()));
        response.addContent(new Element("processVersion",namespace).setText(assignment.getProcessVersion()));
        response.addContent(new Element("processName",namespace).setText(assignment.getProcessName()));
        response.addContent(new Element("activityName",namespace).setText(assignment.getActivityName()));
        response.addContent(new Element("description",namespace).setText(assignment.getDescription()));
        response.addContent(new Element("participant",namespace).setText(assignment.getParticipant()));
        response.addContent(new Element("assigneeId",namespace).setText(assignment.getAssigneeId()));
        response.addContent(new Element("assigneeName",namespace).setText(assignment.getAssigneeName()));
        String format = AppUtil.getAppDateFormat();
        response.addContent(new Element("dateCreated",namespace).setText(TimeZoneUtil.convertToTimeZone(assignment.getDateCreated(), null, format)));
        if (assignment.getDueDate() != null) {
            response.addContent(new Element("dueDate", namespace).setText(TimeZoneUtil.convertToTimeZone(assignment.getDueDate(), null, format)));
        }
        Element var = new Element("variable",namespace);
        Collection<WorkflowVariable> variableList = workflowManager.getActivityVariableList(activityId);
        for (WorkflowVariable variable : variableList) {
            var.addContent(new Element(variable.getId(),namespace).setText(variable.getVal().toString()));
        }
        return response;
    }

}
