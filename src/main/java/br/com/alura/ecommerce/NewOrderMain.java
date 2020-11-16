package br.com.alura.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

	public static void main(String... args) throws InterruptedException, ExecutionException {

		try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>()) {
			try (KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher<>()) {
				for (int i = 0; i < 10; i++) {

					UUID userId = UUID.randomUUID();
					UUID orderId = UUID.randomUUID();
					
					BigDecimal amount = new BigDecimal(Math.random() * 5000 + 1);
					Order order = new Order(userId.toString(), orderId.toString(), amount);
					
					String body = "Thank you for your order! We are processing your order!";
					String subject = "I dont know yet.";
					
					Email email = new Email(subject, body);

					orderDispatcher.send("ECOMMERCE_NEW_ORDER", userId, order);

					
					emailDispatcher.send("ECOMMERCE_SEND_EMAIL", userId, email);
				}
			}
		}
	}
}
