package ru.necatalog.analogfinder.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import ru.necatalog.analogfinder.dto.AnalogResult;
import ru.necatalog.persistence.entity.AttributeEntity;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.enumeration.ValueType;
import ru.necatalog.persistence.repository.AttributeRepository;
import ru.necatalog.persistence.repository.ProductAttributeRepository;
import ru.necatalog.persistence.repository.ProductPriceRepository;
import ru.necatalog.persistence.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class AnalogFinderService {

    private final AttributeRepository attributeRepository;

    private final EntityManager em;

    private final ProductRepository productRepository;

    private final ProductPriceRepository productPriceRepository;

    private final ProductAttributeRepository productAttributeRepository;

    public List<AnalogResult> findAnalogs(String productUrl,
                                          List<String> attributeGroups,
                                          boolean reverse) {
        if (reverse) {
            return findDifferentAnalogs(productUrl, attributeGroups, reverse);
        }
        List<AttributeEntity> attributes = attributeRepository.findAllByNameIn(attributeGroups);
        ProductEntity product = productRepository.findByUrl(productUrl)
            .orElseThrow(() -> new EntityNotFoundException(productUrl));
        Map<String, List<ProductAttributeEntity>> allProductsAttributes =
            findProductsWithAttributes(attributes, product.getLastPrice(), product.getProductName(), product.getUrl());
        List<ProductAttributeEntity> baseProduct = allProductsAttributes.get(productUrl).stream()
            .filter(pa ->
                StringUtils.isNotBlank(pa.getValue()) )
            .toList();
        if (baseProduct == null) {
            throw new EntityNotFoundException("Характеристики товара еще не были собраны");
        }
        Map<String, List<Double>> distances = new HashMap<>();
        for (List<ProductAttributeEntity> productAttributes : allProductsAttributes.values()) {
            distances.put(productAttributes.getFirst().getId().getProductUrl(),
                findDistances(baseProduct, productAttributes, null));
        }
        Map<String, List<Double>> vectors = getVectors(distances, null);
        Map<String, List<Double>> productUrlsVectors = findAnalogues(productUrl, vectors, 5).stream()
            .collect(Collectors.toMap(Function.identity(), vectors::get));
        return productRepository.findAllByUrlIn(productUrlsVectors.keySet().stream().toList()).stream()
            .map(prod -> new AnalogResult(prod.getProductName(), prod.getUrl(), prod.getBrand(),
                prod.getMarketplace(), prod.getImageUrl()))
            .toList();
    }

    private List<AnalogResult> findDifferentAnalogs(String productUrl, List<String> attributeGroups, boolean reverse) {
        List<AttributeEntity> attributes = attributeRepository.findAll();
        List<AttributeEntity> reverseAttributes = attributeRepository.findAllByNameIn(attributeGroups);
        ProductEntity product = productRepository.findByUrl(productUrl)
            .orElseThrow(() -> new EntityNotFoundException(productUrl));
        Map<String, List<ProductAttributeEntity>> allProductsAttributes =
            findProductsWithAttributes(attributes, product.getLastPrice(), product.getProductName(), product.getUrl());
        List<ProductAttributeEntity> baseProduct = allProductsAttributes.get(productUrl).stream()
            .filter(pa ->
                StringUtils.isNotBlank(pa.getValue()) )
            .toList();
        List<Integer> reversedIndexes = new ArrayList<>();
        for (int i = 0; i < baseProduct.size(); i++) {
            int finalI = i;
            if (reverseAttributes.stream()
                .anyMatch(attr -> Objects.equals(attr.getId(), baseProduct.get(finalI).getId().getAttributeId()))) {
                reversedIndexes.add(i);
            }
        }
        if (reversedIndexes.isEmpty()) {
            for (int i = 0; i < attributeGroups.size(); ++i) {
                reversedIndexes.add(baseProduct.size() + i);
            }
        }
        if (baseProduct == null) {
            throw new EntityNotFoundException("Характеристики товара еще не были собраны");
        }
        Map<String, List<Double>> distances = new HashMap<>();
        for (List<ProductAttributeEntity> productAttributes : allProductsAttributes.values()) {
            if (!productAttributes.getFirst().getId().getProductUrl().equals(productUrl)) {
                distances.put(productAttributes.getFirst().getId().getProductUrl(),
                    findDistances(baseProduct, productAttributes, reverseAttributes));
            }
        }
        Map<String, List<Double>> vectors = getVectors(distances, reversedIndexes);
        Map<String, List<Double>> productUrlsVectors = findDifferentAnalogues(productUrl, vectors, 5, reversedIndexes).stream()
            .collect(Collectors.toMap(Function.identity(), vectors::get));
        return productRepository.findAllByUrlIn(productUrlsVectors.keySet().stream().toList()).stream()
            .map(prod -> new AnalogResult(prod.getProductName(), prod.getUrl(), prod.getBrand(),
                prod.getMarketplace(), prod.getImageUrl()))
            .toList();
    }

    private Map<String, List<ProductAttributeEntity>> findProductsWithAttributes(List<AttributeEntity> attributes,
                                                                                 BigDecimal productPrice,
                                                                                 String productName,
                                                                                 String productUrl) {
        BigDecimal minPrice = productPrice.multiply(BigDecimal.valueOf(0.8));
        BigDecimal maxPrice = productPrice.multiply(BigDecimal.valueOf(1.2));
        double rankThreshold = 1;

        String sql = """
                select distinct pa.product_url, pa.attribute_id, pa.value_type, pa.value, pa.unit
                from product_attribute pa
                join product p on p.url = pa.product_url
                join product_ts_vector ptv on ptv.url = p.url
                where pa.attribute_id in (:attributeIds)
                  and p.last_price between :minPrice and :maxPrice
                  and (p.url = :url or ts_rank(ptv.product_name, plainto_tsquery(:searchQuery)) < :rankThreshold)
            """;

        List<ProductAttributeEntity> productAttributeEntities = em.createNativeQuery(sql, ProductAttributeEntity.class)
            .setParameter("attributeIds", attributes.stream().map(AttributeEntity::getId).toList())
            .setParameter("minPrice", minPrice)
            .setParameter("maxPrice", maxPrice)
            .setParameter("rankThreshold", rankThreshold)
            .setParameter("searchQuery", productName)
            .setParameter("url", productUrl)
            .getResultList();

        return productAttributeEntities.stream()
            .collect(Collectors.groupingBy(
                pa -> pa.getId().getProductUrl(),
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> list.stream()
                        .sorted(Comparator.comparing(pa -> pa.getId().getAttributeId()))
                        .toList()
                )));
    }

    private List<Double> findDistances(List<ProductAttributeEntity> baseProduct,
                                       List<ProductAttributeEntity> referenceProduct,
                                       List<AttributeEntity> reverseAttributes) {
        List<Double> vector = new ArrayList<>();
        if (referenceProduct.get(0).getId().getProductUrl().equals("https://ozon.ru/product/apple-macbook-air-2022-noutbuk-13-6-apple-m2-ram-8-gb-ssd-256-gb-apple-m2-macos-mlxx3ll-seryy-994406522/")) {
            System.out.println("asd");
        }
        for (ProductAttributeEntity baseAttribute : baseProduct) {
            ProductAttributeEntity referenceAttribute = null;
            for (ProductAttributeEntity productAttributeEntity : referenceProduct) {
                if (baseAttribute.getId().getAttributeId().equals(productAttributeEntity.getId().getAttributeId())) {
                    referenceAttribute = productAttributeEntity;
                    break;
                }
            }

            if (referenceAttribute == null) {
                if (reverseAttributes != null && reverseAttributes
                    .stream().anyMatch(attr -> attr.getId().equals(baseAttribute.getId().getAttributeId()))) {
                    vector.add(-2d);
                } else{
                    vector.add(-1d);
                }
                continue;
            }
            Long attributeId = referenceAttribute.getId().getAttributeId();
            if (StringUtils.isNotBlank(baseAttribute.getValue())
                && StringUtils.isNotBlank(referenceAttribute.getValue())) {
                if (ValueType.NUMBER.name().equals(baseAttribute.getValueType()) || StringUtils.isNotBlank(baseAttribute.getUnit())) {
                    try {
                        double baseValue = Double.parseDouble(baseAttribute.getValue()
                            .replace(baseAttribute.getUnit() == null ? "" : baseAttribute.getUnit(), "")
                            .replace(" ", ""));
                        double referenceValue = Double.parseDouble(referenceAttribute.getValue()
                            .replace(baseAttribute.getUnit() == null ? "" : baseAttribute.getUnit(), "")
                            .replace(" ", ""));
                        double difference = Math.abs(baseValue - referenceValue);
                        vector.add(difference);
                    } catch (Exception e) {
                        if (reverseAttributes != null && reverseAttributes
                            .stream().anyMatch(attr -> attr.getId().equals(baseAttribute.getId().getAttributeId()))) {
                            vector.add(-2d);
                        } else{
                            vector.add(-1d);
                        }
                    }
                    continue;
                }
                if (ValueType.STRING.name().equals(baseAttribute.getValueType())) {
                    String baseValue = baseAttribute.getValue();
                    String referenceValue = referenceAttribute.getValue();
                    int dist = LevenshteinDistance.getDefaultInstance().apply(baseValue, referenceValue);
                    vector.add((double) dist);
                }
            } else {
                if (reverseAttributes != null && reverseAttributes
                    .stream().anyMatch(attr -> attr.getId().equals(baseAttribute.getId().getAttributeId()))) {
                    vector.add(-2d);
                } else{
                    vector.add(-1d);
                }
            }
        }

        return vector;
    }

    private Map<String, List<Double>> getVectors(Map<String, List<Double>> distances,
                                                 List<Integer> reversedIndexes) {
        if (distances == null) {
            return Map.of();
        }
        Map<String, List<Double>> vectors = new HashMap<>();
        int attributesCount = distances.values().stream()
            .findFirst()
            .map(List::size)
            .orElse(0);
        List<Double> mins = new ArrayList<>();
        List<Double> maxs = new ArrayList<>();
        for (int i = 0; i < attributesCount; i++) {
            mins.add(1000000d);
            maxs.add(0.0000000001d);
        }
        for (Map.Entry<String, List<Double>> singleDistances : distances.entrySet()) {
            if (singleDistances.getValue().stream().anyMatch(dist -> dist == -1)) {
                continue;
            }
            for (int i = 0; i < singleDistances.getValue().size(); i++) {
                if (singleDistances.getValue().get(i) > maxs.get(i)) {
                    maxs.add(i, singleDistances.getValue().get(i));
                    maxs.remove(i+1);
                }
                if (singleDistances.getValue().get(i) == -1) {
                    mins.add(i, maxs.get(i));
                    mins.remove(i+1);
                } else if (singleDistances.getValue().get(i) < mins.get(i)) {
                    mins.add(i, singleDistances.getValue().get(i));
                    mins.remove(i+1);
                }
            }
        }
        for (Map.Entry<String, List<Double>> singleDistances : distances.entrySet()) {
            List<Double> vector = new ArrayList<>();
            for(int i = 0; i < singleDistances.getValue().size(); i++) {
                /*if (reversedIndexes != null && reversedIndexes.contains(i)) {
                    vector.add(1 - (singleDistances.getValue().get(i) == -2d ? 0 : singleDistances.getValue().get(i)) / maxs.get(i));
                } else {*/
                    vector.add((singleDistances.getValue().get(i) == -1d ? maxs.get(i) : singleDistances.getValue().get(i)) / maxs.get(i));
                /*}*/
            }
            if (reversedIndexes != null && singleDistances.getValue().size() - 1 < reversedIndexes.getFirst()) {
                vector.add(1d);
            }
            /*if (singleDistances.getValue().stream().noneMatch(dist -> dist == -1)) {
                vectors.put(singleDistances.getKey(), vector);
            }*/
            vectors.put(singleDistances.getKey(), vector);
        }
        return vectors;
    }

    private static List<String> findAnalogues(
        String productUrl,
        Map<String, List<Double>> productVectors,
        int numAnalogues
    ) {
        List<String> urls = new ArrayList<>(productVectors.keySet());
        double[][] vectors = new double[urls.size()][];

        for (int i = 0; i < urls.size(); i++) {
            List<Double> vec = productVectors.get(urls.get(i));
            if (vec.stream().anyMatch(v -> v > 0.3)) {
                urls.remove(i);
                i--;
                continue;
            }
            vectors[i] = vec.stream().mapToDouble(Double::doubleValue).toArray();
        }

        /*int targetIndex = urls.indexOf(productUrl);
        double[] targetVector = vectors[targetIndex];
*/
        List<String> candidateUrls = new ArrayList<>();
        List<Double> distances = new ArrayList<>();

        for (int i = 0; i < urls.size(); i++) {
            double distance = euclideanDistance(vectors[i]);
            if (distance == 0) {
                System.out.println("asd");
            }
            candidateUrls.add(urls.get(i));
            distances.add(distance);
        }

        return getTopNClosest(candidateUrls, distances, numAnalogues);
    }

    private static List<String> findDifferentAnalogues(
        String productUrl,
        Map<String, List<Double>> productVectors,
        int numAnalogues,
        List<Integer> reversedIndexes) {
        List<String> urls = new ArrayList<>(productVectors.keySet());
        double[][] vectors = new double[urls.size()][];

        for (int i = 0; i < urls.size(); i++) {
            List<Double> vec = productVectors.get(urls.get(i));
            if (/*vec.stream().filter(v -> v > 0.3).toList().size() > 3
                ||*/
                reversedIndexes != null && reversedIndexes.stream().anyMatch(rev -> vec.get(rev) == 0d)) {
                    urls.remove(i);
                    i--;
                    continue;
            }
            vectors[i] = vec.stream().mapToDouble(Double::doubleValue).toArray();
        }

        /*int targetIndex = urls.indexOf(productUrl);
        double[] targetVector = vectors[targetIndex];
*/
        List<String> candidateUrls = new ArrayList<>();
        List<Double> distances = new ArrayList<>();

        for (int i = 0; i < urls.size(); i++) {
            double distance = euclideanDistance(vectors[i]);
            if (distance == 0) {
                System.out.println("asd");
            }
            candidateUrls.add(urls.get(i));
            distances.add(distance);
        }

        return getTopNClosest(candidateUrls, distances, numAnalogues);
    }

    private static double euclideanDistance(double[] v2) {
        double sum = 0.0;
        for (double v : v2) {
            sum += Math.pow(v, 2);
        }
        return Math.sqrt(sum);
    }

    private static List<String> getTopNClosest(
        List<String> urls,
        List<Double> distances,
        int n
    ) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) indices.add(i);

        indices.sort(Comparator.comparingDouble(distances::get));

        return indices.stream()
            .limit(n)
            .map(urls::get)
            .collect(Collectors.toList());
    }

    private static List<String> getTopNDifferents(
        List<String> urls,
        List<Double> distances,
        int n
    ) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) indices.add(i);

        indices.sort(Comparator.comparingDouble(ind -> -distances.get(ind)));

        return indices.stream()
            .limit(n)
            .map(urls::get)
            .collect(Collectors.toList());
    }

    public Map<String, List<String>> getAttributeGroups(String productUrl) {
        List<ProductAttributeEntity> allProductsAttributes =
            productAttributeRepository.findById_ProductUrl(productUrl);
        List<Long> attributeIds = allProductsAttributes.stream()
            .map(pa -> pa.getId().getAttributeId())
            .toList();
        List<AttributeEntity> attributes = attributeRepository.findAllByIdIn(attributeIds);
        return attributes.stream()
            .collect(Collectors.groupingBy(
                AttributeEntity::getGroup,
                Collectors.mapping(AttributeEntity::getName, Collectors.toList())
            ));
    }

}
