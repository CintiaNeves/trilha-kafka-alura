package br.com.alura.ecommerce;

import java.io.Closeable;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

 class KafkaDispatcher<T> implements Closeable{

	private final KafkaProducer<String, T> producer;
	
	KafkaDispatcher() {
		this.producer = new KafkaProducer<>(properties());
	}
	
	private static Properties properties() {
		
		Properties properties = new Properties();
		properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
		properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
		return properties;
	}

	 void send(String topic, String key, T value) throws InterruptedException, ExecutionException {
		
		ProducerRecord<String, T> record = new ProducerRecord<>(topic, key.toString(), value);
		Callback callback = (data, ex)->{
			if(ex != null) {
				ex.printStackTrace();
				return;
			}
			
			System.out.println("Sucesso enviando " + data.topic() + " ::: partition " + data.partition() + " / offset " + data.offset() 
			+ " Timestamp " + data.timestamp());
		};
		
		producer.send(record, callback).get();	
	}

	@Override
	public void close() {
		producer.close();
	}
}
