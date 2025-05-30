package ru.necatalog.ozon.parser.parsing;

import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.necatalog.ozon.parser.parsing.dto.ParsedData;
import ru.necatalog.ozon.parser.parsing.dto.characteristics.Characteristic;
import ru.necatalog.ozon.parser.parsing.page.OzonAttributePage;
import ru.necatalog.ozon.parser.parsing.page.OzonCategoryPage;
import ru.necatalog.persistence.enumeration.Category;

public class OzonPageParser {

    public Stream<ParsedData> parseProductsFromCategoryPage(String pageSource,
                                                            Category category) {
        OzonCategoryPage parser = new OzonCategoryPage(pageSource);
        return parser.getProducts(category);
    }

    public List<Characteristic> parseAttributesPage(String attributeJson) throws JsonProcessingException {
        return new OzonAttributePage(attributeJson).getCharacteristics();
    }
}
