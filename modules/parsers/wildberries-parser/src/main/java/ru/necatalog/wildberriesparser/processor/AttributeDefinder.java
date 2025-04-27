package ru.necatalog.wildberriesparser.processor;

import ru.necatalog.persistence.enumeration.AttributeGroup;

import java.util.Optional;

public interface AttributeDefinder {

    Optional<AttributeGroup> define(String title, String key);

}
