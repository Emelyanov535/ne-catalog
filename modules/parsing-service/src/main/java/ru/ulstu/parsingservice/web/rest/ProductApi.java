package ru.ulstu.parsingservice.web.rest;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.ulstu.parsingservice.enumeration.Category;
import ru.ulstu.parsingservice.enumeration.Marketplace;
import ru.ulstu.parsingservice.service.ProductService;
import ru.ulstu.parsingservice.service.dto.PriceHistoryDto;
import ru.ulstu.parsingservice.service.dto.ProductDto;
import ru.ulstu.parsingservice.service.dto.ProductsPageDto;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductApi {

    private final ProductService productService;

    @GetMapping("/info")
    public ResponseEntity<ProductDto> getProductInfo(@RequestParam String productUrl) {
        return ResponseEntity.ok(productService.findByUrl(productUrl));
    }

    @GetMapping("/price-history")
    public ResponseEntity<PriceHistoryDto> getProductPriceHistoryByRange(@RequestParam String productUrl,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                                                         String zoneOffset) {
        ZoneOffset zone = ZoneOffset.of(zoneOffset);
        ZonedDateTime fromDateTime = from.atStartOfDay(zone);
        ZonedDateTime toDateTime = to.atStartOfDay(zone);
        return ResponseEntity.ok(productService.findPriceHistoryByRange(productUrl, fromDateTime, toDateTime));
    }

    @GetMapping
    public ResponseEntity<ProductsPageDto> getAllProductsByCategoryAndPage(Marketplace marketplace,
                                                                           Category category,
                                                                           Pageable pageable) {
        return ResponseEntity.ok(productService.findAllProductsByPage(marketplace, category, pageable));
    }

}
