package ru.necatalog.ozon.parser.parsing.dto.characteristics;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WidgetStates {

    @JsonAlias({"webCharacteristics-3282540-pdpPage2column-2"})
    private String webCharacteristicsValue;

}