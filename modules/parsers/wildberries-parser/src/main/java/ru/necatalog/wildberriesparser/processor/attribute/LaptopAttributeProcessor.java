package ru.necatalog.wildberriesparser.processor.attribute;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.persistence.entity.AttributeEntity;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.entity.id.ProductAttributeId;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.ValueType;
import ru.necatalog.persistence.repository.AttributeRepository;
import ru.necatalog.wildberriesparser.processor.AttributeProcessor;
import ru.necatalog.wildberriesparser.processor.definder.LaptopAttributeDefinder;
import ru.necatalog.wildberriesparser.service.dto.ProductAttributesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
				ProductAttributeId id = new ProductAttributeId(productUrl, getAttributeId(attributeGroup));
				ProductAttributeEntity attributeEntity = ProductAttributeEntity.builder()
						.id(id)
						.value(option.getValue())
						.valueType(getValueType(option.getValue()))
						.unit(null)
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
