package com.elie.hka.entity;

public class Fakultaet {
    private String name;
    private int studentenAnzahl;

    public String getName() {
        return name;
    }

    public int getStudentenAnzahl() {
        return studentenAnzahl;
    }

    public Fakultaet(final String name, final int studentenAnzahl) {
        this.name = name;
        this.studentenAnzahl = studentenAnzahl;
    }
}
