package com.elie.hka.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class Hochschule {
    private UUID id;

    @NotBlank
    private String name;

    @NotNull
    private Adresse adresse;

    @NotNull
    private List<Fakultaet> fakultaet;

    public Hochschule() {
    }

    public Hochschule(final UUID id, final String name, final Adresse adresse, final List<Fakultaet> fakultaet) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.fakultaet = fakultaet;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public List<Fakultaet> getFakultaet() {
        return fakultaet;
    }
}
