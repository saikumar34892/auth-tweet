// File: src/main/java/com/auth/controller/AuthController.java
package com.example.userlog.controller;

import com.example.userlog.model.User;
import com.example.userlog.security.JwtUtil;
import com.example.userlog.service.AuthService;
import com.example.userlog.service.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SessionManager sessionManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = authService.register(user); // Corrected
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(registeredUser.getUsername()) // Corrected
                .password("") // No need to pass password here, it should be empty after registration
                .authorities(registeredUser.getRole()) // Corrected
                .build();

            String token = jwtUtil.generateToken(userDetails); // Corrected
            sessionManager.createSession(registeredUser.getUsername(), token); // Corrected

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("token", token);
            response.put("username", registeredUser.getUsername()); // Corrected
            response.put("role", registeredUser.getRole()); // Corrected
            
            return ResponseEntity.ok(response); // Corrected
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        if (!credentials.containsKey("username") || !credentials.containsKey("password")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }

        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            Optional<User> userOpt = authService.authenticate(username, password); // Corrected
            
            if (userOpt.isPresent()) { // Corrected
                User user = userOpt.get();
                UserDetails userDetails = org.springframework.security.core.userdetails.User
                    .withUsername(username)
                    .password("") // Password should not be passed here after authentication
                    .authorities(user.getRole()) // Corrected
                    .build();

                String token = jwtUtil.generateToken(userDetails); // Corrected
                sessionManager.createSession(username, token); // Corrected

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("username", username);
                response.put("role", user.getRole()); // Corrected
                return ResponseEntity.ok(response); // Corrected
            }

            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or missing token"));
        }

        try {
            String jwt = token.substring(7);
            String username = jwtUtil.extractUsername(jwt); // Corrected
            
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("") // Corrected
                .authorities("USER")
                .build();

            if (!jwtUtil.validateToken(jwt, userDetails)) { // Corrected
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired token"));
            }

            authService.logout(username); // Corrected
            return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Logout failed: " + e.getMessage()));
        }
    }
}
