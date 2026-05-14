package com.elie.hka.controller;
import com.elie.hka.entity.Hochschule;
import com.elie.hka.service.HochschuleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.util.Collection;
import java.util.UUID;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
public class HochschuleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HochschuleController.class);
    private final HochschuleService service;

    public HochschuleController(final HochschuleService service) {
        this.service = service;
    }

    @GetMapping("/hochschulen")
    public Collection<Hochschule> get() {
        LOGGER.info("GET /hochschulen aufgerufen");
        return service.findAll();
    }

    @GetMapping("/hochschulen/{id}")
    public Hochschule getById(@PathVariable("id") final UUID id) {
        LOGGER.info("GET /hochschulen/{} aufgerufen", id);
        return service.findById(id);
    }

    @GetMapping("/hochschulen/search")
    public Collection<Hochschule> searchByName(@RequestParam("name") final String name) {
        LOGGER.info("GET /hochschulen/search?name={} aufgerufen", name);
        return service.findByName(name);
    }

    @PostMapping("/hochschulen")
    public ResponseEntity<Void> create(@RequestBody @Valid final HochschuleDTO dto) {
        LOGGER.info("POST /hochschulen aufgerufen");

        Hochschule entity = HochschuleMapper.toEntity(dto);
        Hochschule created = service.save(entity);

        return ResponseEntity.created(URI.create("/hochschulen/" + created.getId())).build();
    }

    @PutMapping("/hochschulen/{id}")
    public Hochschule update(@PathVariable("id") final UUID id,
                             @RequestBody @Valid final HochschuleDTO dto) {

        LOGGER.info("PUT /hochschulen/{} aufgerufen", id);

        Hochschule entity = HochschuleMapper.toEntity(dto);
        entity.setId(id);

        return service.update(id, entity);
    }
}

