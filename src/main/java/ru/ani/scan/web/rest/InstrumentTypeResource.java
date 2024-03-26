package ru.ani.scan.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.ani.scan.domain.InstrumentType;
import ru.ani.scan.repository.InstrumentTypeRepository;
import ru.ani.scan.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.ani.scan.domain.InstrumentType}.
 */
@RestController
@RequestMapping("/api/instrument-types")
@Transactional
public class InstrumentTypeResource {

    private final Logger log = LoggerFactory.getLogger(InstrumentTypeResource.class);

    private static final String ENTITY_NAME = "instrumentType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InstrumentTypeRepository instrumentTypeRepository;

    public InstrumentTypeResource(InstrumentTypeRepository instrumentTypeRepository) {
        this.instrumentTypeRepository = instrumentTypeRepository;
    }

    /**
     * {@code POST  /instrument-types} : Create a new instrumentType.
     *
     * @param instrumentType the instrumentType to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new instrumentType, or with status {@code 400 (Bad Request)} if the instrumentType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<InstrumentType> createInstrumentType(@Valid @RequestBody InstrumentType instrumentType)
        throws URISyntaxException {
        log.debug("REST request to save InstrumentType : {}", instrumentType);
        if (instrumentType.getId() != null) {
            throw new BadRequestAlertException("A new instrumentType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        InstrumentType result = instrumentTypeRepository.save(instrumentType);
        return ResponseEntity
            .created(new URI("/api/instrument-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /instrument-types/:id} : Updates an existing instrumentType.
     *
     * @param id the id of the instrumentType to save.
     * @param instrumentType the instrumentType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instrumentType,
     * or with status {@code 400 (Bad Request)} if the instrumentType is not valid,
     * or with status {@code 500 (Internal Server Error)} if the instrumentType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InstrumentType> updateInstrumentType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InstrumentType instrumentType
    ) throws URISyntaxException {
        log.debug("REST request to update InstrumentType : {}, {}", id, instrumentType);
        if (instrumentType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instrumentType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instrumentTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        InstrumentType result = instrumentTypeRepository.save(instrumentType);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, instrumentType.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /instrument-types/:id} : Partial updates given fields of an existing instrumentType, field will ignore if it is null
     *
     * @param id the id of the instrumentType to save.
     * @param instrumentType the instrumentType to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instrumentType,
     * or with status {@code 400 (Bad Request)} if the instrumentType is not valid,
     * or with status {@code 404 (Not Found)} if the instrumentType is not found,
     * or with status {@code 500 (Internal Server Error)} if the instrumentType couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<InstrumentType> partialUpdateInstrumentType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InstrumentType instrumentType
    ) throws URISyntaxException {
        log.debug("REST request to partial update InstrumentType partially : {}, {}", id, instrumentType);
        if (instrumentType.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instrumentType.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instrumentTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InstrumentType> result = instrumentTypeRepository
            .findById(instrumentType.getId())
            .map(existingInstrumentType -> {
                if (instrumentType.getName() != null) {
                    existingInstrumentType.setName(instrumentType.getName());
                }

                return existingInstrumentType;
            })
            .map(instrumentTypeRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, instrumentType.getId().toString())
        );
    }

    /**
     * {@code GET  /instrument-types} : get all the instrumentTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of instrumentTypes in body.
     */
    @GetMapping("")
    public List<InstrumentType> getAllInstrumentTypes() {
        log.debug("REST request to get all InstrumentTypes");
        return instrumentTypeRepository.findAll();
    }

    /**
     * {@code GET  /instrument-types/:id} : get the "id" instrumentType.
     *
     * @param id the id of the instrumentType to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the instrumentType, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InstrumentType> getInstrumentType(@PathVariable("id") Long id) {
        log.debug("REST request to get InstrumentType : {}", id);
        Optional<InstrumentType> instrumentType = instrumentTypeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(instrumentType);
    }

    /**
     * {@code DELETE  /instrument-types/:id} : delete the "id" instrumentType.
     *
     * @param id the id of the instrumentType to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstrumentType(@PathVariable("id") Long id) {
        log.debug("REST request to delete InstrumentType : {}", id);
        instrumentTypeRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
