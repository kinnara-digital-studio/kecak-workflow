package org.kecak.soap.model;

import javax.annotation.Nonnull;

public class ReturnMessage {
    private String status;
    private String message1;
    private String message2;
    private String message3;
    private String message4;
    private String message5;

    public @Nonnull String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public @Nonnull String getMessage1() {
        return message1 == null ? "" : message1;
    }

    public void setMessage1(String message1) {
        this.message1 = message1;
    }

    public @Nonnull String getMessage2() {
        return message2 == null ? "" : message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public @Nonnull String getMessage3() {
        return message3 == null ? "" : message3;
    }

    public void setMessage3(String message3) {
        this.message3 = message3;
    }

    public @Nonnull String getMessage4() {
        return message4 == null ? "" : message4;
    }

    public void setMessage4(String message4) {
        this.message4 = message4;
    }

    public @Nonnull String getMessage5() {
        return message5 == null ? "" : message5;
    }

    public void setMessage5(String message5) {
        this.message5 = message5;
    }
}
