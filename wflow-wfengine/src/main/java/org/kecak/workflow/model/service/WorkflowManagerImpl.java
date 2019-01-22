package org.kecak.workflow.model.service;

import org.joget.commons.util.LogUtil;
import org.joget.workflow.model.WorkflowActivity;
import org.joget.workflow.model.WorkflowProcess;
import org.joget.workflow.model.WorkflowProcessResult;
import org.joget.workflow.model.WorkflowVariable;
import org.joget.workflow.util.WorkflowUtil;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class WorkflowManagerImpl extends org.joget.workflow.model.service.WorkflowManagerImpl implements WorkflowManager {
    @Override
    public WorkflowProcessResult assignmentTransfer(@Nonnull String currentProcessId, @Nonnull String targetActivityId) {
        String newProcessDefId = getProcessDefIdByInstanceId(currentProcessId);

        if(newProcessDefId.isEmpty()) {
            LogUtil.warn(getClass().getName(), "Process Definition ["+newProcessDefId+"] not available");
            return null;
        }

        WorkflowProcessResult result = new WorkflowProcessResult();

        try {
            Map<String, Object> activitiesTree = getRunningActivitiesTree(currentProcessId);
            if (activitiesTree != null && !activitiesTree.isEmpty()) {
                Map.Entry<String, Object> currentActivityEntry = activitiesTree.entrySet().iterator().next();

                Map<String, Object> newActivitiesTree = new HashMap<>();
                newActivitiesTree.put(targetActivityId, currentActivityEntry.getValue());

                // get current variable values
                Collection<WorkflowVariable> variableList = getProcessVariableList(currentProcessId);
                Map<String, String> variableMap = new HashMap<>();
                for (WorkflowVariable variable : variableList) {
                    String val = (variable.getVal() != null) ? variable.getVal().toString() : null;
                    variableMap.put(variable.getId(), val);
                }

                String starter = getUserByProcessIdAndActivityDefId(newProcessDefId, currentProcessId, WorkflowUtil.ACTIVITY_DEF_ID_RUN_PROCESS);
                result = processStart(newProcessDefId, null, variableMap, starter, currentProcessId, true);
                WorkflowProcess processStarted = result.getProcess();

                if (processStarted != null) {
                    String newProcessId = processStarted.getInstanceId();
                    result.setActivities(new ArrayList<>());
                    startActivitiesBasedOnActivitiesTree(newProcessId, newActivitiesTree, result);

                    // abort old process if required
                    processAbort(currentProcessId);
                }
            }
        } catch (Exception e) {
            LogUtil.error(WorkflowManagerImpl.class.getName(), e, "Error copy process instance - " + currentProcessId);
        }

        // return process result
        return result;
    }

    @Override
    public WorkflowProcessResult assignmentStepBack(String currentProcessId) {
        return assignmentStepBack(currentProcessId, 1);
    }

    @Override
    public WorkflowProcessResult assignmentStepBack(String currentProcessId, Integer steps) {
        if(steps == 0) {
            LogUtil.warn(getClass().getName(), "Cannot stepping back 0 steps");
            return  null;
        }

        String newProcessDefId = getProcessDefIdByInstanceId(currentProcessId);

        if(newProcessDefId.isEmpty()) {
            LogUtil.warn(getClass().getName(), "Process Definition ["+newProcessDefId+"] not available");
            return null;
        }

        WorkflowProcessResult result = new WorkflowProcessResult();

        try {
            List<WorkflowActivity> activityHistory = getActivityHistory(currentProcessId);
            LogUtil.info(getClass().getName(), "activityHistory ["+activityHistory.stream().map(WorkflowActivity::getActivityDefId).collect(Collectors.joining("||"))+"]");
            if(activityHistory.size() > steps) {

                // get previous activity
                WorkflowActivity previousActivity = activityHistory.get(steps);

                Collection<WorkflowVariable> previousActivityVariableList = getActivityVariableList(previousActivity.getId());

                Map<String, Object> previousActivityData = previousActivityVariableList.stream()
                        .collect(HashMap::new, (m, wfv) -> m.put(wfv.getId(), wfv.getVal()), Map::putAll);

                Map<String, Object> newActivitiesTree = new HashMap<>();
                newActivitiesTree.put(previousActivity.getActivityDefId(), previousActivityData);

                LogUtil.info(getClass().getName(), "newActivitiesTree ["+newActivitiesTree.entrySet().stream().map(e -> "["+e.getKey()+"->"+e.getValue()+"]").collect(Collectors.joining("||"))+"]");

                // get current variable values
                Collection<WorkflowVariable> variableList = getProcessVariableList(currentProcessId);
                Map<String, String> variableMap = new HashMap<>();
                for (WorkflowVariable variable : variableList) {
                    String val = (variable.getVal() != null) ? variable.getVal().toString() : null;
                    variableMap.put(variable.getId(), val);
                }

                String starter = getUserByProcessIdAndActivityDefId(newProcessDefId, currentProcessId, WorkflowUtil.ACTIVITY_DEF_ID_RUN_PROCESS);
                result = processStart(newProcessDefId, null, variableMap, starter, currentProcessId, true);
                WorkflowProcess processStarted = result.getProcess();

                if (processStarted != null) {
                    String newProcessId = processStarted.getInstanceId();
                    result.setActivities(new ArrayList<>());
                    startActivitiesBasedOnActivitiesTree(newProcessId, newActivitiesTree, result);

                    // abort old process
                    processAbort(currentProcessId);
                }
            }
        } catch (Exception e) {
            LogUtil.error(WorkflowManagerImpl.class.getName(), e, "Error stepping back process instance - " + currentProcessId);
        }

        // return process result
        return result;
    }

    protected @Nonnull List<WorkflowActivity> getActivityHistory(String processId) {
        return getWorkflowProcessLinkDao().getLinks(processId).stream()
                .filter(Objects::nonNull)
                .flatMap(l -> getActivityList(l.getProcessId(), null, 1000, null, null).stream())

                // only handle activity
                .filter(activity -> {
                    final WorkflowActivity definition = getProcessActivityDefinition(activity.getProcessDefId(), activity.getActivityDefId());
                    return WorkflowActivity.TYPE_NORMAL.equals(definition.getType());
                })

                .sorted(Comparator.comparing(WorkflowActivity::getCreatedTime))

                .collect(ArrayList::new, (list, activity) -> {
                    WorkflowActivity info = getRunningActivityInfo(activity.getId());
                    WorkflowActivity definition = getProcessActivityDefinition(activity.getProcessDefId(), activity.getActivityDefId());
                    list.add(0, activity);
                }, List::addAll);
    }
}
