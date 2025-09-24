package vn.yame.config;

import ch.qos.logback.core.util.StringUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.yame.common.enums.TokenType;
import vn.yame.service.JwtService;
import vn.yame.service.UserService;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("doFilterInternal----------------------------------------");
        final String authorization = request.getHeader("Authorization");
        log.info("Authorization: {}", authorization);
        if(StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")){
            log.info("Invalid token");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorization.substring("Bearer ".length());
        log.info("Token: {}", token);
        final String username = jwtService.exTractUsername(token, TokenType.ACCESS);
        log.info("Username: {}", username);

        if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails, TokenType.ACCESS)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);

            }
        }
        filterChain.doFilter(request, response);


    }
}
