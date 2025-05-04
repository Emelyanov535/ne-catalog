package ru.necatalog.persistence.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.persistence.entity.AttributeEntity;
import ru.necatalog.persistence.enumeration.AttributeGroup;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopAdditionalInfoAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopDisplayAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopGpuAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopProcessorAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopRamAttribute;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopStorageAttribute;
import ru.necatalog.persistence.repository.AttributeRepository;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttributeSaver {

    private final AttributeRepository attributeRepository;

    private final List<AttributeGroup[]> attributeGroups = List.of(
        LaptopDisplayAttribute.values(),
        LaptopGpuAttribute.values(),
        LaptopProcessorAttribute.values(),
        LaptopRamAttribute.values(),
        LaptopStorageAttribute.values(),
		LaptopAdditionalInfoAttribute.values()
    );

    @PostConstruct
    @Transactional
    public void init() {
        attributeGroups.stream()
                .flatMap(Arrays::stream)
                .map(AttributeEntity::new)
                .forEach(attribute -> {
                    boolean exists = attributeRepository.existsByNameAndGroup(attribute.getName(), attribute.getGroup());
                    if (!exists) {
                        attributeRepository.save(attribute);
                    }
                });
    }
}
