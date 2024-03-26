package ru.ani.scan.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
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
import ru.ani.scan.domain.InstrumentType;
import ru.ani.scan.repository.InstrumentTypeRepository;

/**
 * Integration tests for the {@link InstrumentTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InstrumentTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/instrument-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InstrumentTypeRepository instrumentTypeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInstrumentTypeMockMvc;

    private InstrumentType instrumentType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstrumentType createEntity(EntityManager em) {
        InstrumentType instrumentType = new InstrumentType().name(DEFAULT_NAME);
        return instrumentType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InstrumentType createUpdatedEntity(EntityManager em) {
        InstrumentType instrumentType = new InstrumentType().name(UPDATED_NAME);
        return instrumentType;
    }

    @BeforeEach
    public void initTest() {
        instrumentType = createEntity(em);
    }

    @Test
    @Transactional
    void createInstrumentType() throws Exception {
        int databaseSizeBeforeCreate = instrumentTypeRepository.findAll().size();
        // Create the InstrumentType
        restInstrumentTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(instrumentType))
            )
            .andExpect(status().isCreated());

        // Validate the InstrumentType in the database
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeCreate + 1);
        InstrumentType testInstrumentType = instrumentTypeList.get(instrumentTypeList.size() - 1);
        assertThat(testInstrumentType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createInstrumentTypeWithExistingId() throws Exception {
        // Create the InstrumentType with an existing ID
        instrumentType.setId(1L);

        int databaseSizeBeforeCreate = instrumentTypeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInstrumentTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(instrumentType))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstrumentType in the database
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = instrumentTypeRepository.findAll().size();
        // set the field null
        instrumentType.setName(null);

        // Create the InstrumentType, which fails.

        restInstrumentTypeMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(instrumentType))
            )
            .andExpect(status().isBadRequest());

        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInstrumentTypes() throws Exception {
        // Initialize the database
        instrumentTypeRepository.saveAndFlush(instrumentType);

        // Get all the instrumentTypeList
        restInstrumentTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(instrumentType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getInstrumentType() throws Exception {
        // Initialize the database
        instrumentTypeRepository.saveAndFlush(instrumentType);

        // Get the instrumentType
        restInstrumentTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, instrumentType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(instrumentType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingInstrumentType() throws Exception {
        // Get the instrumentType
        restInstrumentTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInstrumentType() throws Exception {
        // Initialize the database
        instrumentTypeRepository.saveAndFlush(instrumentType);

        int databaseSizeBeforeUpdate = instrumentTypeRepository.findAll().size();

        // Update the instrumentType
        InstrumentType updatedInstrumentType = instrumentTypeRepository.findById(instrumentType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInstrumentType are not directly saved in db
        em.detach(updatedInstrumentType);
        updatedInstrumentType.name(UPDATED_NAME);

        restInstrumentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedInstrumentType.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedInstrumentType))
            )
            .andExpect(status().isOk());

        // Validate the InstrumentType in the database
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeUpdate);
        InstrumentType testInstrumentType = instrumentTypeList.get(instrumentTypeList.size() - 1);
        assertThat(testInstrumentType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingInstrumentType() throws Exception {
        int databaseSizeBeforeUpdate = instrumentTypeRepository.findAll().size();
        instrumentType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstrumentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, instrumentType.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(instrumentType))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstrumentType in the database
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInstrumentType() throws Exception {
        int databaseSizeBeforeUpdate = instrumentTypeRepository.findAll().size();
        instrumentType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstrumentTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(instrumentType))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstrumentType in the database
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInstrumentType() throws Exception {
        int databaseSizeBeforeUpdate = instrumentTypeRepository.findAll().size();
        instrumentType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstrumentTypeMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(instrumentType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InstrumentType in the database
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInstrumentTypeWithPatch() throws Exception {
        // Initialize the database
        instrumentTypeRepository.saveAndFlush(instrumentType);

        int databaseSizeBeforeUpdate = instrumentTypeRepository.findAll().size();

        // Update the instrumentType using partial update
        InstrumentType partialUpdatedInstrumentType = new InstrumentType();
        partialUpdatedInstrumentType.setId(instrumentType.getId());

        partialUpdatedInstrumentType.name(UPDATED_NAME);

        restInstrumentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstrumentType.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInstrumentType))
            )
            .andExpect(status().isOk());

        // Validate the InstrumentType in the database
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeUpdate);
        InstrumentType testInstrumentType = instrumentTypeList.get(instrumentTypeList.size() - 1);
        assertThat(testInstrumentType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateInstrumentTypeWithPatch() throws Exception {
        // Initialize the database
        instrumentTypeRepository.saveAndFlush(instrumentType);

        int databaseSizeBeforeUpdate = instrumentTypeRepository.findAll().size();

        // Update the instrumentType using partial update
        InstrumentType partialUpdatedInstrumentType = new InstrumentType();
        partialUpdatedInstrumentType.setId(instrumentType.getId());

        partialUpdatedInstrumentType.name(UPDATED_NAME);

        restInstrumentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstrumentType.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInstrumentType))
            )
            .andExpect(status().isOk());

        // Validate the InstrumentType in the database
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeUpdate);
        InstrumentType testInstrumentType = instrumentTypeList.get(instrumentTypeList.size() - 1);
        assertThat(testInstrumentType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingInstrumentType() throws Exception {
        int databaseSizeBeforeUpdate = instrumentTypeRepository.findAll().size();
        instrumentType.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstrumentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, instrumentType.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(instrumentType))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstrumentType in the database
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInstrumentType() throws Exception {
        int databaseSizeBeforeUpdate = instrumentTypeRepository.findAll().size();
        instrumentType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstrumentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(instrumentType))
            )
            .andExpect(status().isBadRequest());

        // Validate the InstrumentType in the database
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInstrumentType() throws Exception {
        int databaseSizeBeforeUpdate = instrumentTypeRepository.findAll().size();
        instrumentType.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstrumentTypeMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(instrumentType))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the InstrumentType in the database
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInstrumentType() throws Exception {
        // Initialize the database
        instrumentTypeRepository.saveAndFlush(instrumentType);

        int databaseSizeBeforeDelete = instrumentTypeRepository.findAll().size();

        // Delete the instrumentType
        restInstrumentTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, instrumentType.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<InstrumentType> instrumentTypeList = instrumentTypeRepository.findAll();
        assertThat(instrumentTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
