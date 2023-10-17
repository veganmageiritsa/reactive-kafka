package com.nl.analyticsservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.util.UUID;

//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@PrimaryKeyClass
//public class ProductViewCountPrimaryKey {
//    @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
//    private UUID id;
//
//    @PrimaryKeyColumn(name = "count", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
//    private Long count;
//}
