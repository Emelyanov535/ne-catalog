package ru.necatalog.ozonparser.parser.service.parsing;

import java.util.List;

import ru.necatalog.ozonparser.parser.service.dto.ParsedData;
import ru.necatalog.ozonparser.parser.service.page.OzonCategoryPage;
import ru.necatalog.persistence.enumeration.Category;

public class OzonPageParser {

    public List<ParsedData> parseProductsFromCategoryPage(String pageSource,
                                                          Category category) {
        OzonCategoryPage categoryPage = new OzonCategoryPage(pageSource);
        return categoryPage.getProducts(category);
    }

}
