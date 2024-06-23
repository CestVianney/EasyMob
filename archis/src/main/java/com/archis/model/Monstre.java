package com.archis.model;

import com.archis.utils.ZoneEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Monstre {
    private int id;
    private String nomMonstre;
    private String nomArchimonstre;
    private String type;
    private int etape;
    private int nombre;
    private List<ZoneEnum> zone;
    private String image;
}
