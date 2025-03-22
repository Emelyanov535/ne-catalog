package ru.necatalog.notifications;

import java.math.BigDecimal;

public interface PriceObserver {
    void priceChanged(String productUrl, BigDecimal oldPrice, BigDecimal newPrice);
}

