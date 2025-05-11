package ru.necatalog.ozon.parser.parsing.dto.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OzonCatalogPageData {

    private WidgetStates widgetStates;

    private String prevPage;

    private String nextPage;

}