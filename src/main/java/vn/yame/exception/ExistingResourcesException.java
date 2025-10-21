package vn.yame.exception;

import vn.yame.common.enums.ErrorCode;

public class ExistingResourcesException extends BaseException {
    public ExistingResourcesException(String message) {
        super(ErrorCode.PRODUCT_ALREADY_EXISTS, message);
    }

    public ExistingResourcesException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ExistingResourcesException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
