package vn.yame.service;

import org.springframework.security.core.userdetails.UserDetails;
import vn.yame.common.enums.TokenType;

public interface JwtService {
    public String generateToken(UserDetails userDetails);
    public String generateRefreshToken(UserDetails userDetails);
    public String exTractUsername(String token, TokenType tokenType);
    public boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType);

}
