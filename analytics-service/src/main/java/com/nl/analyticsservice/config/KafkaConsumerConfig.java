package com.nl.analyticsservice.config;

import com.nl.event.ProductViewEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;

@Configuration
public class KafkaConsumerConfig {

    public static final String PRODUCT_VIEW_EVENTS = "product-view-events";

    @Bean
    public ReceiverOptions<String, ProductViewEvent> receiverOptions(KafkaProperties properties){
        return ReceiverOptions.<String, ProductViewEvent>create(properties.buildConsumerProperties())
                .consumerProperty(JsonDeserializer.VALUE_DEFAULT_TYPE, ProductViewEvent.class)
                .consumerProperty(JsonDeserializer.USE_TYPE_INFO_HEADERS, false)
                .subscription(List.of(PRODUCT_VIEW_EVENTS));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, ProductViewEvent> kafkaConsumerTemplate(ReceiverOptions<String, ProductViewEvent> receiverOptions){
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
