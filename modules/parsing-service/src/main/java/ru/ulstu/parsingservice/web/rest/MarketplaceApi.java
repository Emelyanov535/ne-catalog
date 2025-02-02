package ru.ulstu.parsingservice.web.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ulstu.parsingservice.enumeration.Marketplace;

@RestController
@RequestMapping("/api/v1/marketplaces")
@RequiredArgsConstructor
public class MarketplaceApi {

    @GetMapping
    public ResponseEntity<Marketplace[]> getMarketplace() {
        return ResponseEntity.ok(Marketplace.values());
    }

}
