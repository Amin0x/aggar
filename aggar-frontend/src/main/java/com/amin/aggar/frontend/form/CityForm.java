package com.amin.aggar.frontend.form;

public class CityForm {
    private Integer stateId;
    private String name;

    public CityForm() {}

    public CityForm(Integer stateId, String name) {
        this.stateId = stateId;
        this.name = name;
    }

    public Integer getStateId() { return stateId; }
    public void setStateId(Integer stateId) { this.stateId = stateId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
