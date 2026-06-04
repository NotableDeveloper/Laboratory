package jgit.protocol;

import jgit.exception.GitOperationException.ErrorCode;

public class ResponseMessage {
    private final boolean success;
    private final String primaryData;
    private final String secondaryData;
    private final ErrorCode errorCode;

    private ResponseMessage(boolean success, String primaryData, String secondaryData, ErrorCode errorCode) {
        this.success = success;
        this.primaryData = primaryData;
        this.secondaryData = secondaryData;
        this.errorCode = errorCode;
    }

    public static ResponseMessage success(String primaryData) {
        return new ResponseMessage(true, primaryData, null, null);
    }

    public static ResponseMessage success(String primaryData, String secondaryData) {
        return new ResponseMessage(true, primaryData, secondaryData, null);
    }

    public static ResponseMessage error(ErrorCode errorCode, String message) {
        return new ResponseMessage(false, null, message, errorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getPrimaryData() {
        return primaryData;
    }

    public String getSecondaryData() {
        return secondaryData;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String toProtocolString() {
        if (success) {
            if (secondaryData != null) {
                return "SUCCESS|" + primaryData + "|" + secondaryData;
            }
            return "SUCCESS|" + primaryData;
        } else {
            return "ERROR|" + errorCode.toString() + "|" + secondaryData;
        }
    }
}
