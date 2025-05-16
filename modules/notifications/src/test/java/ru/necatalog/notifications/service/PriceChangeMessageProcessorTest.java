package ru.necatalog.notifications.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.necatalog.persistence.entity.PriceChangeMessage;
import ru.necatalog.persistence.entity.ProductEntity;
import ru.necatalog.persistence.entity.UserEntity;
import ru.necatalog.persistence.repository.PriceChangeMessageRepository;
import ru.necatalog.persistence.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriceChangeMessageProcessorTest {

	@Mock
	private PriceChangeMessageRepository messageRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private MailSenderService mailSenderService;

	@InjectMocks
	private PriceChangeMessageProcessor processor;

	@Test
	void processMessages_ShouldSendEmailsAndMarkMessagesProcessed() {
		ProductEntity product = new ProductEntity();
		product.setProductName("Товар");
		product.setUrl("product-url");

		UserEntity user = new UserEntity();
		user.setUsername("user@example.com");

		PriceChangeMessage message = PriceChangeMessage.builder()
				.product(product)
				.user(user)
				.oldPrice(BigDecimal.valueOf(100))
				.newPrice(BigDecimal.valueOf(80))
				.processed(false)
				.build();

		when(messageRepository.findAllByProcessedFalse()).thenReturn(List.of(message));
		when(userRepository.findAllNotificatedUsers("product-url")).thenReturn(List.of(user));

		processor.processMessages();

		verify(mailSenderService).sendMail(eq("user@example.com"), anyString(), contains("Товар"));
		verify(messageRepository).markMessagesAsProcessed(List.of(message));
	}
}

