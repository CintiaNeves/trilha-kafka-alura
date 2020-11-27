package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;

public class FraudeDetectorService {

	public static void main(String[] args) {
		
		FraudeDetectorService fraudeService = new FraudeDetectorService();
		
		try(KafkaService<Order> service = new KafkaService<Order>(FraudeDetectorService.class.getSimpleName(),
				"ECOMMERCE_NEW_ORDER",
				fraudeService::parse,
				Order.class,
				new HashMap<>())){
			service.run();
		}
	}

	private void parse(ConsumerRecord<String, Order> record) {

		System.out.println("----------------------------------------");
		System.out.println("Processing new order, checking for fraud.");
		System.out.println(record.key());
		System.out.println(record.value());
		System.out.println(record.partition());
		System.out.println(record.offset());
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Order processed.");
	}
}
