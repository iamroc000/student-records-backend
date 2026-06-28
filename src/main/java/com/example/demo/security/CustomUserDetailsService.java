package com.example.demo.security;

import com.example.demo.model.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("[AUTH DB LOG] Fetching user credentials for username: " + username);

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Convert roles to a readable string format for the console log
        String fetchedRoles = userEntity.getRoles().stream().collect(Collectors.joining(", "));
        System.out.println("[AUTH DB LOG] Raw roles found in Database table: [" + fetchedRoles + "]");

        List<SimpleGrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // Verify what Java wrapped inside the GrantedAuthority objects
        String grantedAuthorities = authorities.stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.joining(", "));
        System.out.println("[AUTH DB LOG] SimpleGrantedAuthority array mapped by Spring: [" + grantedAuthorities + "]");

        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                authorities
        );
    }
}