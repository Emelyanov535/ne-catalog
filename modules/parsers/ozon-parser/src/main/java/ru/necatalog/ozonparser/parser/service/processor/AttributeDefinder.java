package ru.necatalog.ozonparser.parser.service.processor;

import java.util.Optional;

import ru.necatalog.persistence.enumeration.AttributeGroup;

public interface AttributeDefinder {

    Optional<AttributeGroup> define(String title, String key);

}
