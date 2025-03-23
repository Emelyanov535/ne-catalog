package ru.necatalog.persistence.enumeration.attribute.laptop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.Category;

@Getter
@RequiredArgsConstructor
public enum LaptopDisplayAttribute implements AttributeGroup {

    DIAGONAL("дюймы"),
    TECHNOLOGY_MATRIX(null),
    SCREEN_COVER(null),
    RESOLUTION(null),
    MAX_FREQUENCY_SCREEN("ГЦ"),
    SENSOR_SCREEN(null);

    private final String groupName = Category.LAPTOP.name() + "_DISPLAY";

    private final String unit;

}