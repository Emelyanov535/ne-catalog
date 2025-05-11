package ru.necatalog.ozon.parser.parsing.processor;

import java.util.Optional;

import ru.necatalog.persistence.enumeration.AttributeGroup;

public interface AttributeDefinder {

    Optional<AttributeGroup> define(String title, String key);

}
