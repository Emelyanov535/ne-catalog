package ru.ulstu.parsingservice.ozon_parser.service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ulstu.parsingservice.ozon_parser.service.parsing.OzonParsingService;

@Service
@RequiredArgsConstructor
@Profile("ozon")
public class OzonProductUpdater {

    private final OzonParsingService ozonParsingService;

    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void updateOzonProducts() {
        ozonParsingService.startProcessing();
    }

}
