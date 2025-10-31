package com.stock_mate.BE.exception;

import com.stock_mate.BE.dto.response.ResponseObject;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;
@Slf4j
@ControllerAdvice
public class GlobalHandlingException {
    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ResponseObject> HandlingRuntimeException(RuntimeException exception) {
        return ResponseEntity.badRequest().body(ResponseObject.builder()
                .status(1000)
                .message(exception.getMessage())
                .build());
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ResponseObject> HandlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        String message = exception.getCustomMessage() != null
                ? exception.getCustomMessage()
                : errorCode.getMessage();

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ResponseObject.builder()
                        .status(errorCode.getCode())
                        .message(message)
                        .build());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ResponseObject> HandlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        return ResponseEntity.status(errorCode.getStatusCode()).body(
                ResponseObject.builder()
                        .status(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    //validation
//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    ResponseEntity<ResponseObject> HandlingValidation(MethodArgumentNotValidException exception) {
//        String enumKey = exception.getFieldError().getDefaultMessage();
//        ErrorCode errorCode = ErrorCode.valueOf(enumKey);
//        return ResponseEntity.badRequest().body(ResponseObject.builder()
//                .status(errorCode.getCode())
//                .message(errorCode.getMessage())
//                .build());
//    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ResponseObject> HandlingValidation(MethodArgumentNotValidException exception) {
        String messageKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = null;
        Map<String, Object> attributes = null;

        try {
            errorCode = ErrorCode.valueOf(messageKey); // Nếu là key enum

            var constraintViolations = exception.getBindingResult()
                    .getAllErrors().get(0).unwrap(ConstraintViolation.class);

            attributes = constraintViolations.getConstraintDescriptor().getAttributes();
            log.info(attributes.toString());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(400)
                    .message(messageKey) // Hiển thị luôn message gốc
                    .build());
        }

        return ResponseEntity.badRequest().body(ResponseObject.builder()
                .status(errorCode.getCode())
                .message(Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage())
                .build());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ResponseObject> handleUnexpectedException(Exception exception) {
        exception.printStackTrace(); // nên dùng log.error() nếu đã cấu hình logging
        ResponseObject responseObject = new ResponseObject();
        responseObject.setStatus(ErrorCode.UNAUTHENTICATED.getCode());
        responseObject.setMessage(ErrorCode.UNAUTHENTICATED.getMessage());

        return ResponseEntity.badRequest().body(responseObject);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseObject> handleInvalidEnum(HttpMessageNotReadableException ex) {

        String message = ex.getMessage();

        // Kiểm tra nếu là lỗi enum
        if (message.contains("MaterialType")) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseObject.builder()
                            .status(ErrorCode.INVALID_MATERIAL_TYPE.getCode())
                            .message("Loại vật tư không hợp lệ. Chỉ chấp nhận: vật tư (RAW_MATERIAL), bán thành phẩm (SEMI_FINISH_PRODUCT), sản phẩm (FINISH_PRODUCT)")
                            .build());
        }

        if(message.contains("OrderStatus")) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseObject.builder()
                            .status(ErrorCode.INVALID_MATERIAL_TYPE.getCode())
                            .message("Trạng thái đơn hàng không hợp lệ.")
                            .build());
        }

        if(message.contains("UserStatus")) {
            return ResponseEntity
                    .badRequest()
                    .body(ResponseObject.builder()
                            .status(ErrorCode.INVALID_USER_STATUS.getCode())
                            .message("Trạng thái người dùng không hợp lệ.")
                            .build());
        }

        return ResponseEntity
                .badRequest()
                .body(ResponseObject.builder()
                        .status(999)
                        .message("Dữ liệu ENUM không hợp lệ")
                        .build());
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE +"}", minValue);
    }
}
