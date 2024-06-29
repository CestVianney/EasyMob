package com.archis.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class ResponseBody {
    private List<String> reussite;
    private List<String> erreurs;

    @JsonProperty("reussite")
    public List<String> getReussite() {
        return reussite;
    }

    public void setReussite(List<String> reussite) {
        this.reussite = reussite;
    }

    @JsonProperty("erreurs")
    public List<String> getErreurs() {
        return erreurs;
    }

    public void setErreurs(List<String> erreurs) {
        this.erreurs = erreurs;
    }
}
