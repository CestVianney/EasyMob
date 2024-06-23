package com.archis.utils;

import lombok.Getter;

@Getter
public enum TypeMonstreEnum {
    MONSTRE("monstre", "Monstre"),
    ARCHIMONSTRE("archimonstre", "Archimonstre"),
    BOSS("boss","Boss"),
    TOUS("tous", "Tous");

    private final String typeBdd;
    private final String display;

    TypeMonstreEnum(String typeBdd, String display) {
        this.typeBdd = typeBdd;
        this.display = display;
    }
}
