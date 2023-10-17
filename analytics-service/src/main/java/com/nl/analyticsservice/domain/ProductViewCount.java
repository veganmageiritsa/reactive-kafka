package com.nl.analyticsservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(value = "product_view_count")
public class ProductViewCount {

    @PrimaryKey
    private UUID id;

    private Long count;
}
