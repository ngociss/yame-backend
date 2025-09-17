package vn.yame.dto.request;

import lombok.Getter;
import vn.yame.common.enums.UserStatus;

import java.io.Serializable;

@Getter
public class UserUpdateRequest extends UserRequest implements Serializable {
    private UserStatus status;
}
