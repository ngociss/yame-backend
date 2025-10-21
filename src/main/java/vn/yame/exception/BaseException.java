package vn.yame.exception;

import lombok.Getter;
import vn.yame.common.enums.ErrorCode;

@Getter
public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = null;
    }

    public BaseException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.args = null;
    }

    public BaseException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = args;
    }

    public BaseException(ErrorCode errorCode, String customMessage, Object... args) {
        super(customMessage);
        this.errorCode = errorCode;
        this.args = args;
    }
}

