package com.amin.aggar.frontend.dto;

public class CityDto {
    private Integer id;
    private Integer stateId;
    private String name;
    private String stateName;
    private String stateNameAr;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateNameAr() {
        return stateNameAr;
    }

    public void setStateNameAr(String stateNameAr) {
        this.stateNameAr = stateNameAr;
    }
}
