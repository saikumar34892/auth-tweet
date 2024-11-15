// File: src/main/java/com/auth/repository/UserRepository.java
package com.example.userlog.repository;

import com.example.userlog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { // Corrected
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
