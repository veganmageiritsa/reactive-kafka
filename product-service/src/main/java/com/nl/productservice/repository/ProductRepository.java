package com.nl.productservice.repository;

import com.nl.productservice.domain.Product;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

import java.util.UUID;

public interface ProductRepository extends ReactiveCassandraRepository<Product, UUID> {
}
