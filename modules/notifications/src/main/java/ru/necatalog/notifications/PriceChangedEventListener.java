package ru.necatalog.notifications;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceChangedEventListener {

    @EventListener
    public void handlePriceChangedEvent(PriceChangedEvent event) {
        String productUrl = event.getProductUrl();
        BigDecimal oldPrice = event.getOldPrice();
        BigDecimal newPrice = event.getNewPrice();

        // Логика обработки изменения цены, например, уведомления
        System.out.println("Цена товара с URL " + productUrl + " изменилась с " + oldPrice + " на " + newPrice);
        // Здесь можно добавить отправку уведомлений пользователю или другие действия
    }
}

