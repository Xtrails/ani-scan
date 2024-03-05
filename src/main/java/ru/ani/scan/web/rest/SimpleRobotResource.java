package ru.ani.scan.web.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ani.scan.domain.Robot;
import ru.ani.scan.service.SimpleRobotService;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for managing {@link Robot}.
 */
@RestController
@RequestMapping("/api/robots/simple")
@Transactional
public class SimpleRobotResource {

    private final Logger log = LoggerFactory.getLogger(SimpleRobotResource.class);

    private final SimpleRobotService simpleRobotService;

    public SimpleRobotResource(SimpleRobotService simpleRobotService) {
        this.simpleRobotService = simpleRobotService;
    }

    /**
     * {@code GET  /robots/simple} : get all the robots.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of robots in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Robot>> getAllRobots(@ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Robots");
        Page<Robot> page = simpleRobotService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
