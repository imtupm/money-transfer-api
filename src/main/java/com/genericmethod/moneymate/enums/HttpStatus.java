package com.genericmethod.moneymate.enums;

/**
 * Additional status code that are not available in Response.Status
 */
public enum HttpStatus {

    UNPROCESSABLE_ENTITY(422, "UNPROCESSABLE_ENTITY");

    private final int code;
    private final String reason;

    private HttpStatus(int statusCode, String reasonPhrase) {
        this.code = statusCode;
        this.reason = reasonPhrase;
    }

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }
}
