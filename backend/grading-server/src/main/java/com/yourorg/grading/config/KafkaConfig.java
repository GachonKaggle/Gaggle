package com.yourorg.grading.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration  // Marks this class as a Spring configuration class
public class KafkaConfig {

    // Configure the Kafka producer factory to send byte[] payloads
    @Bean
    public ProducerFactory<String, byte[]> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");  // Kafka broker address
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);  // Key serializer
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);  // Value serializer (byte[])
        configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 524288000);  // Max request size: 500MB
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 524288000);     // Total memory buffer: 500MB
        return new DefaultKafkaProducerFactory<>(configProps);  // Return configured factory
    }

    // Create KafkaTemplate using the above producer factory
    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Configure the Kafka consumer factory to deserialize JSON payloads into Java objects
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");  // Kafka broker
        // No explicit group-id here (assumed to be provided via application.yml)
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");  // Start from earliest offset if no commit exists
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);  // Key deserializer
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                  "org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");  // Value wrapper
        props.put("spring.deserializer.value.delegate.class",
                  "org.springframework.kafka.support.serializer.JsonDeserializer");  // Delegate to JSON deserializer
        props.put("spring.json.trusted.packages", "com.yourorg.*");  // Trust all packages under com.yourorg
        props.put("spring.json.value.default.type",
                  "com.yourorg.grading.adapter.out.dto.SendRequestDto");  // Default type to deserialize into
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 524288000);  // Max per-partition fetch size
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 524288000);            // Max fetch size for full response
        return new DefaultKafkaConsumerFactory<>(props);  // Return configured factory
    }
}
