package com.amin.aggar.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "states")
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "name_ar", length = 200)
    private String nameAr;

    @Column(length = 10)
    private String code;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "state")
    private List<City> cities;

    public State() {}

    public State(Integer id, String name, String code, LocalDateTime createdAt, List<City> cities) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.createdAt = createdAt;
        this.cities = cities;
    }

    public State(Integer id, String name, String nameAr, String code, LocalDateTime createdAt, List<City> cities) {
        this.id = id;
        this.name = name;
        this.nameAr = nameAr;
        this.code = code;
        this.createdAt = createdAt;
        this.cities = cities;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNameAr() { return nameAr; }
    public void setNameAr(String nameAr) { this.nameAr = nameAr; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<City> getCities() { return cities; }
    public void setCities(List<City> cities) { this.cities = cities; }
}
