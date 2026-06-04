package jgit.exception;

public class GitOperationException extends Exception {
    public enum ErrorCode {
        REPO_NOT_FOUND,
        INVALID_PARAMS,
        AUTHOR_REQUIRED,
        MESSAGE_REQUIRED,
        NO_FILES,
        GIT_ERROR,
        IO_ERROR,
        INVALID_FORMAT
    }

    private final ErrorCode errorCode;

    public GitOperationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GitOperationException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
