package com.amin.aggar.domain.enums;

public enum PricePeriod {
    MONTHLY("monthly"),
    WEEKLY("weekly"),
    DAILY("daily"),
    YEARLY("yearly"),
    ONE_TIME("one_time");

    private final String value;

    PricePeriod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PricePeriod fromValue(String v) {
        for (PricePeriod p : values()) {
            if (p.value.equalsIgnoreCase(v)) return p;
        }
        return null;
    }
}
