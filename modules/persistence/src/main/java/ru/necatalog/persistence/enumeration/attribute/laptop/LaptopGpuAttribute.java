package ru.necatalog.persistence.enumeration.attribute.laptop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.Category;

@Getter
@RequiredArgsConstructor
public enum LaptopGpuAttribute implements AttributeGroup {

    GPU_NAME(null),
    GPU_TYPE(null),
    GPU_BRAND(null),
    VIDEO_MEMORY("ГБ"),;

    private final String groupName = Category.LAPTOP.name() + "_GPU";

    private final String unit;

}