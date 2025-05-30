package ru.necatalog.ozon.parser.parsing.page;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import ru.necatalog.ozon.parser.parsing.dto.characteristics.Characteristic;

@Slf4j
public class OzonAttributePage {

    private final String jsonData;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public OzonAttributePage(String pageHtml) {
        Element jsonBlock = Jsoup.parse(pageHtml).select("pre").first();
        if (jsonBlock == null) {
            log.warn(pageHtml);
            log.error("Переданный html не содержит данных с атрибутами");
        }
        this.jsonData = jsonBlock.text();
    }

    public List<Characteristic> getCharacteristics()
        throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonData, WebCharacteristics.class)
            .getWidgetStates().getWebCharacteristicsValue().getCharacteristics();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class WebCharacteristics {

        private WidgetStates widgetStates;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class WidgetStates {

            private CharacteristicsWrapper webCharacteristicsValue;

            @JsonAnySetter
            public void setDynamicField(String key, Object value) throws JsonProcessingException {
                if (key.startsWith("webCharacteristics")) {
                    webCharacteristicsValue = OBJECT_MAPPER.readValue(value.toString(), CharacteristicsWrapper.class);
                }
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            private static class CharacteristicsWrapper {

                private List<Characteristic> characteristics;

            }

        }

    }

}
