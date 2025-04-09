package ru.necatalog.search.web.controller;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.search.dto.FilterData;
import ru.necatalog.search.dto.SearchResult;
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
    public List<SearchResult> search(Category category,
                                     String searchQuery,
                                     String sortAttribute,
                                     String sortDir,
                                     Integer page,
                                     Integer size,
                                     Map<String, String> attributeValues) {
        return searchService.search(category, searchQuery, sortAttribute, sortDir, page, size, attributeValues);
    }

}
