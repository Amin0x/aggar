package com.amin.aggar.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 255, unique = true)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(nullable = false, unique = true, length = 250)
    private String username;

    @Column(nullable = false, length = 250)
    private String password;

    @Column(length = 50)
    private String role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "owner")
    private List<Property> ownedProperties;

    @OneToMany(mappedBy = "agent")
    private List<Property> agentProperties;

    public User() {
    }

    public User(Integer id, String name, String email, String phone, String role, LocalDateTime createdAt, List<Property> ownedProperties, List<Property> agentProperties) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.createdAt = createdAt;
        this.ownedProperties = ownedProperties;
        this.agentProperties = agentProperties;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Property> getOwnedProperties() {
        return ownedProperties;
    }

    public void setOwnedProperties(List<Property> ownedProperties) {
        this.ownedProperties = ownedProperties;
    }

    public List<Property> getAgentProperties() {
        return agentProperties;
    }

    public void setAgentProperties(List<Property> agentProperties) {
        this.agentProperties = agentProperties;
    }
}
