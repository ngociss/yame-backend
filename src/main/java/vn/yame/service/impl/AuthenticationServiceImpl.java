package vn.yame.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import vn.yame.common.enums.TokenType;
import vn.yame.dto.reponse.TokenResponse;
import vn.yame.dto.request.SignInRequest;
import vn.yame.exception.InvalidDataException;
import vn.yame.model.Token;
import vn.yame.model.User;
import vn.yame.repository.UserRepository;
import vn.yame.service.AuthenticationService;
import vn.yame.service.JwtService;
import vn.yame.service.TokenService;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;

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
}
