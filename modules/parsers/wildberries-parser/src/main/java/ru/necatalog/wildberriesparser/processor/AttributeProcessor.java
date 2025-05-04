package ru.necatalog.wildberriesparser.processor;

import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.wildberriesparser.service.dto.ProductAttributesResponse;

import java.util.List;

public interface AttributeProcessor {

    List<ProductAttributeEntity> process(ProductAttributesResponse attributes,
                                         String productUrl);
}
