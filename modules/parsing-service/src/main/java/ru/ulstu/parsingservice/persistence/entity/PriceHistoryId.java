package ru.ulstu.parsingservice.persistence.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PriceHistoryId implements Serializable {

    @Column(name = "product_url", nullable = false, unique = true)
    private String productUrl;

    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass =
            this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        PriceHistoryId that = (PriceHistoryId) o;
        return getDate() != null && Objects.equals(getDate(), that.getDate());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(date);
    }

}
