package br.com.alura.ecommerce;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

 class KafkaService<T> implements Closeable{

	private final KafkaConsumer<String, T> consumer;
	private final ConsumerFunction<T> parse;

	KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Class<T> type) {
		this(parse, groupId, type);
		consumer.subscribe(Collections.singletonList(topic));
	}
	
	KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Class<T> type) {
		this(parse, groupId, type);
		consumer.subscribe(topic);
	}

	private KafkaService(ConsumerFunction<T> parse, String groupId, Class<T> type) {
		this.parse = parse;
		this.consumer = new KafkaConsumer<>(properties(type, groupId));
	}



	void run() {
		while (true) {
			ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(100));

			if (!records.isEmpty()) {
				
				System.out.println("Encontrei " + records.count() + "registros");
				for (ConsumerRecord<String, T> record : records) {
					parse.consume(record);
				}
			}
		}

	}

	private Properties properties(Class<T> type, String groupId) {

		Properties properties = new Properties();
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, EmailService.class.getSimpleName());
		properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		properties.setProperty(GsonDeserializer.TYPE_CONFIG, type.getName());

		return properties;
	}

	@Override
	public void close() {
		
		consumer.close();
	}

}