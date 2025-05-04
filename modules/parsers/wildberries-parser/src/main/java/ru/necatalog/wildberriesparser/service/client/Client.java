package ru.necatalog.wildberriesparser.service.client;

import ru.necatalog.wildberriesparser.service.dto.ProductAttributesResponse;
import ru.necatalog.wildberriesparser.service.dto.ProductListDto;

public interface Client {
	ProductListDto scrapPage(int page);

	ProductAttributesResponse scrapAttributes(String url);
}
