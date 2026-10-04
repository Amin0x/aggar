package com.amin.aggar.frontend.form;

public class NeighborhoodForm {
    private Integer cityId;
    private String name;

    public NeighborhoodForm() {}

    public NeighborhoodForm(Integer cityId, String name) {
        this.cityId = cityId;
        this.name = name;
    }

    public Integer getCityId() { return cityId; }
    public void setCityId(Integer cityId) { this.cityId = cityId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
