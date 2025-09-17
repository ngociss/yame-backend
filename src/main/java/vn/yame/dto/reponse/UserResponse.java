package vn.yame.dto.reponse;

import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.Gender;
import vn.yame.common.enums.UserStatus;
import vn.yame.model.Address;

import java.util.List;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private UserStatus status;
    private String fullName;
    private String roleName;

}
