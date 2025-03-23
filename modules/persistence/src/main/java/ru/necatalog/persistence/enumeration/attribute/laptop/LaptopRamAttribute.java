package ru.necatalog.persistence.enumeration.attribute.laptop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.Category;

@Getter
@RequiredArgsConstructor
public enum LaptopRamAttribute implements AttributeGroup {

    RAM("ГБ"),
    RAM_TYPE(null),
    UPGRADE_CAPABILITY(null);

    private final String groupName = Category.LAPTOP.name() + "_RAM";

    private final String unit;

}