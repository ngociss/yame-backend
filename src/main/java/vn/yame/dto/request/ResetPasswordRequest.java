package vn.yame.dto.request;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ResetPasswordRequest implements Serializable {
    private String secretKey;
    private String newPassword;
    private String confirmPassword;
}
