package vn.yame.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.yame.common.enums.TokenType;
import vn.yame.service.JwtService;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.access-expiry}")
    private Duration accessExpiry;

    @Value("${jwt.refresh-expiry}")
    private Duration refreshExpiry;

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${jwt.refreshKey}")
    private String jwtRefreshKey;

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, accessExpiry, TokenType.ACCESS);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshExpiry, TokenType.REFRESH);
    }


    @Override
    public String exTractUsername(String token, TokenType tokenType) {
        try {
            return extractClaim(token, Claims::getSubject, tokenType);
        } catch (Exception e) {
            System.out.println("Lỗi khi extract username: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails, TokenType tokenType) {
        try {
            final String username = exTractUsername(token, tokenType);
            return username != null &&
                   username.equals(userDetails.getUsername()) &&
                   !isTokenExpired(token, tokenType);
        } catch (Exception e) {
            System.out.println("Token không hợp lệ: " + e.getMessage());
            return false;
        }
    }

    // Thêm method kiểm tra token hết hạn
    private boolean isTokenExpired(String token, TokenType tokenType) {
        try {
            return extractExpiration(token, tokenType).before(new Date());
        } catch (Exception e) {
            System.out.println("Lỗi khi kiểm tra expiration: " + e.getMessage());
            return true; // Nếu có lỗi thì coi như token hết hạn
        }
    }

    // Thêm method lấy expiration date
    private Date extractExpiration(String token, TokenType tokenType) {
        return extractClaim(token, Claims::getExpiration, tokenType);
    }

    // Thêm method để validate token không cần UserDetails
    public boolean isTokenValid(String token, TokenType tokenType) {
        try {
            return !isTokenExpired(token, tokenType) && exTractUsername(token,tokenType) != null;
        } catch (Exception e) {
            System.out.println("Lỗi validate token: " + e.getMessage());
            return false;
        }
    }

    private String generateToken(UserDetails userDetails, Duration expiry, TokenType tokenType) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiry.toMillis()))
                .signWith(getKey(tokenType), SignatureAlgorithm.HS256)
                .compact();
    }


    private Key getKey(TokenType tokenType) {
        byte[] keyBytes;
        if (tokenType == TokenType.ACCESS) {
           keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        } else {
            keyBytes =  Decoders.BASE64.decode(jwtRefreshKey);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, TokenType tokenType) {
        try {
            final Claims claims = extractAllClaim(token, tokenType);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            System.out.println("Lỗi khi extract claim: " + e.getMessage());
            throw e; // Re-throw để method gọi có thể xử lý
        }
    }

    private Claims extractAllClaim(String token, TokenType tokenType) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey(tokenType))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("Lỗi khi parse JWT token: " + e.getMessage());
            throw e;
        }
    }
}