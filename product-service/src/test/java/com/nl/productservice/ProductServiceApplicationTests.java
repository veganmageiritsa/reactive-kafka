package com.nl.productservice;

import com.nl.event.ProductViewEvent;
import com.nl.productservice.domain.Product;
import com.nl.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.UUID;

@AutoConfigureWebTestClient
class ProductServiceApplicationTests extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void productViewAndEventTest() {
        UUID uuid1 = UUID.fromString("3e8a2ed8-69b3-11ee-8c99-0242ac120002");
        Product product1 = new Product(uuid1, "product1", 45.6);
        productRepository.save(product1)
                .subscribe();

        UUID uuid2 = UUID.fromString("3e8a32d4-69b3-11ee-8c99-0242ac120002");
        Product product2 = new Product(uuid2, "product2", 51.4);
        productRepository.save(product2)
                .subscribe();
        viewProduct(uuid1);
        viewProduct(uuid1);
        viewProductError(UUID.randomUUID());
        viewProduct(uuid2);

        var receiver = this.<ProductViewEvent>createReceiver(PRODUCT_VIEW_EVENTS)
                .receive()
                .take(3);

        StepVerifier.create(receiver)
                .consumeNextWith(r -> Assertions.assertEquals(uuid1, r.value().productId()))
                .consumeNextWith(r -> Assertions.assertEquals(uuid1, r.value().productId()))
                .consumeNextWith(r -> Assertions.assertEquals(uuid2, r.value().productId()))
                .verifyComplete();


    }

    private void viewProduct(UUID uuid) {
        this.webTestClient
                .get()
                .uri("/product/" + uuid)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.id").isEqualTo(uuid.toString());
    }

    private void viewProductError(UUID uuid){
        this.webTestClient
                .get()
                .uri("/product/" + uuid)
                .exchange()
                .expectStatus().is4xxClientError();
    }

}
