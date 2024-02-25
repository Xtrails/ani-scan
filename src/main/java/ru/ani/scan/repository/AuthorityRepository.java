package ru.ani.scan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ani.scan.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
