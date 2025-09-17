package vn.yame.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import vn.yame.common.enums.Gender;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
public class UserRequest implements Serializable {
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    @NotBlank
    private String fullName;

    private LocalDate birthday;
    private Gender gender;

    @Size(min = 6, message = "Password must be at least 8 characters long")
    private String password;
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;
    private List<AddressRequest> addresses;
}
