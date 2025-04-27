package ru.necatalog.wildberriesparser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "wildberries", name = "status", havingValue = "true")
public class WildberriesProductUpdater {

    private final ParsingService parsingService;

//    @Scheduled(cron = "0 0 * * * *")
    @Scheduled(fixedRate = 3600000)
    public void updateWildberriesProducts() {
        log.info("Начинаем отладку...");
        parsingService.parse();
        log.info("Заканчиваем отладку...");
//        parsingService.parseAttributes();
    }
}
