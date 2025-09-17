package vn.yame.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserCreateRequest extends UserRequest implements Serializable {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;
    private String RoleName;
}
