package com.example.demo.controller;

import com.example.demo.dto.AdminCreateUserRequest;
import com.example.demo.model.Student;
import com.example.demo.model.Teacher;
import com.example.demo.model.UserEntity;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.TeacherRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository,
                           TeacherRepository teacherRepository,
                           StudentRepository studentRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/create-account")
    public String createAcademicAccount(@RequestBody AdminCreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return "Error: Username already exists!";
        }

        String targetRole = "ROLE_" + request.role().toUpperCase();

        // 1. Create the system authentication user entry
        UserEntity systemUser = new UserEntity(
                request.username(),
                passwordEncoder.encode(request.password()),
                Set.of(targetRole)
        );

        // 2. Route domain mapping dynamically based on intended role assignment
        if ("ROLE_TEACHER".equals(targetRole)) {
            Teacher teacher = new Teacher(request.firstName(), request.lastName(), request.emailOrDepartment(), systemUser);
            teacherRepository.save(teacher);
            return "Teacher account initialized successfully!";
        } else if ("ROLE_STUDENT".equals(targetRole)) {
            Student student = new Student(request.firstName(), request.lastName(), request.emailOrDepartment(), request.course(), 0.0, systemUser);
            studentRepository.save(student);
            return "Student account initialized successfully!";
        }

        return "Error: Invalid role provided. Use 'TEACHER' or 'STUDENT'.";
    }
}