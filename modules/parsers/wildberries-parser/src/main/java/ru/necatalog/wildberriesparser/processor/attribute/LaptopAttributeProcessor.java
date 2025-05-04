package ru.necatalog.wildberriesparser.processor.attribute;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.persistence.entity.AttributeEntity;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.entity.id.ProductAttributeId;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.ValueType;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopProcessorAttribute;
import ru.necatalog.persistence.repository.AttributeRepository;
import ru.necatalog.wildberriesparser.processor.AttributeProcessor;
import ru.necatalog.wildberriesparser.processor.definder.LaptopAttributeDefinder;
import ru.necatalog.wildberriesparser.service.dto.ProductAttributesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("wildeberriesLaptopAttributeProcessor")
@RequiredArgsConstructor
public class LaptopAttributeProcessor implements AttributeProcessor {

	private final LaptopAttributeDefinder attributeDefinder;
	private final AttributeRepository attributeRepository;
	private List<AttributeEntity> savedAttributes;

	@PostConstruct
	@Transactional(readOnly = true)
	public void init() {
		this.savedAttributes = this.attributeRepository.findAllByGroupContains("LAPTOP");
	}

	@Override
	public List<ProductAttributeEntity> process(ProductAttributesResponse groupedAttributes, String productUrl) {
		List<ProductAttributeEntity> attributes = new ArrayList<>();
		List<ProductAttributesResponse.GroupedOption> groupedOptions = groupedAttributes.getGroupedOptions();
		groupedOptions.forEach(groupedOption -> groupedOption.getOptions().forEach(option -> {
			Optional<AttributeGroup> group = attributeDefinder.define(groupedOption.getGroupName(), option.getName());
			if (group.isPresent()) {
				AttributeGroup attributeGroup = group.get();
				String value = getValue(option, attributeGroup);
				ProductAttributeId id = new ProductAttributeId(productUrl, getAttributeId(attributeGroup));
				ProductAttributeEntity attributeEntity = ProductAttributeEntity.builder()
						.id(id)
						.value(value)
						.valueType(getValueType(value))
						.unit(getUnit(groupedOption.getGroupName(), option, attributeGroup))
						.build();
				attributes.add(attributeEntity);
			}
		}));
		return attributes;
	}

	private String getValueType(String value) {
		try {
			Double.parseDouble(value);
			return ValueType.NUMBER.name();
		} catch (NumberFormatException e) {
			return ValueType.STRING.name();
		}
	}

	private String getValue(ProductAttributesResponse.GroupedOption.Option value,
							AttributeGroup attributeGroup) {
		String attributeText = value.getValue();
		if (attributeGroup.getUnit() != null) {
			if (LaptopProcessorAttribute.FREQUENCY.equals(attributeGroup)) {
				String res = extractStorageValue(attributeText);
				if (Double.parseDouble(res) > 1000) {
					return String.valueOf(Integer.parseInt(res) / 1000.0);
				}
			}
			return extractNumberValue(attributeText);
		} else if (containsStorageUnit(attributeText)) {
			return extractStorageValue(attributeText);
		}
		return attributeText.trim();
	}

	private String getUnit(String attributeName,
						   ProductAttributesResponse.GroupedOption.Option value,
						   AttributeGroup attributeGroup) {
		if (StringUtils.isNotBlank(attributeGroup.getUnit())) {
			return attributeGroup.getUnit();
		}
		String storageUnit = extractStorageUnit(value.getValue());
		if (storageUnit != null) {
			return storageUnit.toUpperCase().trim();
		}
		String storageUnitFromName = extractStorageUnit(attributeName);
		if (storageUnitFromName != null) {
			return storageUnitFromName.toUpperCase().trim();
		}
		return null;
	}

	private boolean containsStorageUnit(String input) {
		String regex = "\\s(ГБ|GB|TB|ТБ|MB|МБ|ГЦ|МГЦ|ШТ)\\s*";
		Matcher matcher = Pattern.compile(regex).matcher(input.toUpperCase());
		return matcher.find();
	}

	private String extractStorageUnit(String input) {
		String regex = "\\s(ГБ|GB|TB|ТБ|MB|МБ|ГЦ|МГЦ)\\s*";
		Matcher matcher = Pattern.compile(regex).matcher(input.toUpperCase());
		return matcher.find() ? matcher.group(1).trim() : null;
	}

	private String extractStorageValue(String input) {
		String regex = "(\\d+(\\.\\d+)?)\\s*(ГБ|GB|TB|ТБ|MB|МБ|ГЦ|МГЦ|ШТ)";
		Matcher matcher = Pattern.compile(regex).matcher(input.toUpperCase());
		if (matcher.find()) {
			return matcher.group(1);
		}
		throw new IllegalStateException("Не удалось найти значение в строке: " + input);
	}

	private String extractNumberValue(String input) {
		Matcher matcher = Pattern.compile("(\\d+(\\.\\d+)?)").matcher(input.replace(",", "."));
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return input.trim();
	}

	private Long getAttributeId(AttributeGroup group) {
		return savedAttributes.stream()
				.filter(savedAttribute ->
						savedAttribute.getGroup().equals(group.getGroupName())
								&& savedAttribute.getName().equals(group.name()))
				.findFirst()
				.map(AttributeEntity::getId)
				.orElseThrow(() -> new RuntimeException("Не найдено соответствующего сохраненного атрибута"));
	}
}
