package com.elie.hka.entity;

public class Adresse {

    private String strasse;
    private String ort;

    public String getStrasse() {
        return strasse;
    }

    public String getOrt() {
        return ort;
    }

    public Adresse(final String strasse, final String ort) {
        this.strasse = strasse;
        this.ort = ort;
    }



}
