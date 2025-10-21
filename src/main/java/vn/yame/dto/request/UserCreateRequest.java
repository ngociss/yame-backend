package vn.yame.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class UserCreateRequest extends UserRequest implements Serializable {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotEmpty(message = "At least one role is required")
    private Set<Long> roleIds;

    @Size(max = 255, message = "Image URL must not exceed 255 characters")
    private String imageUrl;
}
