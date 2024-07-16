package com.easymob.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Monstre {
    private int id;
    private String nom;
    private String slug;
    private String type;

    public Monstre() {
    }

    @Builder
    public Monstre(int id, String nom, String slug, String type) {
        this.id = id;
        this.nom = nom;
        this.slug = slug;
        this.type = type;
    }
}
