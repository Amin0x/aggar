package com.amin.aggar.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ListingTypeConverter implements AttributeConverter<ListingType, String> {
    @Override
    public String convertToDatabaseColumn(ListingType attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public ListingType convertToEntityAttribute(String dbData) {
        return dbData != null ? ListingType.fromValue(dbData) : null;
    }
}
