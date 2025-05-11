package ru.necatalog.ozon.parser.parsing.processor;

import java.util.List;

import ru.necatalog.ozon.parser.parsing.dto.characteristics.Characteristic;
import ru.necatalog.ozon.parser.parsing.enumeration.OzonCategory;
import ru.necatalog.persistence.entity.ProductAttributeEntity;

public interface AttributeProcessor {

    List<ProductAttributeEntity> process(List<Characteristic> attributes,
                                         String productUrl);

    OzonCategory getCategory();

}
