package com.elie.hka.service;

import com.elie.hka.entity.Hochschule;
import com.elie.hka.repository.HochschuleRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;

@Service
public class HochschuleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HochschuleService.class);
    private final HochschuleRepository repository;

    public HochschuleService(final HochschuleRepository repository) {
        this.repository = repository;
    }

    public Collection<Hochschule> findAll() {
        LOGGER.info("Service: finde alle Hochschulen");
        return repository.findAll();
    }

    public Hochschule findById(final UUID id) {
        LOGGER.info("Service: finde Hochschule mit ID {}", id);
        return repository.findById(id);
    }

    public Collection<Hochschule> findByName(final String name) {
        LOGGER.info("Service: suche Hochschulen mit Name {}", name);

        final var result = repository.findByName(name);

        if (result.isEmpty()) {
            throw new RuntimeException("Keine Hochschule mit Name " + name + " gefunden");
        }

        return result;
    }

    public Hochschule save(final Hochschule hochschule) {
        LOGGER.info("Service: speichere Hochschule {}", hochschule.getName());
        return repository.save(hochschule);
    }
    public Hochschule update(final UUID id, final Hochschule hochschule) {
        LOGGER.info("Service: update Hochschule {}", id);
        return repository.update(id, hochschule);
    }
}
