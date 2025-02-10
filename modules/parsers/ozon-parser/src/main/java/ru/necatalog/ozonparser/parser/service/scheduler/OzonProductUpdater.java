package ru.necatalog.ozonparser.parser.service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import ru.necatalog.ozonparser.parser.service.parsing.OzonParsingService;

@RequiredArgsConstructor
public class OzonProductUpdater {

    private final OzonParsingService ozonParsingService;

    @Scheduled(cron = "0 0 0,6,12,18 * * *")
    public void updateOzonProducts() {
        ozonParsingService.startProcessing();
    }

}
