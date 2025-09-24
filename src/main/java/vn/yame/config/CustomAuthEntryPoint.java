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
import vn.yame.dto.reponse.ResponseData;

import java.io.IOException;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        String message;
        String error;

        if (authException instanceof AccountExpiredException) {
            error = "Account Expired";
            message = "User account has expired";
        } else if (authException instanceof DisabledException) {
            error = "Account Disabled";
            message = "User account is disabled";
        } else if (authException instanceof LockedException) {
            error = "Account Locked";
            message = "User account is locked";
        } else if (authException instanceof BadCredentialsException) {
            error = "Bad Credentials";
            message = "Invalid email or password";
        } else {
            error = "Unauthorized";
            message = authException.getMessage();
        }

        ResponseData<Object> res = new ResponseData<>();
        res.setStatusCode(HttpServletResponse.SC_FORBIDDEN);
        res.setSuccess(false);
        res.setError(error);
        res.setMessage(message);

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(res));
    }
}

