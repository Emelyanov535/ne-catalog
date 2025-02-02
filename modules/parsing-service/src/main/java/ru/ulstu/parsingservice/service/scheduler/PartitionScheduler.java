package ru.ulstu.parsingservice.service.scheduler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ulstu.parsingservice.ozon_parser.service.PartitionService;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile("postgres_stat")
public class PartitionScheduler {
    
    private final PartitionService partitionService;
    private final DateTimeFormatter partitionDateTimeFormatter;

    @PostConstruct
    public void init() {
        checkAndCreateMonthlyPartitions();
    }

    @Scheduled(cron = "@monthly")
    public void checkAndCreatePartitionsMonthly() {
        checkAndCreateMonthlyPartitions();
    }

    public void checkAndCreateMonthlyPartitions() {
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate nextMonth = currentMonth.plusMonths(1);

        String currentMonthPartition = getPartitionName(currentMonth);
        String nextMonthPartition = getPartitionName(nextMonth);

        checkAndCreatePartition(currentMonthPartition, currentMonth);
        checkAndCreatePartition(nextMonthPartition, nextMonth);
    }

    private String getPartitionName(LocalDate date) {
        return "price_history_" + partitionDateTimeFormatter.format(date);
    }

    private void checkAndCreatePartition(String partitionName, LocalDate startDate) {
        if (!partitionService.checkPartitionExists(partitionName)) {
            LocalDate endDate = startDate.plusMonths(1);
            partitionService.createPartition(partitionName, startDate.toString(), endDate.toString());
            log.info("Партиция {} создана для диапазона: {} - {} ", partitionName, startDate, endDate);
        } else {
            log.info("Партиция {} уже существует.", partitionName);
        }
    }

}
