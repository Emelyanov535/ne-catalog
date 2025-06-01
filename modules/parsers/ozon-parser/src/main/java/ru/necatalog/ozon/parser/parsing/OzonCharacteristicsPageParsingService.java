package ru.necatalog.ozon.parser.parsing;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.necatalog.ozon.parser.parsing.creator.OzonCharacteristicsPageDataCreator;
import ru.necatalog.ozon.parser.parsing.dto.characteristics.CharacteristicsHolder;
import ru.necatalog.ozon.parser.parsing.dto.characteristics.OzonCharacteristicsPageData;
import ru.necatalog.ozon.parser.parsing.enumeration.OzonCategory;
import ru.necatalog.ozon.parser.parsing.processor.AttributeProcessor;
import ru.necatalog.ozon.parser.service.dto.ParseOzonCharacteristicPayload;
import ru.necatalog.ozon.parser.utils.OzonConsts;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.repository.ProductAttributeRepository;

@Slf4j
@RequiredArgsConstructor
public class OzonCharacteristicsPageParsingService {

    private static final Map<OzonCategory, AttributeProcessor> attributeProcessorsMap = new EnumMap<>(OzonCategory.class);

    private final ProductAttributeRepository productAttributeRepository;

    private final List<AttributeProcessor> attributeProcessors;

    private final OzonPageFetcher pageFetcher;

    private final OzonCharacteristicsPageDataCreator characteristicsPageDataCreator;

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        attributeProcessors.forEach(processor ->
            attributeProcessorsMap.put(processor.getCategory(), processor));
    }

    public void processAttributePage(ParseOzonCharacteristicPayload payload)
        throws JsonProcessingException, InterruptedException {
        String pageUrl = OzonConsts.OZON_API_LINK
            + payload.getProductUrl().replace(OzonConsts.OZON_MAIN_LINK, "")
            + "?layout_container=pdpPage2column&layout_page_index=2";
        String jsonInHtml = pageFetcher.fetchPageJson(pageUrl, 200);
        OzonCharacteristicsPageData characteristicsPageData = characteristicsPageDataCreator.create(jsonInHtml);
        CharacteristicsHolder characteristicsHolder = objectMapper.readValue(
            characteristicsPageData.getWidgetStates().getWebCharacteristicsValue(),
            CharacteristicsHolder.class);
        List<ProductAttributeEntity> attributeEntities = attributeProcessorsMap
            .get(OzonCategory.valueOf(payload.getCategory().name()))
            .process(characteristicsHolder.getCharacteristics(), payload.getProductUrl());
        productAttributeRepository.saveAll(attributeEntities);
        log.info("Сохранили {} атрибутов", attributeEntities.size());
    }

}
