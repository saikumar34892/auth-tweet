// File: src/main/java/com/auth/service/AuthService.java
package com.example.userlog.service;

import com.example.userlog.model.User;
import com.example.userlog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionManager sessionManager;

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Set default role if not specified
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        // Encrypt password before storing
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Corrected
        return userRepository.save(user); // Corrected
    }

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) { // Corrected
            return userOpt;
        }

        return Optional.empty(); // Corrected
    }

    public void logout(String username) {
        sessionManager.invalidateSession(username); // Corrected
    }
}
