package com.archis.model;

import com.archis.utils.ZoneEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Monstre {
    private int id;
    private String nom;
    private String slug;
    private String type;
    private int etape;
    private int quantite;
    private int recherche;
    private int propose;
    private List<ZoneEnum> zone;
    private String image;

    public Monstre() {
    }

    @lombok.Builder
    public Monstre(int id, String nom, String slug, String type, int etape, int quantite, int recherche, int propose, List<ZoneEnum> zone, String image) {
        this.id = id;
        this.nom = nom;
        this.slug = slug;
        this.type = type;
        this.etape = etape;
        this.quantite = quantite;
        this.recherche = recherche;
        this.propose = propose;
        this.zone = zone;
        this.image = image;
    }
}
