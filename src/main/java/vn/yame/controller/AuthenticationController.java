package vn.yame.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.reponse.TokenResponse;
import vn.yame.dto.request.SignInRequest;
import vn.yame.service.AuthenticationService;

@RestController
@RequestMapping("api/v1/auth")
@Validated
@Slf4j
@Tag(name = "Authentication", description = "API xác thực người dùng")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/access")
    @Operation(summary = "User login", description = "Authenticate user and return access token and refresh token")
    public ResponseEntity<ResponseData<TokenResponse>> login (@RequestBody SignInRequest signInRequest) {
        TokenResponse tokenResponse = authenticationService.authenticate(signInRequest);
        return ResponseEntity.ok(ResponseData
                .success(
                        HttpStatus.OK.value(),
                        true,
                        "Login successfully",
                        tokenResponse
                        ));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh access token using refresh token" )
    public ResponseEntity<ResponseData<TokenResponse>> refreshToken (HttpServletRequest request) {
        TokenResponse tokenResponse = authenticationService.refreshToken(request);
        return ResponseEntity.ok(ResponseData
                .success(
                        HttpStatus.OK.value(),
                        true,
                        "Refresh token successfully",
                        tokenResponse
                ));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Invalidate the user's tokens and log them out" )
    public ResponseEntity<ResponseData<String>> logout (HttpServletRequest request) {
        String message = authenticationService.logout(request);
        return ResponseEntity.ok(ResponseData
                .success(
                        HttpStatus.OK.value(),
                        true,
                        message,
                        null
                ));
    }
}
