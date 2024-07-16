package com.easymob.utils;

import lombok.Getter;

@Getter
public enum TypeAjoutEnum {
    QUANTITE("aucun"),
    RECHERCHE("recherche"),
    PROPOSE("propose");

    private final String value;

    TypeAjoutEnum(String value) {
        this.value = value;
    }
}
