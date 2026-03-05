package com.amin.aggar.api.dto;

public class CityDto {
    private Integer id;
    private Integer stateId;
    private String name;

    public CityDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getStateId() { return stateId; }
    public void setStateId(Integer stateId) { this.stateId = stateId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

