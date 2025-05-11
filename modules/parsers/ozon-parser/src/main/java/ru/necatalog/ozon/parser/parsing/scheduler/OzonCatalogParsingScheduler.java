package ru.necatalog.ozon.parser.parsing.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import ru.necatalog.ozon.parser.parsing.OzonParsingService;

@RequiredArgsConstructor
public class OzonCatalogParsingScheduler {

    private final OzonParsingService ozonParsingService;

    //@Scheduled(cron = "0 0 */4 * * *")
    //@PostConstruct
    public void updateOzonProducts() {
        ozonParsingService.parseCatalog();
    }

}
