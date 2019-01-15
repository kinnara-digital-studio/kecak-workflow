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
    String APP_ID_SLIP = "tap_slip_spm";
    String PROCESS_ID_SLIP = "slip";

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
     * Start Slip Process
     * @param appId
     * @param appVersion
     * @param processId
     * @param status
     * @param tipeDokumen
     * @param primaryKey
     * @param nomorDokumen
     * @param tanggalDokumen
     * @param catatan
     * @param urlFrontApp
     * @param frontAppProcessId
     * @param refNumber
     * @param jumlahDibayar
     * @param nomorVendor
     * @param nik
     * @return
     */
    ReturnMessage startSlip(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String processId,
                            @Nonnull String status, @Nonnull String tipeDokumen, @Nonnull String primaryKey,
                            @Nonnull String nomorDokumen, @Nonnull String tanggalDokumen, @Nonnull String catatan,
                            @Nonnull String urlFrontApp, @Nonnull String frontAppProcessId, @Nonnull String refNumber,
                            @Nonnull String jumlahDibayar, @Nonnull String nomorVendor, @Nonnull String nik);
    /**
     * Submit new vendor master
     * @param appId
     * @param appVersion
     * @param vendor
     */
    ReturnMessage submitVendorMasterData(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull VendorMaster vendor);
}
