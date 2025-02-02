package ru.ulstu.parsingservice.wildberries_parser.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ulstu.parsingservice.wildberries_parser.service.ParsingService;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "marketplace.wildberries", name = "status", havingValue = "true")
public class WildberriesProductUpdater {

    private final ParsingService parsingService;

    @Scheduled(fixedRate = 3600000)
    public void updateWildberriesProducts() {
        log.info("Начинаем отладку...");
        parsingService.parse();
        log.info("Заканчиваем отладку...");
    }
}
