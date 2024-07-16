package com.easymob.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonstreMetamobRecense {
    @JsonProperty("id")
    private String id;
    @JsonProperty("nom")
    private String nom;
    @JsonProperty("nom_normal")
    private String nom_normal;
    @JsonProperty("slug")
    private String slug;
    @JsonProperty("type")
    private String type;
    @JsonProperty("etape")
    private String etape;
    @JsonProperty("zone")
    private String zone;
    @JsonProperty("souszone")
    @JsonIgnore
    private String souszone;
    @JsonProperty("image_url")
    private String image_url;
}
