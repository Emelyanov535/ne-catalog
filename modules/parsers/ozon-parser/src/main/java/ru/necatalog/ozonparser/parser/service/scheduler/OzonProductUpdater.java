package ru.necatalog.ozonparser.parser.service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import ru.necatalog.ozonparser.parser.service.parsing.OzonParsingService;
import ru.necatalog.persistence.repository.ProductRepository;

@RequiredArgsConstructor
public class OzonProductUpdater {

    private final OzonParsingService ozonParsingService;

    private final ProductRepository productRepository;

    //@Scheduled(cron = "0 0 */6 * * *")
    @Scheduled(fixedRate = 60000000)
    public void updateOzonProducts() {
        ozonParsingService.startProcessing();
    }

}
