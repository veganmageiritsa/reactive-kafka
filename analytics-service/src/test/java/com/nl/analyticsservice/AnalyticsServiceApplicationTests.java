package com.nl.analyticsservice;

import com.nl.analyticsservice.dto.ProductTrendingDto;
import com.nl.event.ProductViewEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AutoConfigureWebTestClient
class AnalyticsServiceApplicationTests extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient client;
    UUID uuid1 = UUID.fromString("70145aba-6c28-11ee-b962-0242ac120002");

    UUID uuid2 = UUID.fromString("70145eac-6c28-11ee-b962-0242ac120002");

    UUID uuid3 = UUID.fromString("7014619a-6c28-11ee-b962-0242ac120002");

    UUID uuid4 = UUID.fromString("7014630c-6c28-11ee-b962-0242ac120002");

    UUID uuid5 = UUID.fromString("70146438-6c28-11ee-b962-0242ac120002");

    UUID uuid6 = UUID.fromString("701466fe-6c28-11ee-b962-0242ac120002");

    @Test
    void trendingTest() {


        // emit events
        var events = Flux.just(
                        createEvent(uuid2, 2),
                        createEvent(uuid1, 1),
                        createEvent(uuid6, 3),
                        createEvent(uuid4, 2),
                        createEvent(uuid5, 5),
                        createEvent(uuid4, 2),
                        createEvent(uuid6, 3),
                        createEvent(uuid3, 3)
                ).flatMap(Flux::fromIterable)
                .map(e -> this.toSenderRecord(PRODUCT_VIEW_EVENTS, e.productId().toString(), e));

        var resultFlux = this.<ProductViewEvent>createSender().send(events);

        StepVerifier.create(resultFlux)
                .expectNextCount(21)
                .verifyComplete();

        // verify via trending endpoint
        var mono = this.client
                .get()
                .uri("/trending")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .returnResult(new ParameterizedTypeReference<List<ProductTrendingDto>>() {
                })
                .getResponseBody()
                .next();

        StepVerifier.create(mono)
                .consumeNextWith(this::validateResult)
                .verifyComplete();

    }

    // 6,5,4,3,2   1
    private void validateResult(List<ProductTrendingDto> list) {
        Assertions.assertEquals(5, list.size());
        Assertions.assertEquals(uuid6, list.get(0).productId());
        Assertions.assertEquals(6, list.get(0).viewCount());
        Assertions.assertEquals(uuid2, list.get(4).productId());
        Assertions.assertEquals(2, list.get(4).viewCount());
        Assertions.assertTrue(list.stream().noneMatch(p -> p.productId().equals(uuid1)));
    }

    private List<ProductViewEvent> createEvent(UUID productId, int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> new ProductViewEvent(productId))
                .collect(Collectors.toList());
    }
}
