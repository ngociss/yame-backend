package vn.yame.exception;

import vn.yame.common.enums.ErrorCode;

public class NotFoundResourcesException extends BaseException {
    public NotFoundResourcesException(String message) {
        super(ErrorCode.PRODUCT_NOT_FOUND, message);
    }
    
    public NotFoundResourcesException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundResourcesException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
