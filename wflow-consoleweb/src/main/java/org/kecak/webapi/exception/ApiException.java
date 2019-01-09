package org.kecak.webapi.exception;

public class ApiException extends Exception {
    private int httpErrorCode;

    public ApiException(int errorCode, String message) {
        super(message);
        this.httpErrorCode = errorCode;
    }

    public int getErrorCode() {
        return httpErrorCode;
    }
}
