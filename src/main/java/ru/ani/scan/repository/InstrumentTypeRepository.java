package ru.ani.scan.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.ani.scan.domain.InstrumentType;

/**
 * Spring Data JPA repository for the InstrumentType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface InstrumentTypeRepository extends JpaRepository<InstrumentType, Long> {}
