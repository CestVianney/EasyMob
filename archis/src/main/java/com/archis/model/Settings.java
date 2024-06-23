package com.archis.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Settings {
    private String nom;
    private String valeur;
}
