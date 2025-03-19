package ru.necatalog.notifications;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PriceService implements PriceSubject {
    private List<PriceObserver> observers = new ArrayList<>();

    @Override
    public void registerObserver(PriceObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(PriceObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String productUrl, BigDecimal oldPrice, BigDecimal newPrice) {
        for (PriceObserver observer : observers) {
            observer.priceChanged(productUrl, oldPrice, newPrice);
        }
    }

    public void checkPriceAndNotify(String productUrl, BigDecimal newPrice) {
        // Получаем старую цену из базы данных (предположим, из PriceHistoryEntity)
        BigDecimal oldPrice = getPriceFromDatabase(productUrl);

        // Если цена изменилась, уведомляем наблюдателей
        if (oldPrice == null || oldPrice.compareTo(newPrice) != 0) {
            notifyObservers(productUrl, oldPrice, newPrice);
        }
    }

    private BigDecimal getPriceFromDatabase(String productUrl) {
        // Логика получения предыдущей цены товара из базы данных
        return null; // для примера
    }
}

