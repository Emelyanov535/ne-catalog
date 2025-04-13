package ru.necatalog.analogfinder.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.necatalog.persistence.entity.AttributeEntity;
import ru.necatalog.persistence.entity.ProductAttributeEntity;
import ru.necatalog.persistence.enumeration.ValueType;
import ru.necatalog.persistence.enumeration.attribute.laptop.LaptopProcessorAttribute;
import ru.necatalog.persistence.repository.AttributeRepository;
import ru.necatalog.persistence.repository.ProductAttributeRepository;
import smile.clustering.CentroidClustering;
import smile.clustering.KMeans;

@Service
@RequiredArgsConstructor
public class AnalogFinderService {

    private final ProductAttributeRepository productAttributeRepository;

    private final AttributeRepository attributeRepository;

    private final EntityManager em;

    @Transactional(readOnly = true)
    public Map<String, List<Double>> findAnalogs(String productUrl,
                                                 List<String> attributeGroups) {
        List<AttributeEntity> attributes = attributeRepository.findAllByGroupIn(attributeGroups);
        Map<String, List<ProductAttributeEntity>> allProductsAttributes = findProductsWithAttributes(attributes);
        List<ProductAttributeEntity> baseProduct = allProductsAttributes.get(productUrl).stream()
            .filter(pa ->
                StringUtils.isNotBlank(pa.getValue()) )
            .toList();
        if (baseProduct == null) {
            throw new EntityNotFoundException("Характеристики товара еще не были собраны");
        }
        Map<String, List<Double>> vectors = new HashMap<>();
        for (List<ProductAttributeEntity> productAttributes : allProductsAttributes.values()) {
            vectors.put(productAttributes.getFirst().getId().getProductUrl(),
                vectorize(baseProduct, productAttributes));
        }
        return findAnalogues(productUrl, vectors, 70, 5).stream()
            .collect(Collectors.toMap(Function.identity(), vectors::get));
    }

    private Map<String, List<ProductAttributeEntity>> findProductsWithAttributes(List<AttributeEntity> attributes) {
        String sb = """
                select new ru.necatalog.persistence.entity.ProductAttributeEntity(
                    pa.id,
                    pa.valueType,
                    pa.value,
                    pa.unit)
                from ProductAttributeEntity pa
                where pa.id.attributeId in :attributeIds
            """;

        List<ProductAttributeEntity> productAttributeEntities = em.createQuery(sb, ProductAttributeEntity.class)
            .setParameter("attributeIds", attributes.stream().map(AttributeEntity::getId).toList())
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

    private List<Double> vectorize(List<ProductAttributeEntity> baseProduct,
                                   List<ProductAttributeEntity> referenceProduct) {
        List<Double> vector = new ArrayList<>();
        for (ProductAttributeEntity baseAttribute : baseProduct) {
            ProductAttributeEntity referenceAttribute = null;
            for (ProductAttributeEntity productAttributeEntity : referenceProduct) {
                if (baseAttribute.getId().getAttributeId().equals(productAttributeEntity.getId().getAttributeId())) {
                    referenceAttribute = productAttributeEntity;
                    break;
                }
            }

            if (referenceAttribute == null) {
                vector.add(1.0);
                continue;
            }

            if (StringUtils.isNotBlank(baseAttribute.getValue())
                && StringUtils.isNotBlank(referenceAttribute.getValue())) {
                if (ValueType.NUMBER.name().equals(baseAttribute.getValueType()) || StringUtils.isNotBlank(baseAttribute.getUnit())) {
                    try {
                        double baseValue = Double.parseDouble(baseAttribute.getValue());
                        double referenceValue = Double.parseDouble(referenceAttribute.getValue());
                        double difference = Math.abs(baseValue - referenceValue);
                        vector.add(baseValue == 0 ? 1.0 : difference / Math.abs(baseValue));
                    } catch (Exception e) {
                        //
                    }
                    continue;
                }
                if (ValueType.STRING.name().equals(baseAttribute.getValueType())) {
                    String baseValue = baseAttribute.getValue();
                    String referenceValue = referenceAttribute.getValue();
                    int dist = LevenshteinDistance.getDefaultInstance().apply(baseValue, referenceValue);
                    vector.add((double) dist / Math.max(baseValue.length(), referenceValue.length()));
                }
            } else {
                vector.add(1.0);
            }
        }
        return vector;
    }

    public static List<String> findAnalogues(
        String productUrl,
        Map<String, List<Double>> productVectors,
        int numClusters,
        int numAnalogues
    ) {
        List<String> urls = new ArrayList<>(productVectors.keySet());
        double[][] vectors = new double[productVectors.size()][];

        for (int i = 0; i < urls.size(); i++) {
            List<Double> vec = productVectors.get(urls.get(i));
            vectors[i] = vec.stream().mapToDouble(Double::doubleValue).toArray();
        }

        CentroidClustering<double[], double[]> kmeans = KMeans.fit(vectors, numClusters, 50);

        int targetIndex = urls.indexOf(productUrl);
        double[] targetVector = vectors[targetIndex];
        int targetCluster = kmeans.predict(targetVector);

        List<String> sameClusterUrls = new ArrayList<>();
        List<Double> sameClusterDistances = new ArrayList<>();

        for (int i = 0; i < urls.size(); i++) {
            if (i != targetIndex && kmeans.predict(vectors[i]) == targetCluster) {
                sameClusterUrls.add(urls.get(i));
                double distance = euclideanDistance(targetVector, vectors[i]);
                sameClusterDistances.add(distance);
            }
        }

        return getTopNClosest(sameClusterUrls, sameClusterDistances, numAnalogues);
    }

    private static double euclideanDistance(double[] v1, double[] v2) {
        double sum = 0.0;
        for (int i = 0; i < v1.length; i++) {
            sum += Math.pow(v1[i] - v2[i], 2);
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

}
