package ru.necatalog.search.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.dto.PriceFilterDto;
import ru.necatalog.persistence.dto.ProductAttributeFilter;
import ru.necatalog.persistence.entity.AttributeEntity;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;
import ru.necatalog.persistence.repository.AttributeRepository;
import ru.necatalog.persistence.repository.ProductAttributeRepository;
import ru.necatalog.persistence.repository.ProductRepository;
import ru.necatalog.search.dto.FilterData;
import ru.necatalog.search.dto.SearchResult;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final AttributeRepository attributeRepository;

    private final ProductAttributeRepository productAttributeRepository;

    private final ProductRepository productRepository;

    private Map<Long, AttributeEntity> attributes;

    private final EntityManager entityManager;

    @PostConstruct
    public void init() {
        attributes = attributeRepository.findAll().stream()
            .collect(Collectors.toMap(AttributeEntity::getId, Function.identity()));
    }

    public FilterData getAvailableFilters(Category category) {
        PriceFilterDto price = productRepository.findPriceFilterData(category.name());
        List<Long> categoryAttributes = attributes.values().stream()
            .filter(attr -> attr.getGroup().contains(category.name()))
            .map(AttributeEntity::getId)
            .toList();
        List<ProductAttributeFilter> attributeValuePairs =
            productAttributeRepository.findDistinctById_attributeIdIn(categoryAttributes);
        Map<String, List<String>> filters = attributeValuePairs.stream()
            .collect(Collectors.groupingBy(
                pair -> attributes.get(pair.getAttributeId()).getName(),
                Collectors.mapping(
                    pair -> pair.getValue() + (pair.getUnit() == null ? "" : " " + pair.getUnit()),
                    Collectors.toList())));
        for (var value : filters.values()) {
            Collections.sort(value);
        }
        FilterData filterData = new FilterData();
        filterData.setFilters(filters);
        filterData.setPriceStart(price.getPriceStart());
        filterData.setPriceEnd(price.getPriceEnd());
        return filterData;
    }

    public List<SearchResult> search(Category category,
                                     String searchQuery,
                                     String sortAttribute,
                                     String sortDir,
                                     Integer page,
                                     Integer size,
                                     Map<String, String> attributeValues) {
        List<AttributeEntity> categoryAttributes = attributes.values().stream()
            .filter(attr -> attr.getGroup().contains(category.name()))
            .toList();
        Map<String, AttributeEntity> attributes = categoryAttributes.stream()
            .collect(Collectors.toMap(AttributeEntity::getName, Function.identity()));

        StringBuilder query = new StringBuilder("""
        select p.product_name,
               p.url,
               p.brand,
               p.marketplace,
               p.image_url,
               ph.price,
               p.percent_change
        from product p
        join price_history ph on ph.product_url = p.url
        where ph.date = (select max(ph2.date) from price_history ph2 where ph2.product_url = p.url)
        """);

        if (searchQuery != null && !searchQuery.isBlank()) {
            query.append(" and (to_tsvector(p.product_name) @@ plainto_tsquery(:searchQuery)) ");
        }

        boolean hasFilters = attributeValues != null && !attributeValues.isEmpty();
        if (hasFilters && !attributeValues.containsKey("page")) {
            query.append("""
            and p.url in (
                select pa.product_url
                from product_attribute pa
                where
            """);

            List<String> conditions = new ArrayList<>();
            for (Map.Entry<String, String> entry : attributeValues.entrySet()) {
                String attrName = entry.getKey();
                String value = entry.getValue();
                AttributeEntity attr = attributes.get(attrName);
                if (attr != null && value != null) {
                    conditions.add("(pa.attribute_id = " + attr.getId() +
                        " and trim(concat(pa.value, ' ', pa.unit)) = '" + value + "')");
                }
            }

            if (!conditions.isEmpty()) {
                query.append(conditions.stream().collect(Collectors.joining(" or ")));
                query.append(" group by pa.product_url having count(distinct pa.attribute_id) = ").append(conditions.size());
                query.append(")");
            } else {
                query.append(" and false");
            }
        }

        if ("price".equalsIgnoreCase(sortAttribute) &&
            ("asc".equalsIgnoreCase(sortDir) || "desc".equalsIgnoreCase(sortDir))) {
            query.append(" order by ph.price ").append(sortDir);
        }

        int safePage = page != null ? page : 0;
        int safeSize = size != null ? size : 10;
        query.append(" offset ").append(safePage * safeSize);
        query.append(" limit ").append(safeSize);

        Query nativeQuery = entityManager.createNativeQuery(query.toString());
        if (searchQuery != null && !searchQuery.isBlank()) {
            nativeQuery.setParameter("searchQuery", searchQuery);
        }

        return ((List<Object[]>) nativeQuery.getResultList()).stream()
            .map(row -> new SearchResult(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                Marketplace.valueOf((String) row[3]),
                (String) row[4],
                (BigDecimal) row[5],
                (Double) row[6]
            ))
            .toList();
    }

}
