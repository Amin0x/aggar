package com.amin.aggar.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cities", uniqueConstraints = @UniqueConstraint(columnNames = {"state_id", "name"}))
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "city")
    private List<Neighborhood> neighborhoods;

    public City() {}

    public City(Integer id, State state, String name, LocalDateTime createdAt, List<Neighborhood> neighborhoods){
        this.id = id; this.state = state; this.name = name; this.createdAt = createdAt; this.neighborhoods = neighborhoods;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Neighborhood> getNeighborhoods() { return neighborhoods; }
    public void setNeighborhoods(List<Neighborhood> neighborhoods) { this.neighborhoods = neighborhoods; }
}
