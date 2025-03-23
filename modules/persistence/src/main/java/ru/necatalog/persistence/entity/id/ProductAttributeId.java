package ru.necatalog.persistence.entity.id;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ProductAttributeId implements Serializable {

    @Column(name = "product_url", nullable = false, unique = true)
    private String productUrl;

    @Column(name = "attribute_id", nullable = false)
    private Long attributeId;

}
