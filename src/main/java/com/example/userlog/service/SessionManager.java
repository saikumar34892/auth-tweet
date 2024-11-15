// File: src/main/java/com/auth/service/SessionManager.java
package com.example.userlog.service;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class SessionManager {
    private final ConcurrentMap<String, String> userSessions = new ConcurrentHashMap<>();

    public void createSession(String username, String token) {
        userSessions.put(username, token); // Corrected
    }

    public void invalidateSession(String username) {
        userSessions.remove(username); // Corrected
    }

    public boolean isSessionValid(String username, String token) {
        String storedToken = userSessions.get(username); // Corrected
        return storedToken != null && storedToken.equals(token); // Corrected
    }
}
