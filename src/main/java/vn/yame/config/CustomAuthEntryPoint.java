package vn.yame.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.reponse.ResponseData;

import java.io.IOException;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorCode;
        String message;

        if (authException instanceof AccountExpiredException) {
            errorCode = ErrorCode.ACCOUNT_LOCKED.getCode();
            message = "User account has expired";
        } else if (authException instanceof DisabledException) {
            errorCode = ErrorCode.ACCOUNT_LOCKED.getCode();
            message = "User account is disabled";
        } else if (authException instanceof LockedException) {
            errorCode = ErrorCode.ACCOUNT_LOCKED.getCode();
            message = "User account is locked";
        } else if (authException instanceof BadCredentialsException) {
            errorCode = ErrorCode.INVALID_CREDENTIALS.getCode();
            message = "Invalid email or password";
        } else {
            errorCode = ErrorCode.UNAUTHORIZED.getCode();
            message = authException.getMessage();
        }

        ResponseData<Object> res = ResponseData.error(
                HttpServletResponse.SC_UNAUTHORIZED,
                errorCode,
                message,
                request.getRequestURI()
        );

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        response.getWriter().write(mapper.writeValueAsString(res));
    }
}
