package ru.necatalog.notifications;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class PriceChangedEvent extends ApplicationEvent {
    private final String productUrl;
    private final BigDecimal oldPrice;
    private final BigDecimal newPrice;

    public PriceChangedEvent(Object source, String productUrl, BigDecimal oldPrice, BigDecimal newPrice) {
        super(source);
        this.productUrl = productUrl;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }
}
