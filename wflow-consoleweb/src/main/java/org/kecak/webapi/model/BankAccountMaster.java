package org.kecak.webapi.model;

public class BankAccountMaster {
    public final static String FORM_DEF_ID = "master_bank_account";

    private String id; // account number

    public BankAccountMaster() {
    }

    public BankAccountMaster(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
