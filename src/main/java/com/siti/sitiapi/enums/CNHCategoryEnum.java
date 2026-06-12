package com.siti.sitiapi.enums;

import lombok.Getter;

@Getter
public enum CNHCategoryEnum {
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E");

    private String category;

    CNHCategoryEnum(String category) {
        this.category = category;
    }
}
