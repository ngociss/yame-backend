package vn.yame.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SignInRequest implements Serializable {

    @NotBlank(message = "Email not be blank")
    String email;
    @NotBlank(message = "Password not be blank")
    String password;
}
