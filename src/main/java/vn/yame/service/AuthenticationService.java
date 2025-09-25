package vn.yame.service;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import vn.yame.dto.reponse.TokenResponse;
import vn.yame.dto.request.ResetPasswordRequest;
import vn.yame.dto.request.SignInRequest;

public interface AuthenticationService {

    TokenResponse authenticate(SignInRequest signInRequest);
    TokenResponse refreshToken(HttpServletRequest request);
    String logout(HttpServletRequest request);
    String forgotPassword (String email);
    String resetPassword (String secretKey);
    String changePassword(ResetPasswordRequest resetPasswordRequest);

}
