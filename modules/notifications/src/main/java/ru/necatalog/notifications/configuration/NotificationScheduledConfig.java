package ru.necatalog.notifications.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import ru.necatalog.notifications.service.PriceChangeMessageProcessor;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "notification", name = "status", havingValue = "true")
public class NotificationScheduledConfig {

	private final PriceChangeMessageProcessor processor;

	@Scheduled(fixedRate = 10000)
	public void processPriceChangeMessages() {
		processor.processMessages();
	}
}
