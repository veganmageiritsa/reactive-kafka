package com.nl.analyticsservice.service;

import com.nl.analyticsservice.domain.ProductViewCount;
import com.nl.analyticsservice.dto.ProductTrendingDto;
import com.nl.analyticsservice.repository.ProductViewRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class ProductTrendingBroadcast {

    private static final Logger log = LoggerFactory.getLogger(ProductTrendingBroadcast.class);
    private final ProductViewRepository repository;
    private Flux<List<ProductTrendingDto>> trends;

    @PostConstruct
    private void init() {
        this.trends = this.repository.findAll()
                .sort(Comparator.comparing(ProductViewCount::getCount).reversed())
                .take(5)
                .map(pvc -> new ProductTrendingDto(pvc.getId(), pvc.getCount()))
                .doOnNext(ptd->log.info("Next ptd : {}", ptd))
                .collectList()
                .filter(Predicate.not(List::isEmpty))
                .repeatWhen(l -> l.delayElements(Duration.ofSeconds(30)))
                .distinctUntilChanged()
                .cache(1);
    }

    public Flux<List<ProductTrendingDto>> getTrends() {
        return this.trends;
    }
}
