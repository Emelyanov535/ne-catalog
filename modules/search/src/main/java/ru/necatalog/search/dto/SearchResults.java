package ru.necatalog.search.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.necatalog.persistence.enumeration.Marketplace;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResults {

    List<SearchResult> results;

    long maxPage;

}
