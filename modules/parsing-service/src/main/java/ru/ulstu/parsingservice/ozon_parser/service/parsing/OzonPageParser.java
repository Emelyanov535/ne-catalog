package ru.ulstu.parsingservice.ozon_parser.service.parsing;

import java.util.List;

import org.springframework.stereotype.Service;
import ru.ulstu.parsingservice.enumeration.Category;
import ru.ulstu.parsingservice.ozon_parser.service.dto.ParsedData;
import ru.ulstu.parsingservice.ozon_parser.service.page.OzonCategoryPage;

@Service
public class OzonPageParser {

    public List<ParsedData> parseProductsFromCategoryPage(String pageSource,
                                                          Category category) {
        OzonCategoryPage categoryPage = new OzonCategoryPage(pageSource);
        return categoryPage.getProducts(category);
    }

}
