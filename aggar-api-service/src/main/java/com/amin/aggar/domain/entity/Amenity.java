package com.amin.aggar.domain.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @ManyToMany(mappedBy = "amenities")
    private Set<Property> properties;

    public Amenity() {}

    public Amenity(Integer id, String name, Set<Property> properties) {
        this.id = id;
        this.name = name;
        this.properties = properties;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<Property> getProperties() { return properties; }
    public void setProperties(Set<Property> properties) { this.properties = properties; }
}
