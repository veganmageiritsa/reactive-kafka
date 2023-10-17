package com.nl.analyticsservice.service;

import com.nl.analyticsservice.domain.ProductViewCount;
import com.nl.analyticsservice.repository.ProductViewRepository;
import com.nl.event.ProductViewEvent;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductViewEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(ProductViewEventConsumer.class);
    private final ReactiveKafkaConsumerTemplate<String, ProductViewEvent> template;
    private final ProductViewRepository productViewRepository;

    @PostConstruct
    public void subscribe() {
        template.receive()
                .bufferTimeout(1000, Duration.ofSeconds(1))
                .flatMap(this::updateBatch)
                .subscribe();
    }

    private Mono<Void> updateBatch(List<ReceiverRecord<String, ProductViewEvent>> events) {
        Map<UUID, Long> scores = events
                .stream()
                .map(r -> r.value().productId())
                .collect(Collectors.groupingBy(
                        Function.identity()
                        , Collectors.counting()));

        return productViewRepository.findAllById(scores.keySet())
                .collectMap(ProductViewCount::getId)
                .defaultIfEmpty(Collections.emptyMap())
                .map(dbMap -> scores.keySet()
                        .stream()
                        .map(productId -> updateViewCount(dbMap, scores, productId))
                        .collect(Collectors.toList()))
                .flatMapMany(this.productViewRepository::saveAll)
                .doOnComplete(() -> events.get(events.size() - 1).receiverOffset().acknowledge())
                .doOnError(ex -> log.error(ex.getMessage()))
                .then();

    }

    private ProductViewCount updateViewCount(Map<UUID, ProductViewCount> dbMap, Map<UUID, Long> eventMap, UUID productId) {
        var pvc = dbMap.getOrDefault(productId, new ProductViewCount(productId, 0L));
        pvc.setCount(pvc.getCount() + eventMap.get(productId));
        return pvc;
    }
}
