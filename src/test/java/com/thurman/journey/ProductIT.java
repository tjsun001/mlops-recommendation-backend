package com.thurman.journey;

import com.thurman.AbstractTestConfig;
import com.thurman.product.NewProductRequest;
import com.thurman.product.ProductResponse;
import com.thurman.product.UpdateProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductIT extends AbstractTestConfig {

    public static final String PRODUCT_BASE_URL = "api/v1/products";
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void canCreateProduct() {
        createProduct(new NewProductRequest(
                "Laptop",
                "1gb ram etc",
                BigDecimal.TEN,
                100,
                "https://amigoscode.com/laptop.png"
        ));
    }

    private UUID createProduct(NewProductRequest request) {
        // when
        return webTestClient
                .post()
                .uri(PRODUCT_BASE_URL)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(UUID.class)
                .value(uuid -> assertThat(uuid).isNotNull())
                .returnResult()
                .getResponseBody();
    }

    @Test
    void canGetAllProducts() {
        // given 1st product
        NewProductRequest laptop = new NewProductRequest(
                "Laptop",
                "1gb ram etc",
                new BigDecimal("10.00"),
                100,
                "https://amigoscode.com/laptop.png"
        );
        var laptopId = createProduct(laptop);

        // given 2nd product
        NewProductRequest tv = new NewProductRequest(
                "Tv",
                "LED with crystal clear nano...",
                new BigDecimal("1.00"),
                50,
                "https://amigoscode.com/tv.png"
        );
        var tvId = createProduct(tv);

        // when
        List<ProductResponse> products = webTestClient.get()
                .uri(PRODUCT_BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<ProductResponse>() {
                })
                .returnResult()
                .getResponseBody();
        // then
        var productsFromDb = products.stream()
                .filter(
                        p -> p.id().equals(tvId) || p.id().equals(laptopId)
                )
                .toList();

        assertThat(productsFromDb)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields(
                        "createdAt", "updatedAt", "deletedAt"
                )
                .containsExactlyInAnyOrder(
                        new ProductResponse(
                                tvId,
                                tv.name(),
                                tv.description(),
                                tv.price(),
                                tv.imageUrl(),
                                tv.stockLevel(),
                                true, null, null, null
                        ),
                        new ProductResponse(
                                laptopId,
                                laptop.name(),
                                laptop.description(),
                                laptop.price(),
                                laptop.imageUrl(),
                                laptop.stockLevel(),
                                true, null, null, null
                        )
                );
    }

    @Test
    void canGetProductById() {
        // given
        NewProductRequest laptop = new NewProductRequest(
                "Laptop",
                "1gb ram etc",
                new BigDecimal("10.00"),
                100,
                "https://amigoscode.com/laptop.png"
        );
        var laptopId = createProduct(laptop);

        // when
        ProductResponse responseBody = webTestClient.get()
                .uri(PRODUCT_BASE_URL + "/" + laptopId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ProductResponse>() {
                })
                .returnResult()
                .getResponseBody();

        // then
        assertThat(responseBody)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt", "deletedAt")
                .isEqualTo(
                        new ProductResponse(
                                laptopId,
                                laptop.name(),
                                laptop.description(),
                                laptop.price(),
                                laptop.imageUrl(),
                                laptop.stockLevel(),
                                true, null, null, null
                        )
                );
    }

    @Test
    void canGetDeleteProductById() {
        // given
        NewProductRequest laptop = new NewProductRequest(
                "Laptop",
                "1gb ram etc",
                new BigDecimal("10.00"),
                100,
                "https://amigoscode.com/laptop.png"
        );
        var laptopId = createProduct(laptop);

        // when
        webTestClient.delete()
                .uri(PRODUCT_BASE_URL + "/" + laptopId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty();

        // then
        webTestClient.get()
                .uri(PRODUCT_BASE_URL + "/" + laptopId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("product with id [" + laptopId +"] not found")
                .jsonPath("$.error").isEqualTo("Not Found")
                .jsonPath("$.statusCode").isEqualTo("404")
                .jsonPath("$.path").isEqualTo("/api/v1/products/" + laptopId)
                .jsonPath("$.fieldError").doesNotExist();

//        {"message":"product with id [02682eec-6d8e-43ac-a5c8-60dd7bb6fa14] not found","error":"Not Found","statusCode":404,"path":"/api/v1/products/02682eec-6d8e-43ac-a5c8-60dd7bb6fa14","timestamp":"2025-06-26T16:00:01.854366Z","fieldErrors":null}


    }

    @Test
    void canUpdateProduct() {
        // given
        NewProductRequest laptop = new NewProductRequest(
                "Laptop",
                "1gb ram etc",
                new BigDecimal("10.00"),
                100,
                "https://amigoscode.com/laptop.png"
        );
        var laptopId = createProduct(laptop);

        // when
        webTestClient.put()
                .uri(PRODUCT_BASE_URL + "/" + laptopId)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateProductRequest(
                        null, null, null, new BigDecimal("200.00"), 500, false
                ))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .isEmpty();
        // then
        ProductResponse responseBody = webTestClient.get()
                .uri(PRODUCT_BASE_URL + "/" + laptopId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<ProductResponse>() {
                })
                .returnResult()
                .getResponseBody();

        // then
        assertThat(responseBody)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt", "deletedAt")
                .isEqualTo(
                        new ProductResponse(
                                laptopId,
                                laptop.name(),
                                laptop.description(),
                                new BigDecimal("200.00"),
                                laptop.imageUrl(),
                                500,
                                false, null, null, null
                        )
                );
    }
}
