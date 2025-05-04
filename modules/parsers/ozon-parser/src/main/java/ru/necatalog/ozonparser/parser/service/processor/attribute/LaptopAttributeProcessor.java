package ru.necatalog.ozonparser.parser.service.processor.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.ozonparser.parser.enumeration.OzonCategory;
import ru.necatalog.ozonparser.parser.service.dto.Characteristic;
import ru.necatalog.ozonparser.parser.service.processor.AttributeProcessor;
import ru.necatalog.ozonparser.parser.service.processor.definder.LaptopAttributeDefinder;
import ru.necatalog.persistence.entity.AttributeEntity;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.entity.id.ProductAttributeId;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.ValueType;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopGpuAttribute;
import ru.necatalog.persistence.repository.AttributeRepository;

@Service
@RequiredArgsConstructor
public class LaptopAttributeProcessor implements AttributeProcessor {

    private final LaptopAttributeDefinder definder;

    private final AttributeRepository attributeRepository;

    private List<AttributeEntity> savedAttributes;

    private String videoCardMemoryValue = null; // Необходимо для извлечения количества видеопамяти из названия видеокарты,

    private String videoCardMemoryValueUnit = null; // Озон не всегда выносит это в отдельный атрибут

    @PostConstruct
    @Transactional(readOnly = true)
    public void init() {
        this.savedAttributes = this.attributeRepository.findAllByGroupContains(getCategory().getMappedCategory().name());
    }

    @Override
    public List<ProductAttributeEntity> process(List<Characteristic> characteristics,
                                                String productUrl) {
        List<ProductAttributeEntity> attributes = new ArrayList<>();
        characteristics.stream().filter(characteristic -> characteristic.getAttributes() != null)
            .forEach(characteristic -> characteristic.getAttributes().forEach(attribute -> {
            Optional<AttributeGroup> group = definder.define(characteristic.getTitle(), attribute.getKey());
            if (group.isPresent()) {
                AttributeGroup attributeGroup = group.get();
                attribute.getValues().forEach(attributeValue -> {
                    String value = getValue(characteristic, attributeValue, attributeGroup);
                    if (StringUtils.isNotBlank(value)) {
                        ProductAttributeId id = new ProductAttributeId(productUrl, getAttributeId(attributeGroup));
                        ProductAttributeEntity attributeEntity = ProductAttributeEntity.builder()
                            .id(id)
                            .valueType(getValueType(value))
                            .value(value)
                            .unit(getUnit(attribute.getName(), attributeValue, attributeGroup))
                            .build();
                        attributes.add(attributeEntity);
                    }
                });
            }
        }));
        if (StringUtils.isNotBlank(videoCardMemoryValue)
            && StringUtils.isNotBlank(videoCardMemoryValueUnit)) {
            Long attributeId = getAttributeId(LaptopGpuAttribute.VIDEO_MEMORY);
            Optional<ProductAttributeEntity> optVideoMemory = attributes.stream()
                .filter(attr ->
                    attributeId.equals(attr.getId().getAttributeId()))
                .findFirst();
            if (optVideoMemory.isEmpty()) {
                attributes.add(new ProductAttributeEntity(
                    new ProductAttributeId(productUrl, attributeId),
                    ValueType.STRING.name(),
                    videoCardMemoryValue,
                    videoCardMemoryValueUnit));
            }
        }
        return attributes;
    }

    private String getUnit(String attributeName,
                           Characteristic.Short.Values value,
                           AttributeGroup attributeGroup) {
        if (StringUtils.isNotBlank(attributeGroup.getUnit())) {
            return attributeGroup.getUnit();
        }
        if (LaptopGpuAttribute.GPU_NAME.equals(attributeGroup)) {
            try {
                videoCardMemoryValue = extractStorageValue(value.getText()).toString().trim();
                videoCardMemoryValueUnit = extractStorageUnit(attributeName);
                return null;
            } catch (Exception ignored) {
                //
            }
        }
        String storageUnit = extractStorageUnit(value.getText());
        if (storageUnit != null) {
            return storageUnit.toUpperCase().trim();
        }
        String storageUnitFromName = extractStorageUnit(attributeName);
        if (storageUnitFromName != null) {
            return storageUnitFromName.toUpperCase().trim();
        }
        return null;
    }

    private String extractStorageUnit(String input) {
        String regex = "\\s(ГБ|GB|TB|ТБ|MB|МБ|ГЦ)\\s*";
        Matcher matcher = Pattern.compile(regex).matcher(input.toUpperCase());
        return matcher.find() ? matcher.group().trim() : null;
    }

    private Integer extractStorageValue(String input) {
        String regex = "(\\d+)\\s*(ГБ|GB|TB|ТБ|MB|МБ|ГЦ)";
        Matcher matcher = Pattern.compile(regex).matcher(input.toUpperCase());
        return Integer.parseInt(matcher.group(1));
    }

    private String getValue(Characteristic characteristic,
                            Characteristic.Short.Values value,
                            AttributeGroup attributeGroup) {
        String attributeText = value.getText();
        /*if ("Видеокарта".equals(characteristic.getTitle())
            && LaptopGpuAttribute.GPU_NAME.equals(attributeGroup)) {
            Integer value = extractGB(attributeText);
            if (value != null) {
                return value.toString();
            }
        }*/
        if (attributeGroup.getUnit() != null) {
            String result = attributeText.replaceAll("\\D+", ".").trim();
            if (result.endsWith(".")) {
                result = result.substring(0, result.length() - 1);
            }
            return result.trim();
        }
        return attributeText.trim();
    }

    private Long getAttributeId(AttributeGroup group) {
        return savedAttributes.stream()
            .filter(savedAttribute ->
                savedAttribute.getGroup().equals(group.getGroupName())
                && savedAttribute.getName().equals(group.name()))
            .findFirst()
            .map(AttributeEntity::getId)
            .orElseThrow(() -> new NotFoundException("Не найдено соответствующего сохраненного атрибута"));
    }

    private String getValueType(String value) {
        try {
            Double.parseDouble(value);
            return ValueType.NUMBER.name();
        } catch (NumberFormatException e) {
            return ValueType.STRING.name();
        }
    }

    @Override
    public OzonCategory getCategory() {
        return OzonCategory.LAPTOP;
    }

}
