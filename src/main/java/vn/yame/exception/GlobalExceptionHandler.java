package vn.yame.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.reponse.ResponseData;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseData<Object>> handleBaseException(
            BaseException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ResponseData.error(
                        errorCode.getHttpStatus().value(),
                        errorCode.getCode(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseData<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseData.error(
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.VALIDATION_FAILED.getCode(),
                        ErrorCode.VALIDATION_FAILED.getMessage(),
                        request.getRequestURI(),
                        errors
                ));
    }

    @ExceptionHandler(NotFoundResourcesException.class)
    public ResponseEntity<ResponseData<Object>> handleNotFoundResourcesException(
            NotFoundResourcesException ex, HttpServletRequest request) {
        return handleBaseException(ex, request);
    }

    @ExceptionHandler(ExistingResourcesException.class)
    public ResponseEntity<ResponseData<Object>> handleExistingResourcesException(
            ExistingResourcesException ex, HttpServletRequest request) {
        return handleBaseException(ex, request);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ResponseData<Object>> handleInvalidDataException(
            InvalidDataException ex, HttpServletRequest request) {
        return handleBaseException(ex, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseData<Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseData.error(
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.INVALID_REQUEST.getCode(),
                        "Invalid request body: " + ex.getMostSpecificCause().getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseData<Object>> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ResponseData.error(
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        ErrorCode.METHOD_NOT_ALLOWED.getCode(),
                        "Method " + ex.getMethod() + " is not supported for this endpoint",
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        // Log the exception for debugging
        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseData.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        "An unexpected error occurred. Please contact support.",
                        request.getRequestURI()
                ));
    }
}