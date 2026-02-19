package com.thurman.consumer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * NOTE:
 * Do NOT put @ConditionalOnProperty on Spring Data repositories.
 * If you want to gate Kafka/consumer behavior, gate the service/listener layer instead.
 */
public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, UUID> {
    // JpaRepository already gives you:
    // existsById(UUID id)
    // save(entity)
    // findById(id)
}
