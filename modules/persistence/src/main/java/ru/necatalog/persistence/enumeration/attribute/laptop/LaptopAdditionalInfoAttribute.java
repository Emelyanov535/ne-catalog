package ru.necatalog.persistence.enumeration.attribute.laptop;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.Category;

@Getter
@RequiredArgsConstructor
public enum LaptopAdditionalInfoAttribute implements AttributeGroup {

	VENDOR_CODE(null),
	PART_NUMBER(null);

	private final String groupName = Category.LAPTOP.name() + "_INFO";

	private final String unit;
}
