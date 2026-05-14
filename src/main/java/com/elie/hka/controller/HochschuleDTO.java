package com.elie.hka.controller;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class HochschuleDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String strasse;

    @NotBlank
    private String ort;

    @NotEmpty
    private List<String> fakultaeten;

    public String getName() {
        return name;
    }

    public String getStrasse() {
        return strasse;
    }

    public String getOrt() {
        return ort;
    }

    public List<String> getFakultaeten() {
        return fakultaeten;
    }

    public HochschuleDTO(final String name, final String strasse, final String ort, final List<String> fakultaeten) {
        this.name = name;
        this.strasse = strasse;
        this.ort = ort;
        this.fakultaeten = fakultaeten;
    }
}
