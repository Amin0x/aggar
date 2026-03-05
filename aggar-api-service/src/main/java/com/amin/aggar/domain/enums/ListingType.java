package com.amin.aggar.domain.enums;

public enum ListingType {
    SALE("sale"),
    RENT("rent");

    private final String value;

    ListingType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ListingType fromValue(String v) {
        for (ListingType lt : values()) {
            if (lt.value.equalsIgnoreCase(v)) return lt;
        }
        return null;
    }
}
