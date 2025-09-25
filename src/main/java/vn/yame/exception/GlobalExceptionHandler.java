package vn.yame.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import vn.yame.dto.reponse.ResponseData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    private ResponseEntity<ResponseData<Object>> buildResponse(
            HttpStatus status, String error, String message) {
        ResponseData<Object> res = new ResponseData<>();
        res.setStatusCode(status.value());
        res.setSuccess(false);
        res.setError(error);
        res.setMessage(message);
        return ResponseEntity.status(status).body(res);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseData<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        ResponseData<Object> res = new ResponseData<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setSuccess(false);
        res.setError("Dữ liệu đầu vào không hợp lệ");
        res.setMessage("Validation failed");
        res.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(NotFoundResourcesException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseData<Object>> handleNotFoundResourcesException(NotFoundResourcesException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
    }

    @ExceptionHandler(ExistingResourcesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseData<Object>> handleExistingResourcesException(ExistingResourcesException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Existing Resource", ex.getMessage());
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseData<Object>> handleInvalidDataException(InvalidDataException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Data", ex.getMessage());
    }

//    @ExceptionHandler(NullPointerException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<ResponseData<Object>> handleNullPointerException(NullPointerException ex) {
//        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Data", ex.getMessage());
//    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseData<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ResponseData<Object> res = new ResponseData<>();
        res.setStatusCode(HttpServletResponse.SC_BAD_REQUEST); // 400
        res.setSuccess(false);
        res.setError("Bad Request");
        res.setMessage(ex.getLocalizedMessage());
        return ResponseEntity.badRequest().body(res);
    }




}