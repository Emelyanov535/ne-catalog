package ru.necatalog.notifications.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.necatalog.persistence.entity.PriceChangeMessage;
import ru.necatalog.persistence.entity.UserEntity;
import ru.necatalog.persistence.repository.PriceChangeMessageRepository;
import ru.necatalog.persistence.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceChangeMessageProcessor {

	private final PriceChangeMessageRepository priceChangeMessageRepository;
	private final UserRepository userRepository;
	private final MailSenderService mailSenderService;

	@Transactional
	public void processMessages() {
		List<PriceChangeMessage> newMessages = priceChangeMessageRepository.findAllByProcessedFalse();

		Map<UserEntity, List<PriceChangeMessage>> userMessages = newMessages.stream()
				.flatMap(message -> userRepository.findAllNotificatedUsers(message.getProduct().getId()).stream()
						.map(user -> Map.entry(user, message)))
				.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

		userMessages.forEach(this::sendEmailAsync);

		priceChangeMessageRepository.markMessagesAsProcessed(newMessages);
	}

	@Async
	public void sendEmailAsync(UserEntity user, List<PriceChangeMessage> messages) {
		StringBuilder emailBody = new StringBuilder("Изменения цен:\n");
		messages.forEach(msg -> emailBody.append("Товар: ")
				.append(msg.getProduct().getProductName())
				.append(" | Было: ")
				.append(msg.getOldPrice())
				.append(" | Стало: ")
				.append(msg.getNewPrice())
				.append("\n"));

		mailSenderService.sendMail(user.getUsername(), "Обновление цен", emailBody.toString());
	}
}