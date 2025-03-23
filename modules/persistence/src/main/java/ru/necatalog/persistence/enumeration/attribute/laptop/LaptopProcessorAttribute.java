package ru.necatalog.persistence.enumeration.attribute.laptop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.Category;

@Getter
@RequiredArgsConstructor
public enum LaptopProcessorAttribute implements AttributeGroup {

    CPU_NAME(null),
    BRAND_NAME(null),
    MODEL_NAME(null),
    FREQUENCY("ГГц"),
    NUM_OF_CORES(null),
    NUM_OF_THREADS(null);

    private final String groupName = Category.LAPTOP.name() + "_PROCESSOR";

    private final String unit;

}