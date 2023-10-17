package com.nl.productservice.config;

import com.nl.productservice.domain.Product;
import com.nl.productservice.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.SenderRecord;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
@Component
public class GenerateData implements CommandLineRunner {
    private final ProductRepository productRepository;
    @Override
    public void run(String... args) throws Exception {
//        Flux.range(1, 100)
//                .map(i -> new Product(UUID.randomUUID(), "product"+i, ThreadLocalRandom.current().nextDouble(50, 100)))
//                .collectList()
//                .map(productRepository::saveAll)
//                .subscribe();

        Flux<Product> products = Flux.range(1, 100)
                .map(i -> new Product(UUID.randomUUID(), "product" + i, ThreadLocalRandom.current().nextDouble(50, 100)));

        productRepository.saveAll(products)
                .subscribe();
    }
}
