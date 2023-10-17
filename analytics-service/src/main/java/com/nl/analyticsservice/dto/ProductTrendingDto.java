package com.nl.analyticsservice.dto;

import java.util.UUID;

public record ProductTrendingDto(UUID productId, Long viewCount) {
}
