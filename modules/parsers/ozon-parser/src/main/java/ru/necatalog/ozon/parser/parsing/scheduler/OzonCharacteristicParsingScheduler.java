package ru.necatalog.ozon.parser.parsing.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import ru.necatalog.ozon.parser.parsing.OzonParsingService;

@Slf4j
@RequiredArgsConstructor
public class OzonCharacteristicParsingScheduler {

    private final OzonParsingService ozonParsingService;

    //@Scheduled(cron = "0 30 * * * *")
    //@PostConstruct
    public void updateOzonProducts() {
        ozonParsingService.parseCharacteristics();
    }

}
