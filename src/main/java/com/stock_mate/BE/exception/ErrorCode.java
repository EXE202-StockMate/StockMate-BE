package com.stock_mate.BE.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {

    PASSWORD_LENGTH(999, "Password length must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(999, "Passwords don't match", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK(1001, "Not enough quantity in stock. Available: {available}, Requested: {requested}", HttpStatus.BAD_REQUEST),
    FINISH_PRODUCT_NOT_FOUND(1002, "Finish product with ID {fgID} not found", HttpStatus.NOT_FOUND),
    STOCK_NOT_FOUND(1003, "Stock with ID {stockID} not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1004, "User with ID {userID} not found", HttpStatus.NOT_FOUND),
    CUSTOMER_NOT_FOUND(1005, "Customer with ID {customerID} not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND(1006, "Order not found", HttpStatus.NOT_FOUND),
    RAW_MATERIAL_NOT_FOUND(1007, "Raw material with ID {rmID} not found", HttpStatus.NOT_FOUND),
    MEDIA_NOT_FOUND(1008, "Media not found", HttpStatus.NOT_FOUND),
    ROLE_EXISTS(1009, "Role already exists", HttpStatus.BAD_REQUEST),
    REQUISITION_NOT_FOUND(1010, "Requisition with ID {requisitionID} not found", HttpStatus.NOT_FOUND),
    BOM_ALREADY_EXISTS(1011, "BOM has already existed", HttpStatus.BAD_REQUEST),
    SEMI_FINISH_PRODUCT_NOT_FOUND(1012, "Semi finish product with ID {sfgID} not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1013, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1014, "You do not have permission", HttpStatus.FORBIDDEN),
    PRODUCT_NAME_REQUIRED(1015, "Product name not null", HttpStatus.BAD_REQUEST),
    PRODUCT_CATEGORY_REQUIRED(1016, "Category not null", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(1017, "Invalid Credentials", HttpStatus.UNAUTHORIZED),
    CUSTOMER_EXISTS(1018, "Customer exists", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1019, "Role not found", HttpStatus.NOT_FOUND),
    INVALID_EMAIL_FORMAT(1020, "Email is not correct format", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_FORMAT(1021, "Phone is not correct format", HttpStatus.BAD_REQUEST),
    BOM_NOT_FOUND(1022, "BOM with ID {bomID} not found", HttpStatus.NOT_FOUND),
    INVALID_MATERIAL_TYPE(1023, "Invalid material type: {materialType}", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY(1024, "Quantity must be greater than zero", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(1025, "Invalid order status: {status}", HttpStatus.BAD_REQUEST),
    INVALID_USER_STATUS(1026, "Invalid user status: {status}", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_FOUND(1027, "User with username {username} not found", HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(1028, "Permission with name {name} not found", HttpStatus.NOT_FOUND),
    PERMISSION_EXISTS(1029, "Permission already exists", HttpStatus.BAD_REQUEST);


    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
