package com.nl.productservice.service;

import com.nl.productservice.dto.ProductDto;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface ProductService {
    Mono<ProductDto> getProduct(UUID productId);
}
