package org.fhmdb.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fhmdb.auth.service.UserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String path = request.getServletPath();

        logger.info("Incoming request: {} {}", request.getMethod(), path);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or malformed Authorization header");
            filterChain.doFilter(request, response);
            return;
        }
        logger.info("JWT token found, extracting email...");

        final String jwt = authHeader.substring(7);
        final String email;

        try {
            email = jwtUtil.extractUsername(jwt);
            logger.info("Token subject (email): {}", email);
        } catch (Exception e) {
            logger.error("Failed to extract username from token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(email);
            logger.info("Loaded user details for: {}", email);

            if (jwtUtil.validateToken(jwt)) {
                logger.info("Token is valid. Setting authentication for: {}", email);
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                logger.warn("Invalid token for: {}", email);
            }
        } else {
            logger.debug("No email extracted or user already authenticated.");
        }

        filterChain.doFilter(request, response);
    }

}
