package ru.necatalog.persistence.enumeration.attribute.laptop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.Category;

@Getter
@RequiredArgsConstructor
public enum LaptopStorageAttribute implements AttributeGroup {

    VOLUME_SSD(null),
    VOLUME_HDD(null),
    TYPE(null),
    NUM_SSD(null),
    NUM_HDD(null),
    SSD_FORM_FACTOR(null),
    HDD_FORM_FACTOR(null);

    private final String groupName = Category.LAPTOP.name() + "_STORAGE";

    private final String unit;

}