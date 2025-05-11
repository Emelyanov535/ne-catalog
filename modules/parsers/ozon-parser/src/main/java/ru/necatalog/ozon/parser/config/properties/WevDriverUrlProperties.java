package ru.necatalog.ozon.parser.config.properties;

import java.util.List;

import lombok.Data;

@Data
public class WevDriverUrlProperties {

    private List<String> catalog;

    private List<String> characteristics;

}
