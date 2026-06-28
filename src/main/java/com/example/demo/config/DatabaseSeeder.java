package com.example.demo.config;

import com.example.demo.model.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

//@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("[SEEDER LOG] Running automatic database password synchronization...");

        // Remove old manual conflicting records if they exist
        userRepository.findByUsername("super_admin").ifPresent(userRepository::delete);

        // Generate a mathematically perfect local BCrypt hash for 'admin123'
        String secureHashedPassword = passwordEncoder.encode("admin123");

        System.out.println("[SEEDER LOG] New calculated BCrypt hash string: " + secureHashedPassword);

        UserEntity admin = new UserEntity(
                "super_admin",
                secureHashedPassword,
                Set.of("ROLE_ADMIN")
        );

        userRepository.save(admin);
        System.out.println("[SEEDER LOG] Database successfully synchronized! 'super_admin' password is now securely set to 'admin123'.");
    }
}