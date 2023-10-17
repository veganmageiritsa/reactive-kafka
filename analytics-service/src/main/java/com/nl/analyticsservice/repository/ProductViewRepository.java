package com.nl.analyticsservice.repository;

import com.nl.analyticsservice.domain.ProductViewCount;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ProductViewRepository extends ReactiveCassandraRepository<ProductViewCount, UUID> {


//    @Query("SELECT * FROM product_view_count limit 5 ALLOW FILTERING;")
//    Flux<ProductViewCount> findTop5ByOrderByCountDesc();

    @Query("update product_view_count SET counter=counter+?1 WHERE id = ?0;")
    Object updateCounterValue(UUID id, Long counter);

}
