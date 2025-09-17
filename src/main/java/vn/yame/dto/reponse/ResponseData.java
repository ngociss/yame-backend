package vn.yame.dto.reponse;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"statusCode", "success", "error", "message", "data"})
public class ResponseData<T> {
    private int statusCode;
    private boolean success;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static <T> ResponseData<T> success(int statusCode, boolean success, String message, T data) {
        return new ResponseData<>(statusCode, success, null, message, data);
    }
}
