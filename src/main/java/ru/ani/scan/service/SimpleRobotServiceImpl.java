package ru.ani.scan.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.ani.scan.domain.Robot;
import ru.ani.scan.utils.MyListComparator;

@Service
@Deprecated
public class SimpleRobotServiceImpl implements SimpleRobotService {

    private static List<Robot> robotStore = new ArrayList<>();

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
