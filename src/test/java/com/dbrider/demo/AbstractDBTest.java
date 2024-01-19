package com.dbrider.demo;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
abstract class AbstractDBTest {

    @Container
    static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("itemsdb")
            .withUsername("hello")
            .withPassword("world");

    @DynamicPropertySource
    static void loadProps(final DynamicPropertyRegistry registry) {
        System.out.println(CONTAINER.getJdbcUrl());

        registry.add("spring.datasource.url", CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", () -> "hello");
        registry.add("spring.datasource.password", () -> "world");
    }

}
