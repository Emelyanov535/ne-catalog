package ru.necatalog.persistence.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import ru.necatalog.persistence.enumeration.Category;
import ru.necatalog.persistence.enumeration.Marketplace;

@Getter
@Setter
@Entity
@Table(name = "product")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @Column(name = "url")
    private String url;

    @Column(name = "marketplace", nullable = false, length = Integer.MAX_VALUE)
    @Enumerated(EnumType.STRING)
    private Marketplace marketplace;

    @Column(name = "category", nullable = false, length = Integer.MAX_VALUE)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "brand", nullable = false, length = Integer.MAX_VALUE)
    private String brand;

    @Column(name = "product_name", nullable = false, length = Integer.MAX_VALUE)
    private String productName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass =
            o instanceof HibernateProxy hp ? hp.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass =
            this instanceof HibernateProxy hp ? hp.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        ProductEntity that = (ProductEntity) o;
        return getUrl() != null && Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hp ?
            hp.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}