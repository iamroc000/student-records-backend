package com.example.demo.dto;

public record AdminCreateUserRequest(
        String username,
        String password,
        String role, // "TEACHER" or "STUDENT"
        String firstName,
        String lastName,
        String emailOrDepartment, // Acts as department for teacher, email for student
        String course // Optional field strictly for student initialization
) {}