package ru.ani.scan.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.ani.scan.domain.Robot;

/**
 * Spring Data JPA repository for the Robot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RobotRepository extends JpaRepository<Robot, Long> {}
