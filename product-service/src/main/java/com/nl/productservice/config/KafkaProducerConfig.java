package com.nl.productservice.config;

import com.nl.event.ProductViewEvent;
import com.nl.productservice.service.ProductViewEventProducer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Sinks;
import reactor.kafka.sender.SenderOptions;

@Configuration
public class KafkaProducerConfig {

    protected static final String PRODUCT_VIEW_EVENTS = "product-view-events";

    @Bean
    public SenderOptions<String, ProductViewEvent> senderOptions(KafkaProperties properties){
        return SenderOptions.create(properties.buildProducerProperties());
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, ProductViewEvent> producerTemplate(SenderOptions<String, ProductViewEvent> senderOptions){
        return new ReactiveKafkaProducerTemplate<>(senderOptions);
    }

    @Bean
    public ProductViewEventProducer productViewEventProducer(ReactiveKafkaProducerTemplate<String, ProductViewEvent> template){
        var sink = Sinks.many().unicast().<ProductViewEvent>onBackpressureBuffer();
        var flux = sink.asFlux();
        var eventProducer = new ProductViewEventProducer(template, sink, flux, PRODUCT_VIEW_EVENTS);
        eventProducer.subscribe();
        return eventProducer;
    }
}