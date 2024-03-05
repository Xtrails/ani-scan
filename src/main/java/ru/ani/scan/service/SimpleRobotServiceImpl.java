package ru.ani.scan.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.ani.scan.domain.Instrument;
import ru.ani.scan.domain.Robot;
import ru.ani.scan.domain.enumeration.OperationType;
import ru.ani.scan.domain.enumeration.RobotType;
import ru.ani.scan.utils.MyListComparator;

@Service
public class SimpleRobotServiceImpl implements SimpleRobotService {

    private static List<Robot> robotStore = new ArrayList<>();

    static {
        robotStore.add(
            new Robot(
                1L,
                RobotType.SIMPLE,
                "1,2",
                1L,
                OperationType.BUY,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1D,
                1L,
                new Instrument(1L, "MGNT")
            )
        );
        robotStore.add(
            new Robot(
                2L,
                RobotType.SIMPLE,
                "2,3",
                1L,
                OperationType.BUY,
                2L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                2D,
                1L,
                new Instrument(1L, "MGNT")
            )
        );
    }

    @Override
    public Page<Robot> findAll(Pageable pageable) {
        return toPage(pageable, robotStore);
    }

    private Page<Robot> toPage(Pageable pageable, List<Robot> listRobots) {
        var sort = pageable.getSort();
        var isAscending = pageable.getSort().stream().findFirst().map(Sort.Order::isAscending);
        var isDescending = pageable.getSort().stream().findFirst().map(Sort.Order::isDescending);
        var property = pageable.getSort().stream().findFirst().map(Sort.Order::getProperty).filter(StringUtils::hasText);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listRobots.size());
        if (
            sort.isSorted() &&
            property.isPresent() &&
            ((isAscending.isPresent() && isAscending.get()) || (isDescending.isPresent() && isDescending.get()))
        ) {
            if (isAscending.isPresent() && isAscending.get()) {
                var comparator = new MyListComparator(property.get(), "asc");
                listRobots.sort(comparator);
            } else if (isDescending.get()) {
                var comparator = new MyListComparator(property.get(), "desc");
                listRobots.sort(comparator);
            }
        }
        return new PageImpl<>(listRobots.subList(start, end), pageable, listRobots.size());
    }
}
