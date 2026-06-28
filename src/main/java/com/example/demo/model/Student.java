package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;
    private String course;

    // Standard Constructors, Getters, and Setters
    public Student() {}

    public Student(String firstName, String lastName, String email, String course) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.course = course;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
}