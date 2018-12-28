package org.kecak.webapi.service;

import javax.annotation.Nonnull;
import java.util.Map;

public interface SoapFormService {

    /**
     * @param appId      application ID
     * @param appVersion application version
     * @param formDefId  form ID
     * @param data       form data
     * @return data ID
     */
    String formSubmit(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String formDefId, @Nonnull Map<String, String> data);

    /**
     * Complete form assignment
     *
     * @param appId
     * @param appVersion
     * @param assignmentId
     * @param fieldData
     * @param workflowVariables
     */
    void formAssignmentComplete(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String assignmentId, @Nonnull Map<String, String> fieldData, @Nonnull Map<String, String> workflowVariables);
}
