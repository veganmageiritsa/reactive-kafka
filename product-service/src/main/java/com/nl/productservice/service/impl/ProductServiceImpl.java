package com.nl.productservice.service.impl;

import com.nl.event.ProductViewEvent;
import com.nl.productservice.dto.ProductDto;
import com.nl.productservice.mapper.ProductMapper;
import com.nl.productservice.repository.ProductRepository;
import com.nl.productservice.service.ProductService;
import com.nl.productservice.service.ProductViewEventProducer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductViewEventProducer productViewEventProducer;
    @Override
    public Mono<ProductDto> getProduct(UUID productId) {
        return productRepository.findById(productId)
                .doOnNext(e -> this.productViewEventProducer.emitEvent(new ProductViewEvent(e.getId())))
                .map(ProductMapper::toProductDto);
    }
}
