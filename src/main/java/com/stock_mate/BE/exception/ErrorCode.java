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
    OUT_OF_STOCK(1001, "Not enough quantity in stock. Available: {available}, Requested: {requested}", HttpStatus.BAD_REQUEST),
    FINISH_PRODUCT_NOT_FOUND(1002, "Finish product with ID {fgID} not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1013, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZE(1014, "You do not have permission", HttpStatus.FORBIDDEN);


    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
