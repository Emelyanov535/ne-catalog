package ru.necatalog.wildberriesparser.converter;

import java.time.LocalDateTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;
import ru.necatalog.wildberriesparser.service.dto.ProductListDto;

@Component
public class ProductInfoDto2ProductEntity implements Converter<ProductListDto.Data.ProductInfoDto, ProductEntity> {

    private static final String URL_FORMAT = "https://www.wildberries.ru/catalog/%d/detail.aspx?targetUrl=BP";
    private static final String IMAGE_URL_FORMAT = "https://basket-%s.wbbasket.ru/vol%d/part%d/%d/images/big/1.webp";

    @Override
    public ProductEntity convert(ProductListDto.Data.ProductInfoDto source) {
        return ProductEntity.builder()
                .marketplace(Marketplace.WILDBERRIES)
                .category(Category.LAPTOP)
                .brand(source.getBrand())
                .productName(source.getName())
                .createdAt(LocalDateTime.now())
                .imageUrl(getImageUrl(source.getId()))
                .url(String.format(URL_FORMAT, source.getId()))
                .percentChange(0.0)
                .build();
    }

    private String getImageUrl(Long productId) {
        long vol = productId / 100000;
        Long part = productId / 1000;
        String basket = getBasket(vol);

        return String.format(IMAGE_URL_FORMAT, basket, vol, part, productId);
    }

    private String getBasket(long vol) {
        if (vol <= 143) return "01";
        if (vol <= 287) return "02";
        if (vol <= 431) return "03";
        if (vol <= 719) return "04";
        if (vol <= 1007) return "05";
        if (vol <= 1061) return "06";
        if (vol <= 1115) return "07";
        if (vol <= 1169) return "08";
        if (vol <= 1313) return "09";
        if (vol <= 1601) return "10";
        if (vol <= 1655) return "11";
        if (vol <= 1919) return "12";
        if (vol <= 2045) return "13";
        if (vol <= 2191) return "14";
        if (vol <= 2403) return "15";
        if (vol <= 2620) return "16";
        if (vol <= 2837) return "17";
        if (vol <= 3051) return "18";
        if (vol <= 3267) return "19";
        if (vol <= 3485) return "20";

        return "21";
    }
}
