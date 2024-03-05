package ru.ani.scan.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.ani.scan.domain.Robot;

public interface SimpleRobotService {
    Page<Robot> findAll(Pageable pageable);
}
