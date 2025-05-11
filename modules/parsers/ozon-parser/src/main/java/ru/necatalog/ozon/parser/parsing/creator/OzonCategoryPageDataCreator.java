package ru.necatalog.ozon.parser.parsing.creator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.necatalog.ozon.parser.parsing.dto.catalog.OzonCatalogPageData;

@Slf4j
@RequiredArgsConstructor
public class OzonCategoryPageDataCreator implements PageDataCreator<OzonCatalogPageData> {

    private final ObjectMapper objectMapper;

    public OzonCatalogPageData create(String jsonInHtml) throws JsonProcessingException {
        String json = getJson(jsonInHtml);
        return objectMapper.readValue(json, OzonCatalogPageData.class);
    }

}

