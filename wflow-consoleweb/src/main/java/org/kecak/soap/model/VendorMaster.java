package org.kecak.soap.model;

import java.util.List;

public class VendorMaster {
    public final static String FORM_DEF_ID = "po_master_vendor";
    public final static String FIELD_NAME = "nama_vendor";
    public final static String FIELD_BANK_ACCOUNT = "bank_account";

    private String id;
    private String name;
    private List<BankAccountMaster> bankAccountList;

    public VendorMaster() {}

    public VendorMaster(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BankAccountMaster> getBankAccountList() {
        return bankAccountList;
    }

    public void setBankAccountList(List<BankAccountMaster> bankAccountList) {
        this.bankAccountList = bankAccountList;
    }
}
