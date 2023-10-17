package com.nl.productservice.mapper;

import com.nl.productservice.domain.Product;
import com.nl.productservice.dto.ProductDto;

public class ProductMapper {

    public static ProductDto toProductDto(Product product){
        return new ProductDto(product.getId(), product.getDescription(), product.getPrice());
    }
}
