package com.example.userlog.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;



@Data // Lombok annotation to generate getters, setters, etc.
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users") // Make sure this annotation is here to mark the class as a JPA entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "USER"; // Default role if none is provided
}
