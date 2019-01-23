package com.josetesan.busExtractor.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;


@Configuration
public class BinderConfiguration {

    @Bean
    public KafkaTemplate kafkaTemplate(ProducerFactory<String, GenericRecord> producerFactory) {

        return new KafkaTemplate(producerFactory);

    }

    /**
     * @see https://dzone.com/articles/kafka-avro-serialization-and-the-schema-registry
     * @see https://www.e4developer.com/2018/05/21/getting-started-with-kafka-in-spring-boot/
     * @see https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-messaging.html#boot-features-kafka
     * @return a configured producerfactory
     */
    @Bean
    public ProducerFactory<String, GenericRecord> producerFactory() {
        return new DefaultKafkaProducerFactory<String, GenericRecord>(null, new StringSerializer(), new KafkaAvroSerializer());
    }
}
