package vn.yame.dto.reponse;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"timestamp", "statusCode", "success", "errorCode", "message", "path", "data"})
public class ResponseData<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime timestamp;

    private int statusCode;
    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorCode;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String path;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ResponseData<T> success(int statusCode, boolean success, String message, T data) {
        ResponseData<T> response = new ResponseData<>();
        response.setTimestamp(LocalDateTime.now());
        response.setStatusCode(statusCode);
        response.setSuccess(success);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ResponseData<T> error(int statusCode, String errorCode, String message, String path) {
        ResponseData<T> response = new ResponseData<>();
        response.setTimestamp(LocalDateTime.now());
        response.setStatusCode(statusCode);
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setPath(path);
        return response;
    }

    public static <T> ResponseData<T> error(int statusCode, String errorCode, String message, String path, T data) {
        ResponseData<T> response = new ResponseData<>();
        response.setTimestamp(LocalDateTime.now());
        response.setStatusCode(statusCode);
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setMessage(message);
        response.setPath(path);
        response.setData(data);
        return response;
    }
}
