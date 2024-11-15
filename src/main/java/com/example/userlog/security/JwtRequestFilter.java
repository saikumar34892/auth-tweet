package com.example.userlog.security;

import com.example.userlog.service.SessionManager;
import io.jsonwebtoken.ExpiredJwtException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SessionManager sessionManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Remove "Bearer " prefix
            try {
                username = jwtUtil.extractUsername(jwt); // Extract username from token
            } catch (ExpiredJwtException e) {
                logger.warn("JWT Token has expired");
            }
        }

        // If the username exists and no authentication is set in context, we validate the token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Check if session is valid
            if (sessionManager.isSessionValid(username, jwt)) {
                UserDetails userDetails = new User(username, "", new ArrayList<>()); // Create userDetails object

                // Validate the JWT token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Set the request details
                    SecurityContextHolder.getContext().setAuthentication(authToken); // Set authentication in the security context
                }
            }
        }

        // Continue with the request chain
        chain.doFilter(request, response);
    }
}
