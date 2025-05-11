package ru.necatalog.ozon.parser.parsing.dto.catalog;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class WidgetStates {

    private String productsInfo;

    private String paginator;

    @JsonAnySetter
    public void setDynamicField(String key, Object value) {
        if (key.startsWith("tileGridDesktop")) {
            this.productsInfo = value.toString();
        }
        if (key.startsWith("infiniteVirtualPaginator")) {
            this.paginator = value.toString();
        }
    }

}