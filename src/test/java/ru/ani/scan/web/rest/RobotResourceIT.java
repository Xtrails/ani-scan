package ru.ani.scan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.ani.scan.IntegrationTest;
import ru.ani.scan.domain.Robot;
import ru.ani.scan.domain.enumeration.OperationType;
import ru.ani.scan.domain.enumeration.RobotType;
import ru.ani.scan.repository.RobotRepository;

/**
 * Integration tests for the {@link RobotResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RobotResourceIT {

    private static final RobotType DEFAULT_TYPE = RobotType.SIMPLE;
    private static final RobotType UPDATED_TYPE = RobotType.LOOP;

    private static final String DEFAULT_LOTS = "AAAAAAAAAA";
    private static final String UPDATED_LOTS = "BBBBBBBBBB";

    private static final Long DEFAULT_PERIOD = 1L;
    private static final Long UPDATED_PERIOD = 2L;

    private static final OperationType DEFAULT_OPERATION_TYPE = OperationType.BUY;
    private static final OperationType UPDATED_OPERATION_TYPE = OperationType.SELL;

    private static final Long DEFAULT_OPERATION_COUNT = 1L;
    private static final Long UPDATED_OPERATION_COUNT = 2L;

    private static final Instant DEFAULT_FIRST_OPERATION_DTTM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FIRST_OPERATION_DTTM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_OPERATION_DTTM = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_OPERATION_DTTM = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_LAST_PRICE = 1D;
    private static final Double UPDATED_LAST_PRICE = 2D;

    private static final Long DEFAULT_VOLUME_BY_HOUR = 1L;
    private static final Long UPDATED_VOLUME_BY_HOUR = 2L;

    private static final String ENTITY_API_URL = "/api/robots";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RobotRepository robotRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRobotMockMvc;

    private Robot robot;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Robot createEntity(EntityManager em) {
        Robot robot = new Robot()
            .type(DEFAULT_TYPE)
            .lots(DEFAULT_LOTS)
            .period(DEFAULT_PERIOD)
            .operationType(DEFAULT_OPERATION_TYPE)
            .operationCount(DEFAULT_OPERATION_COUNT)
            .firstOperationDttm(DEFAULT_FIRST_OPERATION_DTTM)
            .lastOperationDttm(DEFAULT_LAST_OPERATION_DTTM)
            .lastPrice(DEFAULT_LAST_PRICE)
            .volumeByHour(DEFAULT_VOLUME_BY_HOUR);
        return robot;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Robot createUpdatedEntity(EntityManager em) {
        Robot robot = new Robot()
            .type(UPDATED_TYPE)
            .lots(UPDATED_LOTS)
            .period(UPDATED_PERIOD)
            .operationType(UPDATED_OPERATION_TYPE)
            .operationCount(UPDATED_OPERATION_COUNT)
            .firstOperationDttm(UPDATED_FIRST_OPERATION_DTTM)
            .lastOperationDttm(UPDATED_LAST_OPERATION_DTTM)
            .lastPrice(UPDATED_LAST_PRICE)
            .volumeByHour(UPDATED_VOLUME_BY_HOUR);
        return robot;
    }

    @BeforeEach
    public void initTest() {
        robot = createEntity(em);
    }

    @Test
    @Transactional
    void createRobot() throws Exception {
        int databaseSizeBeforeCreate = robotRepository.findAll().size();
        // Create the Robot
        restRobotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isCreated());

        // Validate the Robot in the database
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeCreate + 1);
        Robot testRobot = robotList.get(robotList.size() - 1);
        assertThat(testRobot.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testRobot.getLots()).isEqualTo(DEFAULT_LOTS);
        assertThat(testRobot.getPeriod()).isEqualTo(DEFAULT_PERIOD);
        assertThat(testRobot.getOperationType()).isEqualTo(DEFAULT_OPERATION_TYPE);
        assertThat(testRobot.getOperationCount()).isEqualTo(DEFAULT_OPERATION_COUNT);
        assertThat(testRobot.getFirstOperationDttm()).isEqualTo(DEFAULT_FIRST_OPERATION_DTTM);
        assertThat(testRobot.getLastOperationDttm()).isEqualTo(DEFAULT_LAST_OPERATION_DTTM);
        assertThat(testRobot.getLastPrice()).isEqualTo(DEFAULT_LAST_PRICE);
        assertThat(testRobot.getVolumeByHour()).isEqualTo(DEFAULT_VOLUME_BY_HOUR);
    }

    @Test
    @Transactional
    void createRobotWithExistingId() throws Exception {
        // Create the Robot with an existing ID
        robot.setId(1L);

        int databaseSizeBeforeCreate = robotRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRobotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        // Validate the Robot in the database
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = robotRepository.findAll().size();
        // set the field null
        robot.setType(null);

        // Create the Robot, which fails.

        restRobotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLotsIsRequired() throws Exception {
        int databaseSizeBeforeTest = robotRepository.findAll().size();
        // set the field null
        robot.setLots(null);

        // Create the Robot, which fails.

        restRobotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPeriodIsRequired() throws Exception {
        int databaseSizeBeforeTest = robotRepository.findAll().size();
        // set the field null
        robot.setPeriod(null);

        // Create the Robot, which fails.

        restRobotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOperationTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = robotRepository.findAll().size();
        // set the field null
        robot.setOperationType(null);

        // Create the Robot, which fails.

        restRobotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOperationCountIsRequired() throws Exception {
        int databaseSizeBeforeTest = robotRepository.findAll().size();
        // set the field null
        robot.setOperationCount(null);

        // Create the Robot, which fails.

        restRobotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFirstOperationDttmIsRequired() throws Exception {
        int databaseSizeBeforeTest = robotRepository.findAll().size();
        // set the field null
        robot.setFirstOperationDttm(null);

        // Create the Robot, which fails.

        restRobotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastOperationDttmIsRequired() throws Exception {
        int databaseSizeBeforeTest = robotRepository.findAll().size();
        // set the field null
        robot.setLastOperationDttm(null);

        // Create the Robot, which fails.

        restRobotMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRobots() throws Exception {
        // Initialize the database
        robotRepository.saveAndFlush(robot);

        // Get all the robotList
        restRobotMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(robot.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].lots").value(hasItem(DEFAULT_LOTS)))
            .andExpect(jsonPath("$.[*].period").value(hasItem(DEFAULT_PERIOD.intValue())))
            .andExpect(jsonPath("$.[*].operationType").value(hasItem(DEFAULT_OPERATION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].operationCount").value(hasItem(DEFAULT_OPERATION_COUNT.intValue())))
            .andExpect(jsonPath("$.[*].firstOperationDttm").value(hasItem(DEFAULT_FIRST_OPERATION_DTTM.toString())))
            .andExpect(jsonPath("$.[*].lastOperationDttm").value(hasItem(DEFAULT_LAST_OPERATION_DTTM.toString())))
            .andExpect(jsonPath("$.[*].lastPrice").value(hasItem(DEFAULT_LAST_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].volumeByHour").value(hasItem(DEFAULT_VOLUME_BY_HOUR.intValue())));
    }

    @Test
    @Transactional
    void getRobot() throws Exception {
        // Initialize the database
        robotRepository.saveAndFlush(robot);

        // Get the robot
        restRobotMockMvc
            .perform(get(ENTITY_API_URL_ID, robot.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(robot.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.lots").value(DEFAULT_LOTS))
            .andExpect(jsonPath("$.period").value(DEFAULT_PERIOD.intValue()))
            .andExpect(jsonPath("$.operationType").value(DEFAULT_OPERATION_TYPE.toString()))
            .andExpect(jsonPath("$.operationCount").value(DEFAULT_OPERATION_COUNT.intValue()))
            .andExpect(jsonPath("$.firstOperationDttm").value(DEFAULT_FIRST_OPERATION_DTTM.toString()))
            .andExpect(jsonPath("$.lastOperationDttm").value(DEFAULT_LAST_OPERATION_DTTM.toString()))
            .andExpect(jsonPath("$.lastPrice").value(DEFAULT_LAST_PRICE.doubleValue()))
            .andExpect(jsonPath("$.volumeByHour").value(DEFAULT_VOLUME_BY_HOUR.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingRobot() throws Exception {
        // Get the robot
        restRobotMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRobot() throws Exception {
        // Initialize the database
        robotRepository.saveAndFlush(robot);

        int databaseSizeBeforeUpdate = robotRepository.findAll().size();

        // Update the robot
        Robot updatedRobot = robotRepository.findById(robot.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRobot are not directly saved in db
        em.detach(updatedRobot);
        updatedRobot
            .type(UPDATED_TYPE)
            .lots(UPDATED_LOTS)
            .period(UPDATED_PERIOD)
            .operationType(UPDATED_OPERATION_TYPE)
            .operationCount(UPDATED_OPERATION_COUNT)
            .firstOperationDttm(UPDATED_FIRST_OPERATION_DTTM)
            .lastOperationDttm(UPDATED_LAST_OPERATION_DTTM)
            .lastPrice(UPDATED_LAST_PRICE)
            .volumeByHour(UPDATED_VOLUME_BY_HOUR);

        restRobotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRobot.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedRobot))
            )
            .andExpect(status().isOk());

        // Validate the Robot in the database
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeUpdate);
        Robot testRobot = robotList.get(robotList.size() - 1);
        assertThat(testRobot.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testRobot.getLots()).isEqualTo(UPDATED_LOTS);
        assertThat(testRobot.getPeriod()).isEqualTo(UPDATED_PERIOD);
        assertThat(testRobot.getOperationType()).isEqualTo(UPDATED_OPERATION_TYPE);
        assertThat(testRobot.getOperationCount()).isEqualTo(UPDATED_OPERATION_COUNT);
        assertThat(testRobot.getFirstOperationDttm()).isEqualTo(UPDATED_FIRST_OPERATION_DTTM);
        assertThat(testRobot.getLastOperationDttm()).isEqualTo(UPDATED_LAST_OPERATION_DTTM);
        assertThat(testRobot.getLastPrice()).isEqualTo(UPDATED_LAST_PRICE);
        assertThat(testRobot.getVolumeByHour()).isEqualTo(UPDATED_VOLUME_BY_HOUR);
    }

    @Test
    @Transactional
    void putNonExistingRobot() throws Exception {
        int databaseSizeBeforeUpdate = robotRepository.findAll().size();
        robot.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRobotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, robot.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        // Validate the Robot in the database
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRobot() throws Exception {
        int databaseSizeBeforeUpdate = robotRepository.findAll().size();
        robot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRobotMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        // Validate the Robot in the database
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRobot() throws Exception {
        int databaseSizeBeforeUpdate = robotRepository.findAll().size();
        robot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRobotMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Robot in the database
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRobotWithPatch() throws Exception {
        // Initialize the database
        robotRepository.saveAndFlush(robot);

        int databaseSizeBeforeUpdate = robotRepository.findAll().size();

        // Update the robot using partial update
        Robot partialUpdatedRobot = new Robot();
        partialUpdatedRobot.setId(robot.getId());

        partialUpdatedRobot
            .type(UPDATED_TYPE)
            .period(UPDATED_PERIOD)
            .operationCount(UPDATED_OPERATION_COUNT)
            .firstOperationDttm(UPDATED_FIRST_OPERATION_DTTM)
            .lastOperationDttm(UPDATED_LAST_OPERATION_DTTM)
            .lastPrice(UPDATED_LAST_PRICE)
            .volumeByHour(UPDATED_VOLUME_BY_HOUR);

        restRobotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRobot.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRobot))
            )
            .andExpect(status().isOk());

        // Validate the Robot in the database
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeUpdate);
        Robot testRobot = robotList.get(robotList.size() - 1);
        assertThat(testRobot.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testRobot.getLots()).isEqualTo(DEFAULT_LOTS);
        assertThat(testRobot.getPeriod()).isEqualTo(UPDATED_PERIOD);
        assertThat(testRobot.getOperationType()).isEqualTo(DEFAULT_OPERATION_TYPE);
        assertThat(testRobot.getOperationCount()).isEqualTo(UPDATED_OPERATION_COUNT);
        assertThat(testRobot.getFirstOperationDttm()).isEqualTo(UPDATED_FIRST_OPERATION_DTTM);
        assertThat(testRobot.getLastOperationDttm()).isEqualTo(UPDATED_LAST_OPERATION_DTTM);
        assertThat(testRobot.getLastPrice()).isEqualTo(UPDATED_LAST_PRICE);
        assertThat(testRobot.getVolumeByHour()).isEqualTo(UPDATED_VOLUME_BY_HOUR);
    }

    @Test
    @Transactional
    void fullUpdateRobotWithPatch() throws Exception {
        // Initialize the database
        robotRepository.saveAndFlush(robot);

        int databaseSizeBeforeUpdate = robotRepository.findAll().size();

        // Update the robot using partial update
        Robot partialUpdatedRobot = new Robot();
        partialUpdatedRobot.setId(robot.getId());

        partialUpdatedRobot
            .type(UPDATED_TYPE)
            .lots(UPDATED_LOTS)
            .period(UPDATED_PERIOD)
            .operationType(UPDATED_OPERATION_TYPE)
            .operationCount(UPDATED_OPERATION_COUNT)
            .firstOperationDttm(UPDATED_FIRST_OPERATION_DTTM)
            .lastOperationDttm(UPDATED_LAST_OPERATION_DTTM)
            .lastPrice(UPDATED_LAST_PRICE)
            .volumeByHour(UPDATED_VOLUME_BY_HOUR);

        restRobotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRobot.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRobot))
            )
            .andExpect(status().isOk());

        // Validate the Robot in the database
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeUpdate);
        Robot testRobot = robotList.get(robotList.size() - 1);
        assertThat(testRobot.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testRobot.getLots()).isEqualTo(UPDATED_LOTS);
        assertThat(testRobot.getPeriod()).isEqualTo(UPDATED_PERIOD);
        assertThat(testRobot.getOperationType()).isEqualTo(UPDATED_OPERATION_TYPE);
        assertThat(testRobot.getOperationCount()).isEqualTo(UPDATED_OPERATION_COUNT);
        assertThat(testRobot.getFirstOperationDttm()).isEqualTo(UPDATED_FIRST_OPERATION_DTTM);
        assertThat(testRobot.getLastOperationDttm()).isEqualTo(UPDATED_LAST_OPERATION_DTTM);
        assertThat(testRobot.getLastPrice()).isEqualTo(UPDATED_LAST_PRICE);
        assertThat(testRobot.getVolumeByHour()).isEqualTo(UPDATED_VOLUME_BY_HOUR);
    }

    @Test
    @Transactional
    void patchNonExistingRobot() throws Exception {
        int databaseSizeBeforeUpdate = robotRepository.findAll().size();
        robot.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRobotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, robot.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        // Validate the Robot in the database
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRobot() throws Exception {
        int databaseSizeBeforeUpdate = robotRepository.findAll().size();
        robot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRobotMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isBadRequest());

        // Validate the Robot in the database
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRobot() throws Exception {
        int databaseSizeBeforeUpdate = robotRepository.findAll().size();
        robot.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRobotMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(robot))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Robot in the database
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRobot() throws Exception {
        // Initialize the database
        robotRepository.saveAndFlush(robot);

        int databaseSizeBeforeDelete = robotRepository.findAll().size();

        // Delete the robot
        restRobotMockMvc
            .perform(delete(ENTITY_API_URL_ID, robot.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Robot> robotList = robotRepository.findAll();
        assertThat(robotList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
