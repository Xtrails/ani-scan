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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ani.scan.domain.Robot;
import ru.ani.scan.repository.RobotRepository;
import ru.ani.scan.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ru.ani.scan.domain.Robot}.
 */
@RestController
@RequestMapping("/api/robots")
@Transactional
public class RobotResource {

    private final Logger log = LoggerFactory.getLogger(RobotResource.class);

    private static final String ENTITY_NAME = "robot";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RobotRepository robotRepository;

    public RobotResource(RobotRepository robotRepository) {
        this.robotRepository = robotRepository;
    }

    /**
     * {@code POST  /robots} : Create a new robot.
     *
     * @param robot the robot to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new robot, or with status {@code 400 (Bad Request)} if the robot has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Robot> createRobot(@Valid @RequestBody Robot robot) throws URISyntaxException {
        log.debug("REST request to save Robot : {}", robot);
        if (robot.getId() != null) {
            throw new BadRequestAlertException("A new robot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Robot result = robotRepository.save(robot);
        return ResponseEntity
            .created(new URI("/api/robots/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /robots/:id} : Updates an existing robot.
     *
     * @param id the id of the robot to save.
     * @param robot the robot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated robot,
     * or with status {@code 400 (Bad Request)} if the robot is not valid,
     * or with status {@code 500 (Internal Server Error)} if the robot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Robot> updateRobot(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Robot robot)
        throws URISyntaxException {
        log.debug("REST request to update Robot : {}, {}", id, robot);
        if (robot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, robot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!robotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Robot result = robotRepository.save(robot);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, robot.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /robots/:id} : Partial updates given fields of an existing robot, field will ignore if it is null
     *
     * @param id the id of the robot to save.
     * @param robot the robot to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated robot,
     * or with status {@code 400 (Bad Request)} if the robot is not valid,
     * or with status {@code 404 (Not Found)} if the robot is not found,
     * or with status {@code 500 (Internal Server Error)} if the robot couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Robot> partialUpdateRobot(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Robot robot
    ) throws URISyntaxException {
        log.debug("REST request to partial update Robot partially : {}, {}", id, robot);
        if (robot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, robot.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!robotRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Robot> result = robotRepository
            .findById(robot.getId())
            .map(existingRobot -> {
                if (robot.getType() != null) {
                    existingRobot.setType(robot.getType());
                }
                if (robot.getLots() != null) {
                    existingRobot.setLots(robot.getLots());
                }
                if (robot.getPeriod() != null) {
                    existingRobot.setPeriod(robot.getPeriod());
                }
                if (robot.getOperationType() != null) {
                    existingRobot.setOperationType(robot.getOperationType());
                }
                if (robot.getOperationCount() != null) {
                    existingRobot.setOperationCount(robot.getOperationCount());
                }
                if (robot.getFirstOperationDttm() != null) {
                    existingRobot.setFirstOperationDttm(robot.getFirstOperationDttm());
                }
                if (robot.getLastOperationDttm() != null) {
                    existingRobot.setLastOperationDttm(robot.getLastOperationDttm());
                }
                if (robot.getDetectionDttm() != null) {
                    existingRobot.setDetectionDttm(robot.getDetectionDttm());
                }
                if (robot.getLastPrice() != null) {
                    existingRobot.setLastPrice(robot.getLastPrice());
                }
                if (robot.getVolumeByHour() != null) {
                    existingRobot.setVolumeByHour(robot.getVolumeByHour());
                }

                return existingRobot;
            })
            .map(robotRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, robot.getId().toString())
        );
    }

    /**
     * {@code GET  /robots} : get all the robots.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of robots in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Robot>> getAllRobots(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Robots");
        Page<Robot> page = robotRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /robots/:id} : get the "id" robot.
     *
     * @param id the id of the robot to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the robot, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Robot> getRobot(@PathVariable("id") Long id) {
        log.debug("REST request to get Robot : {}", id);
        Optional<Robot> robot = robotRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(robot);
    }

    /**
     * {@code DELETE  /robots/:id} : delete the "id" robot.
     *
     * @param id the id of the robot to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRobot(@PathVariable("id") Long id) {
        log.debug("REST request to delete Robot : {}", id);
        robotRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
