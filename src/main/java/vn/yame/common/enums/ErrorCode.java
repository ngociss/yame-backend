package vn.yame.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ========== GENERAL ==========
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERR_000", "Internal server error occurred"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "ERR_001", "Invalid request"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "ERR_002", "Validation failed"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ERR_003", "Unauthorized access"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "ERR_004", "Access forbidden"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "ERR_005", "HTTP method not allowed"),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "ERR_006", "Unsupported media type"),

    // ========== AUTHENTICATION & AUTHORIZATION ==========
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_001", "Invalid email or password"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_002", "Token has expired"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_003", "Invalid token"),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_004", "Invalid refresh token"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "AUTH_005", "Account has been locked"),
    ACCOUNT_NOT_VERIFIED(HttpStatus.FORBIDDEN, "AUTH_006", "Account is not verified"),
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "AUTH_007", "Insufficient permission"),

    // ========== USER ==========
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_002", "User already exists"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_003", "Email already exists"),
    PHONE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_004", "Phone number already exists"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER_005", "Invalid password format"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "USER_006", "Passwords do not match"),
    OLD_PASSWORD_INCORRECT(HttpStatus.BAD_REQUEST, "USER_007", "Old password is incorrect"),

    // ========== ROLE & PERMISSION ==========
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROLE_001", "Role not found"),
    ROLE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ROLE_002", "Role already exists"),
    PERMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "PERM_001", "Permission not found"),
    PERMISSION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PERM_002", "Permission already exists"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RES_001", "Resource not found"),

    // ========== ADDRESS ==========
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "ADDR_001", "Address not found"),
    DEFAULT_ADDRESS_REQUIRED(HttpStatus.BAD_REQUEST, "ADDR_002", "At least one default address is required"),

    // ========== CATEGORY ==========
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CAT_001", "Category not found"),
    CATEGORY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "CAT_002", "Category already exists"),
    CATEGORY_HAS_PRODUCTS(HttpStatus.BAD_REQUEST, "CAT_003", "Cannot delete category with existing products"),
    CATEGORY_SLUG_DUPLICATE(HttpStatus.BAD_REQUEST, "CAT_004", "Category slug already exists"),

    // ========== PRODUCT ==========
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROD_001", "Product not found"),
    PRODUCT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PROD_002", "Product already exists"),
    PRODUCT_SLUG_DUPLICATE(HttpStatus.BAD_REQUEST, "PROD_003", "Product slug already exists"),
    PRODUCT_INACTIVE(HttpStatus.BAD_REQUEST, "PROD_004", "Product is not active"),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "PROD_005", "Product is out of stock"),

    // ========== PRODUCT VARIANT ==========
    VARIANT_NOT_FOUND(HttpStatus.NOT_FOUND, "VAR_001", "Product variant not found"),
    VARIANT_SKU_DUPLICATE(HttpStatus.BAD_REQUEST, "VAR_002", "SKU already exists"),
    VARIANT_INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "VAR_003", "Insufficient stock for variant"),
    VARIANT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "VAR_004", "Variant is out of stock"),

    // ========== SIZE, COLOR, MATERIAL ==========
    SIZE_NOT_FOUND(HttpStatus.NOT_FOUND, "SIZE_001", "Size not found"),
    COLOR_NOT_FOUND(HttpStatus.NOT_FOUND, "COLOR_001", "Color not found"),
    MATERIAL_NOT_FOUND(HttpStatus.NOT_FOUND, "MAT_001", "Material not found"),
    SUPPLIER_NOT_FOUND(HttpStatus.NOT_FOUND, "SUP_001", "Supplier not found"),

    // ========== CART ==========
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_001", "Cart not found"),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_002", "Cart item not found"),
    CART_EMPTY(HttpStatus.BAD_REQUEST, "CART_003", "Cart is empty"),
    CART_ITEM_QUANTITY_INVALID(HttpStatus.BAD_REQUEST, "CART_004", "Invalid cart item quantity"),
    CART_ITEM_EXCEEDS_STOCK(HttpStatus.BAD_REQUEST, "CART_005", "Cart item quantity exceeds available stock"),

    // ========== DISCOUNT ==========
    DISCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "DISC_001", "Discount not found"),
    DISCOUNT_CODE_INVALID(HttpStatus.BAD_REQUEST, "DISC_002", "Invalid discount code"),
    DISCOUNT_EXPIRED(HttpStatus.BAD_REQUEST, "DISC_003", "Discount has expired"),
    DISCOUNT_NOT_STARTED(HttpStatus.BAD_REQUEST, "DISC_004", "Discount has not started yet"),
    DISCOUNT_USAGE_LIMIT_REACHED(HttpStatus.BAD_REQUEST, "DISC_005", "Discount usage limit reached"),
    DISCOUNT_MIN_ORDER_NOT_MET(HttpStatus.BAD_REQUEST, "DISC_006", "Minimum order amount not met for discount"),
    DISCOUNT_ALREADY_APPLIED(HttpStatus.BAD_REQUEST, "DISC_007", "Discount already applied"),

    // ========== ORDER ==========
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD_001", "Order not found"),
    ORDER_ALREADY_PAID(HttpStatus.BAD_REQUEST, "ORD_002", "Order already paid"),
    ORDER_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "ORD_003", "Order already canceled"),
    ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "ORD_004", "Cannot cancel order in current status"),
    ORDER_INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "ORD_005", "Invalid order status transition"),
    ORDER_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD_006", "Order status not found"),
    ORDER_STATUS_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ORD_008", "Order status already exists"),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD_007", "Order item not found"),

    // ========== PAYMENT ==========
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAY_001", "Payment not found"),
    PAYMENT_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "PAY_002", "Payment already completed"),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAY_003", "Payment processing failed"),
    PAYMENT_INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "PAY_004", "Invalid payment amount"),
    PAYMENT_METHOD_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "PAY_005", "Payment method not supported"),

    // ========== SHIPPING ==========
    SHIPPING_METHOD_NOT_FOUND(HttpStatus.NOT_FOUND, "SHIP_001", "Shipping method not found"),
    SHIPPING_ADDRESS_REQUIRED(HttpStatus.BAD_REQUEST, "SHIP_002", "Shipping address is required"),

    // ========== REVIEW ==========
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REV_001", "Review not found"),
    REVIEW_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "REV_002", "Review already exists for this product"),
    REVIEW_UNAUTHORIZED(HttpStatus.FORBIDDEN, "REV_003", "User must purchase product before reviewing"),
    REVIEW_INVALID_RATING(HttpStatus.BAD_REQUEST, "REV_004", "Invalid rating value"),

    // ========== PURCHASE ORDER ==========
    PURCHASE_ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "PO_001", "Purchase order not found"),
    PURCHASE_ORDER_ALREADY_RECEIVED(HttpStatus.BAD_REQUEST, "PO_002", "Purchase order already received"),
    PURCHASE_ORDER_INVALID_STATUS(HttpStatus.BAD_REQUEST, "PO_003", "Invalid purchase order status"),

    // ========== STOCK ==========
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "STOCK_001", "Insufficient stock"),
    STOCK_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "STOCK_002", "Failed to update stock"),
    NEGATIVE_STOCK_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "STOCK_003", "Stock cannot be negative"),

    // ======== AUTHENTICATION ========
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH_008", "Email or password is incorrect");



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}

