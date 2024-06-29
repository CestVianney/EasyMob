package com.archis.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MonstreMetamob {
    @JsonProperty("id")
    private String id;

    @JsonProperty("nom")
    private String nom;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("type")
    private String type;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("etape")
    private String etape;

    @JsonProperty("zone")
    private String zone;

    @JsonProperty("souszone")
    private String souszone;

    @JsonProperty("quantite")
    private String quantite;

    @JsonProperty("recherche")
    private String recherche;

    @JsonProperty("propose")
    private String propose;

    @JsonProperty("nom_normal")
    private String nomNormal;


    // Default constructor
    public MonstreMetamob() {
    }

    public Monstre mapToMonstre() {
        return Monstre.builder()
                .id(Integer.parseInt(id))
                .quantite(Integer.parseInt(quantite))
                .recherche(Integer.parseInt(recherche))
                .propose(Integer.parseInt(propose))
                .build();
    }
}
