package com.thurman.product;

import com.thurman.SharedPostgresContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Testcontainers
class ProductRepositoryTest {

    @Container
    @ServiceConnection
    private static final SharedPostgresContainer POSTGRES =
            SharedPostgresContainer.getInstance();


    @Autowired
    private ProductRepository underTest;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    void canFindAvailablePublishedProducts() {
        // given
        Product product1 = new Product(
                UUID.randomUUID(),
                "iphone",
                "bardnjknjkndsjknkjnajkndjksandsajkndkjasnkdjank",
                new BigDecimal("1000"),
                "https://amigoscode.com/logo.png",
                10
        );

        Product product2 = new Product(
                UUID.randomUUID(),
                "samsung",
                "bardnjknjkndsjknkjnajkndjksandsajkndkjasnkdjank",
                new BigDecimal("1200"),
                "https://amigoscode.com/logo.png",
                5
        );
        product2.setPublished(false);

        Product product3 = new Product(
                UUID.randomUUID(),
                "watch",
                "bardnjknjkndsjknkjnajkndjksandsajkndkjasnkdjank",
                new BigDecimal("5000"),
                "https://amigoscode.com/logo.png",
                0
        );

        Product product4 = new Product(
                UUID.randomUUID(),
                "PS5",
                "bardnjknjkndsjknkjnajkndjksandsajkndkjasnkdjank",
                new BigDecimal("300"),
                "https://amigoscode.com/logo.png",
                90
        );

        underTest.saveAll(
                List.of(product1, product2, product3, product4)
        );
        // when
        List<Product> availablePublishedProducts =
                underTest.findAvailablePublishedProducts();
        // then
        assertThat(availablePublishedProducts)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields(
                        "updatedAt", "createdAt"
                )
                .containsExactly(
                        product4, product1
                );
    }
}