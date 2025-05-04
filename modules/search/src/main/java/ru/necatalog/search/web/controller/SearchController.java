package ru.necatalog.search.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.search.dto.FilterData;
import ru.necatalog.search.dto.SearchResults;
import ru.necatalog.search.service.SearchService;
import ru.necatalog.search.web.api.SearchApi;

@RestController
@RequiredArgsConstructor
public class SearchController implements SearchApi {

    private final SearchService searchService;

    @Override
    public FilterData getAvailableFilters(Category category) {
        return searchService.getAvailableFilters(category);
    }

    @Override
    public SearchResults search(Category category,
                                String searchQuery,
                                String sortAttribute,
                                String sortDir,
                                Integer page,
                                Integer size,
                                Integer startPrice,
                                Integer endPrice,
                                MultiValueMap<String, String> attributeValues) {
        attributeValues.remove("page");
        attributeValues.remove("size");
        attributeValues.remove("startPrice");
        attributeValues.remove("size");
        attributeValues.remove("endPrice");
        attributeValues.remove("sortDir");
        attributeValues.remove("sortAttribute");
        attributeValues.remove("sortBy");
        attributeValues.remove("searchQuery");
        attributeValues.remove("category");
        return searchService.search(category, searchQuery, sortAttribute, sortDir, page, size, attributeValues, startPrice, endPrice);
    }

}
