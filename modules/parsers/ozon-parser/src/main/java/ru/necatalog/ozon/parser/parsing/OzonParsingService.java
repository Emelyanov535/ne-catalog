package ru.necatalog.ozon.parser.parsing;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.necatalog.ozon.parser.config.properties.OzonParserProperties;
import ru.necatalog.ozon.parser.parsing.enumeration.OzonCategory;
import ru.necatalog.ozon.parser.service.dto.ParseOzonCharacteristicPayload;
import ru.necatalog.persistence.entity.DelayedTaskEntity;
import ru.necatalog.persistence.enumeration.DelayedTaskStatus;
import ru.necatalog.persistence.enumeration.DelayedTaskType;
import ru.necatalog.persistence.service.DelayedTaskService;

@Slf4j
public class OzonParsingService {

    private final ExecutorService pageExecutorService;

    private final OzonCatalogPageParsingService categoryPageParsingService;

    private final DelayedTaskService delayedTaskService;

    private final ObjectMapper objectMapper;

    private final OzonCharacteristicsPageParsingService characteristicsPageParsingService;

    public OzonParsingService(OzonParserProperties ozonConfigProperties,
                              OzonCatalogPageParsingService categoryPageParsingService,
                              DelayedTaskService delayedTaskService,
                              ObjectMapper objectMapper, OzonCharacteristicsPageParsingService characteristicsPageParsingService) {
        this.categoryPageParsingService = categoryPageParsingService;
        this.pageExecutorService = Executors.newFixedThreadPool(ozonConfigProperties.getMaxThreads());
        this.delayedTaskService = delayedTaskService;
        this.objectMapper = objectMapper;
        this.characteristicsPageParsingService = characteristicsPageParsingService;
    }

    public void parseCatalog() {
        for (OzonCategory category : OzonCategory.values()) {
            log.info("Начало обработки категории {}", category);
            pageExecutorService.submit(() -> categoryPageParsingService.parse(category));
        }
    }

    @SneakyThrows
    public void parseCharacteristics() {
        List<DelayedTaskEntity> tasks = delayedTaskService.findByType(DelayedTaskType.PARSE_OZON_CHARACTERISTIC);
        log.info("Размер актуальных задач {}", tasks.size());
        for (var task : tasks) {
            try {
                task.setStatus(DelayedTaskStatus.IN_PROCESS);
                delayedTaskService.update(task);
                ParseOzonCharacteristicPayload payload =
                    objectMapper.readValue(task.getPayload(), ParseOzonCharacteristicPayload.class);
                log.info("Получаем характеристики для {}", payload.getProductUrl());
                characteristicsPageParsingService.processAttributePage(payload);
                task.setStatus(DelayedTaskStatus.COMPLETED);
                delayedTaskService.delete(task);
            } catch (Exception e) {
                task.setStatus(DelayedTaskStatus.IN_PROCESS);
                delayedTaskService.update(task);
                log.error(e.getMessage(), e);
            }
        }
    }
}