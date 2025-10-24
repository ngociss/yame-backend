package vn.yame.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.ErrorCode;
import vn.yame.common.enums.TokenType;
import vn.yame.common.enums.UserStatus;
import vn.yame.dto.reponse.TokenResponse;
import vn.yame.dto.reponse.UserResponse;
import vn.yame.dto.request.RegisterRequest;
import vn.yame.dto.request.ResetPasswordRequest;
import vn.yame.dto.request.SignInRequest;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.InvalidDataException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.UserMapper;
import vn.yame.model.Role;
import vn.yame.model.Token;
import vn.yame.model.User;
import vn.yame.repository.RoleRepository;
import vn.yame.repository.UserRepository;
import vn.yame.service.AuthenticationService;
import vn.yame.service.JwtService;
import vn.yame.service.TokenService;

import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        log.info("Registering new user with email: {}", registerRequest.getEmail());

        // Validate password confirmation
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new InvalidDataException(
                ErrorCode.PASSWORD_MISMATCH,
                "Password and confirm password do not match"
            );
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ExistingResourcesException(
                ErrorCode.EMAIL_ALREADY_EXISTS,
                "Email '" + registerRequest.getEmail() + "' is already registered"
            );
        }

        // Create new user
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setStatus(UserStatus.ACTIVE);
        user.setVerified(false);
        // Lấy role mặc định từ database
        Role customerRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new NotFoundResourcesException("Default USER role not found"));
        user.setRoles(new HashSet<>(Set.of(customerRole)));

        User savedUser = userRepository.save(user);

        log.info("User registered successfully with id: {}, email: {}", savedUser.getId(), savedUser.getEmail());

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public TokenResponse authenticate(SignInRequest signInRequest) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInRequest.getEmail(),
                signInRequest.getPassword()
        ));

        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Email or password is incorrect"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Token token = Token.builder()
                .username(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        tokenService.save(token);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .roleName("roleName")
                .build();
    }

    @Override
    public TokenResponse refreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("x-token");
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("Refresh refreshToken is missing");
        }

        // extract username from refreshToken
        String username = jwtService.exTractUsername(refreshToken, TokenType.REFRESH);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user, TokenType.REFRESH)) {
            throw new InvalidDataException("Refresh refreshToken is invalid or expired");
        }

        String accessToken = jwtService.generateToken(user);

        Token token = Token.builder()
                .username(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        tokenService.save(token);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .roleName("roleName")
                .build();
    }

    @Override
    public String logout(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("x-token");
        if (StringUtils.isBlank(accessToken) || !accessToken.startsWith("Bearer ")) {
            throw new InvalidDataException("Access token is missing or invalid");
        }
        accessToken = accessToken.substring("Bearer ".length());
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("Refresh token is missing");
        }

        String username = jwtService.exTractUsername(accessToken, TokenType.ACCESS);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isTokenValid(accessToken, user, TokenType.ACCESS)) {
            throw new InvalidDataException("Access token is invalid or expired");
        }

        if (!jwtService.isTokenValid(refreshToken, user, TokenType.REFRESH)) {
            throw new InvalidDataException("Refresh token is invalid or expired");
        }

        Token token = tokenService.getByUsername(username) ;
        tokenService.delete(token);
        return "Logout successfully";
    }

    @Override
    public String forgotPassword(String email) {
        //check email exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidDataException("Email not found"));

        // user is active
        if (!user.getStatus().name().equals("ACTIVE")) {
            throw new InvalidDataException("User is not active");
        }
        // generate reset tokem
        String resetToken = jwtService.generateResetToken(user);

        // send email confirm
        String confirmLink = "http://localhost:8080/api/v1/auth/reset";

        return "We have sent a password reset link to your email: " + email + ". Please check your inbox. " + confirmLink;
    }

    @Override
    public String resetPassword(String secretKey) {
        log.info("=====================Reset Password============");

        // extract username from refreshToken
        User user = isValidUserByToken(secretKey, TokenType.RESET);
        return "Reset password successfully" ;
    }

    @Override
    public String changePassword(ResetPasswordRequest resetPasswordRequest) {
        User user = isValidUserByToken(resetPasswordRequest.getSecretKey(), TokenType.RESET);
        if (!resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmPassword())) {
            throw new InvalidDataException("Password and Confirm Password do not match");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        return "Change password successfully";
    }

    private User isValidUserByToken(String token, TokenType tokenType) {
        String username = jwtService.exTractUsername(token, tokenType);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtService.isTokenValid(token, user, tokenType)) {
            throw new InvalidDataException("Token is invalid or expired");
        }
        return user;
    }
}
