package ru.necatalog.ozonparser.parser.service.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import ru.necatalog.ozonparser.parser.service.parsing.OzonParsingService;
import ru.necatalog.persistence.repository.ProductRepository;

@RequiredArgsConstructor
public class OzonProductUpdater {

    private final OzonParsingService ozonParsingService;

    private final ProductRepository productRepository;

    @Scheduled(fixedDelay = 6000000)
    public void updateOzonProducts() {
        ozonParsingService.startProcessing();
        //ozonParsingService.processAttributePage(
            //productRepository.findAll(Pageable.ofSize(1)).get().findFirst().orElse(null));
    }

}
