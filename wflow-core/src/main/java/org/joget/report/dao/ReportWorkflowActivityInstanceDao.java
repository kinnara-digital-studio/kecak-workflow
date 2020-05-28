package org.joget.report.dao;

import org.joget.report.model.ReportWorkflowActivityInstance;

import java.util.Collection;

public interface ReportWorkflowActivityInstanceDao {

    public boolean saveReportWorkflowActivityInstance(ReportWorkflowActivityInstance reportWorkflowActivityInstance);

    public ReportWorkflowActivityInstance getReportWorkflowActivityInstance(String instanceId);

    public Collection<ReportWorkflowActivityInstance> getReportWorkflowActivityInstanceList(String appId, String appVersion, String processDefId, String activityDefId, String sort, Boolean desc, Integer start, Integer rows);

    public long getReportWorkflowActivityInstanceListSize(String appId, String appVersion, String processDefId, String activityDefId);

    /**
     *
     * You can use activityInstanceCondition by using e.[field]
     *
     * @param appId
     * @param appVersion
     * @param processDefId
     * @param activityInstanceCondition
     * @param arguments
     * @param sort
     * @param desc
     * @param start
     * @param rows
     * @return
     */
    public Collection<ReportWorkflowActivityInstance> getReportWorkflowActivityInstanceList(String appId, String appVersion, String processDefId, String activityInstanceCondition, Object[] arguments, String sort, Boolean desc, Integer start, Integer rows);

    /**
     *
     * You can use activityInstanceCondition by using e.[field]
     *
     * @param appId
     * @param appVersion
     * @param processDefId
     * @param activityInstanceCondition
     * @param arguments
     * @return
     */
    public long getReportWorkflowActivityInstanceListSize(String appId, String appVersion, String processDefId, String activityInstanceCondition, Object[] arguments);
}
