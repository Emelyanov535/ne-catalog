package ru.necatalog.wildberriesparser.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductSpecification {
	private String name;
	private String value;
}
