package ru.ulstu.parsingservice.ozon_parser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ulstu.parsingservice.ozon_parser.enumeration.OzonCategory;

@Slf4j
@Service
@RequiredArgsConstructor
public class OzonService {

    public OzonCategory[] getCategories() {
        return OzonCategory.values();
    }

}
