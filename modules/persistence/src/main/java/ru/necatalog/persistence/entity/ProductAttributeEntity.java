package ru.necatalog.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.necatalog.persistence.entity.id.ProductAttributeId;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_attribute")
public class ProductAttributeEntity {

    @EmbeddedId
    private ProductAttributeId id;

    @Column(name = "value_type", nullable = false)
    private String valueType;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "unit")
    private String unit;

    public ProductAttributeEntity(String productUrl, Long attributeId, String valueType, String value, String unit) {
        this.id = new ProductAttributeId(productUrl, attributeId);
        this.value = value;
        this.unit = unit;
        this.valueType = valueType;
    }

}
