package com.amin.aggar.api.dto;

public record CityDto (
    Integer id,
    Integer stateId,
    String name,
    String stateName,
    String stateNameAr
){}
