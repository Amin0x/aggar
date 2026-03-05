package com.amin.aggar.api.dto;

public class NeighborhoodDto {
    private Integer id;
    private Integer cityId;
    private String name;

    public NeighborhoodDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCityId() { return cityId; }
    public void setCityId(Integer cityId) { this.cityId = cityId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

