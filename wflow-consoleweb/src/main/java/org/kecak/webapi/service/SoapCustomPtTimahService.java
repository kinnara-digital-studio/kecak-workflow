package org.kecak.webapi.service;

import org.kecak.webapi.model.ReturnMessage;
import org.kecak.webapi.model.VendorMaster;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * PT Timah only, submit vendor master data
 */
public interface SoapCustomPtTimahService {
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
     * @param tipeDokumen
     * @param inputBy
     * @param poNumber
     * @param invoiceNumber
     * @param invoiceDate
     * @param vendorNumber
     * @param vendorName
     * @param jumlahTagihan
     * @param bankName
     * @param ppnMasukan
     * @param ppnWapu
     * @param uangMuka
     * @param pph21
     * @param pph22
     * @param pph23
     * @param jumlahDibayar
     * @return
     */
    ReturnMessage startSlip(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull String processId,
                            @Nonnull String tipeDokumen, @Nonnull String inputBy, @Nonnull String inputDate, @Nonnull String poNumber,
                            @Nonnull String invoiceNumber, @Nonnull String invoiceDate, @Nonnull String vendorNumber,
                            @Nonnull String vendorName, @Nonnull String jumlahTagihan, @Nonnull String bankName,
                            @Nonnull String ppnMasukan, @Nonnull String ppnWapu, @Nonnull String hutangPpnWapu, @Nonnull String uangMuka,
                            @Nonnull String pph21, @Nonnull String pph22, @Nonnull String pph23,
                            @Nonnull String jumlahDibayar, @Nonnull String keterangan1, @Nonnull String keterangan2, @Nonnull String keterangan3,
                            @Nonnull String keterangan4, @Nonnull Map<String, String> attachment);
    /**
     * Submit new vendor master
     * @param appId
     * @param appVersion
     * @param vendor
     */
    ReturnMessage submitVendorMasterData(@Nonnull String appId, @Nonnull Long appVersion, @Nonnull VendorMaster vendor);
}
