package com.amin.aggar.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PricePeriodConverter implements AttributeConverter<PricePeriod, String> {
    @Override
    public String convertToDatabaseColumn(PricePeriod attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public PricePeriod convertToEntityAttribute(String dbData) {
        return dbData != null ? PricePeriod.fromValue(dbData) : null;
    }
}
