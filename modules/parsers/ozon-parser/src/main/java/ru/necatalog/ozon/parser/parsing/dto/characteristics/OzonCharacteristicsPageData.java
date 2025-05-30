package ru.necatalog.ozon.parser.parsing.dto.characteristics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OzonCharacteristicsPageData {

    private WidgetStates widgetStates;

}