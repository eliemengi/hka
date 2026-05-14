package com.elie.hka.repository;

import com.elie.hka.entity.Adresse;
import com.elie.hka.entity.Fakultaet;
import com.elie.hka.entity.Hochschule;
import com.elie.hka.service.NotFoundException;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class HochschuleRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(HochschuleRepository.class);

    private static final UUID ID_1 = UUID.fromString("6e9e2ec1-7a51-4e8f-ac70-7a855815438d");

    private static final Collection<Hochschule> HOCHSCHULEN = Stream.of(
            new Hochschule(
                    ID_1,
                    "Hochschule Karlsruhe",
                    new Adresse("Moltkestrasse 30", "Karlsruhe"),
                    Stream.of(
                            new Fakultaet("Informatik und Wirtschaftsinformatik", 2500),
                            new Fakultaet("Maschinenbau und Mechatronik", 1800)
                    ).collect(Collectors.toList())
            )
    ).collect(Collectors.toList());

    public Collection<Hochschule> findAll() {
        LOGGER.info("Repository: gebe Hochschulen zurück");
        return HOCHSCHULEN;
    }

    public Hochschule findById(final UUID id) {
        LOGGER.info("Repository: suche Hochschule mit ID {}", id);
        return HOCHSCHULEN.stream()
                .filter(hochschule -> hochschule.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(id));
    }

    public Collection<Hochschule> findByName(final String name) {
        LOGGER.info("Repository: suche Hochschulen mit Name {}", name);
        return HOCHSCHULEN.stream()
                .filter(hochschule -> hochschule.getName().contains(name))
                .collect(Collectors.toList());
    }

    public Hochschule save(final Hochschule hochschule) {
        final var created = new Hochschule(
                UUID.randomUUID(),
                hochschule.getName(),
                hochschule.getAdresse(),
                hochschule.getFakultaet()
        );

        HOCHSCHULEN.add(created);

        return created;
    }

    public Hochschule update(final UUID id, final Hochschule neu) {
        LOGGER.info("Repository: update Hochschule {}", id);

        final var alt = findById(id);

        HOCHSCHULEN.remove(alt);

        final var updated = new Hochschule(
                id,
                neu.getName(),
                neu.getAdresse(),
                neu.getFakultaet()
        );

        HOCHSCHULEN.add(updated);

        return updated;
    }
}