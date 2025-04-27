package ru.necatalog.search.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.search.dto.FilterData;
import ru.necatalog.search.dto.SearchResults;

@RequestMapping("/search")
@Tag(name = "Поиск и фильтрация товара")
public interface SearchApi {

    @GetMapping("/{category}/filters")
    @Operation(summary = "Получить доступные фильтры для категории")
    FilterData getAvailableFilters(@PathVariable("category") Category category);

    @GetMapping("/{category}")
    @Operation(summary = "Найти товары")
    SearchResults search(@PathVariable("category") Category category,
                         @RequestParam("searchQuery") String searchQuery,
                         @RequestParam(value = "sortBy", required = false) String sortAttribute,
                         @RequestParam(value = "sortDir", required = false) String sortDir,
                         @RequestParam("page") Integer page,
                         @RequestParam("size") Integer size,
                         @RequestParam(value = "startPrice", required = false) Integer startPrice,
                         @RequestParam(value = "endPrice", required = false) Integer endPrice,
                         @RequestParam MultiValueMap<String, String> attributeValues);

}
