package com.amin.aggar.frontend.dto;

public record CityDto(
        Integer id,
        Integer stateId,
        String name,
        String stateName,
        String stateNameAr
) {
}
