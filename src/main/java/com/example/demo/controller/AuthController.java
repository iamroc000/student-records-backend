package com.example.demo.controller;

import com.example.demo.model.UserEntity;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            return "Error: Username is already taken!";
        }

        // Hash the password before saving to the database
        UserEntity user = new UserEntity(
                registerRequest.username(),
                passwordEncoder.encode(registerRequest.password()),
                Set.of("USER")
        );

        userRepository.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest) {
        System.out.println("[API LOGIN LOG] Attempting login process for user: " + loginRequest.username());

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
            );

            System.out.println("[API LOGIN LOG] AuthenticationManager successfully verified credentials!");

            String assignedAuthorities = authentication.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.joining(", "));
            System.out.println("[API LOGIN LOG] Final verified authorities passed to JWT token: [" + assignedAuthorities + "]");

            String token = tokenProvider.generateToken(authentication.getName(), authentication.getAuthorities());
            return new AuthResponse(token);

        } catch (Exception e) {
            System.out.println("[API LOGIN LOG] CRITICAL: Authentication failed inside AuthenticationManager!");
            System.out.println("[API LOGIN LOG] Exception type encountered: " + e.getClass().getName());
            System.out.println("[API LOGIN LOG] Exception error message details: " + e.getMessage());
            throw e;
        }
    }
}

// Java 21 DTO Records
record LoginRequest(String username, String password) {}
record RegisterRequest(String username, String password) {}
record AuthResponse(String accessToken) {}