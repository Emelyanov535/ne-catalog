package ru.ulstu.parsingservice.web.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ulstu.parsingservice.enumeration.Category;
import ru.ulstu.parsingservice.enumeration.Marketplace;
import ru.ulstu.parsingservice.ozon_parser.service.OzonService;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryApi {

    private final OzonService ozonService;

    @GetMapping
    public ResponseEntity<?> getCategories(Marketplace marketplace) {
        if (Marketplace.OZON.equals(marketplace)) {
            return ResponseEntity.ok(ozonService.getCategories());
        }
        return ResponseEntity.ok(Category.values());
    }

}
