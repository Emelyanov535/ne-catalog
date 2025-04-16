package ru.necatalog.analogfinder.web.api;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.necatalog.analogfinder.dto.AnalogResult;
import ru.necatalog.persistence.enumeration.AttributeGroup;

@RequestMapping("/find-analog")
@Tag(name = "Поиск аналогов товара")
public interface AnalogFinderApi {

    @GetMapping
    @Operation(summary = "Получить аналоги товара")
    List<AnalogResult> findAnalogs(@RequestParam("productUrl") String productUrl,
                                   @RequestParam("attributeGroups") List<String> attributeGroups);

    @GetMapping("/attributes")
    @Operation(summary = "Получить атрибуты для поиска аналогов")
    Map<String, List<String>> getAttributeGroups(@RequestParam("productUrl") String productUrl);

}
