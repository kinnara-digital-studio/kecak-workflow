package org.kecak.workflow.model.service;

import org.joget.commons.util.PagedList;
import org.joget.workflow.model.*;
import org.joget.workflow.model.service.WorkflowUserManager;

import java.util.*;

/**
 * Service methods to interact with workflow engine
 * 
 */
public interface WorkflowManager extends org.joget.workflow.model.service.WorkflowManager {
    /**
     * Transfer current assingment to target activity
     * @param currentProcessId
     * @param targetActivity
     * @return
     */
    WorkflowProcessResult assignmentTransfer(String currentProcessId, String targetActivity);

    /**
     * Step back activity in 1 step
     * @param currentProcessId
     * @return
     */
    WorkflowProcessResult assignmentStepBack(String currentProcessId);

    /**
     * Step back activity in N steps
     * @param currentProcessId
     * @param steps
     * @return
     */
    WorkflowProcessResult assignmentStepBack(String currentProcessId, Integer steps);
}
