package org.kecak.exception;

public class ApiException extends RuntimeException {
    private int httpErrorCode;

    public ApiException(int errorCode, String message) {
        super(message);
        this.httpErrorCode = errorCode;
    }

    public int getErrorCode() {
        return httpErrorCode;
    }
}
