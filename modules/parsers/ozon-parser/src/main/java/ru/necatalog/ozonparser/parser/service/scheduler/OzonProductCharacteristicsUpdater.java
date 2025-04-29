package ru.necatalog.ozonparser.parser.service.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import ru.necatalog.ozonparser.parser.service.parsing.OzonParsingService;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.repository.ProductRepository;

@Slf4j
@RequiredArgsConstructor
public class OzonProductCharacteristicsUpdater {

    private final OzonParsingService ozonParsingService;

    private final ProductRepository productRepository;

    //@Scheduled(cron = "0 0 */6 * * *")
    @Scheduled(fixedRate = 60000000)
    public void updateOzonProducts() {
        productRepository.findWithoutCharacteristics().forEach(product -> {
            if (product.getCategory() == Category.SMARTPHONE) return;
            for (int i = 0; i < 5; ++i) {
                try {
                    log.info("Получаем характеристик для {}", product.getUrl());
                    ozonParsingService.processAttributePage(product);
                    break;
                } catch (Exception e) {
                    log.error("Не удалось получить характеристики {}", product.getUrl());
                }
            }
        });
    }

}
