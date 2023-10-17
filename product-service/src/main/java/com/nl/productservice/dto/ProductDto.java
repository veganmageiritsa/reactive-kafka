package com.nl.productservice.dto;

import java.util.UUID;

public record ProductDto(UUID id, String description, Double price) {
}
