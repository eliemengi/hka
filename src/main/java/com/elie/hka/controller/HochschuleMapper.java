package com.elie.hka.controller;

import com.elie.hka.entity.Adresse;
import com.elie.hka.entity.Fakultaet;
import com.elie.hka.entity.Hochschule;
import java.util.List;
import java.util.stream.Collectors;

public class HochschuleMapper {

    public static Hochschule toEntity(HochschuleDTO dto) {

        Adresse adresse = new Adresse(dto.getStrasse(), dto.getOrt());

        List<Fakultaet> fakultaeten = dto.getFakultaeten()
                .stream()
                .map(name -> new Fakultaet(name, 0))
                .collect(Collectors.toList());

        return new Hochschule(null, dto.getName(), adresse, fakultaeten);
    }
}

