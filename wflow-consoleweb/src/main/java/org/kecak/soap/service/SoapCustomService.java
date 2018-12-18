package org.kecak.soap.service;

import org.hibernate.loader.custom.Return;
import org.kecak.soap.model.ReturnMessage;
import org.kecak.soap.model.VendorMaster;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * PT Timah only, submit vendor master data
 */
public interface SoapCustomService {

    /**
     * Start a process
     * @param appId application ID
     * @param appVersion application version, use 0 for published version
     * @param processId process ID, the one without #
     * @param workflowVariable
     * @return
     */
    ReturnMessage processStart(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String processId, @Nonnull Map<String, String> workflowVariable);

    /**
     * Submit new vendor master
     * @param appId
     * @param appVersion
     * @param vendor
     */
    ReturnMessage submitVendorMasterData(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull VendorMaster vendor);
}
