package ru.necatalog.ozonparser.parser.service.processor;

import java.util.List;

import ru.necatalog.ozonparser.parser.enumeration.OzonCategory;
import ru.necatalog.ozonparser.parser.service.dto.Characteristic;
import ru.necatalog.persistence.entity.ProductAttributeEntity;

public interface AttributeProcessor {

    List<ProductAttributeEntity> process(List<Characteristic> attributes,
                                         String productUrl);

    OzonCategory getCategory();

}
