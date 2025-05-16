package ru.necatalog.notifications.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import ru.necatalog.notifications.service.PriceChangeMessageProcessor;
import ru.necatalog.notifications.service.PriceDropNotificationService;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "notification", name = "status", havingValue = "true")
public class NotificationScheduledConfig {

	private final PriceChangeMessageProcessor processor;
	private final PriceDropNotificationService priceDropService;

	@Scheduled(fixedRate = 60000)
	public void processPriceChangeMessages() {
		priceDropService.checkPriceDrops();
		processor.processMessages();
	}
}
