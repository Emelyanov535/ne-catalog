package ru.necatalog.notifications;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PriceNotificationService implements PriceObserver {
    @Override
    public void priceChanged(String productUrl, BigDecimal oldPrice, BigDecimal newPrice) {
        // Логика уведомлений
        System.out.println("Цена товара с URL " + productUrl + " изменилась с " + oldPrice + " на " + newPrice);
        // Здесь можно добавить отправку уведомлений пользователю
    }
}
