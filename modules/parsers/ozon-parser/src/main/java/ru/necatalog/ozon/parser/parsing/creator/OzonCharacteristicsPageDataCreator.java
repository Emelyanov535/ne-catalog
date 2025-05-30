package ru.necatalog.ozon.parser.parsing.creator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.necatalog.ozon.parser.parsing.dto.characteristics.OzonCharacteristicsPageData;

@Slf4j
@RequiredArgsConstructor
public class OzonCharacteristicsPageDataCreator implements PageDataCreator<OzonCharacteristicsPageData> {

    private final ObjectMapper objectMapper;

    public OzonCharacteristicsPageData create(String jsonInHtml) throws JsonProcessingException {
        String json = getJson(jsonInHtml);
        if (json == null) {
            return null;
        }
        return objectMapper.readValue(json, OzonCharacteristicsPageData.class);
    }

}
