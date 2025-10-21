package vn.yame.exception;

import vn.yame.common.enums.ErrorCode;

public class InvalidDataException extends BaseException {
    public InvalidDataException(String message) {
        super(ErrorCode.INVALID_REQUEST, message);
    }

    public InvalidDataException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidDataException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
