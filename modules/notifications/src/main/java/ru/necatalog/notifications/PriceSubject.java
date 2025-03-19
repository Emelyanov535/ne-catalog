package ru.necatalog.notifications;

import java.math.BigDecimal;

public interface PriceSubject {
    void registerObserver(PriceObserver observer);
    void removeObserver(PriceObserver observer);
    void notifyObservers(String productUrl, BigDecimal oldPrice, BigDecimal newPrice);
}

