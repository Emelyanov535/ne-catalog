package ru.necatalog.ozon.parser.parsing.dto.catalog;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class OzonCatalogPaginator {

    private String prevPage;

    private String nextPage;



}
