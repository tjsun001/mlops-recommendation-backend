package com.thurman.consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true")
public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, UUID> {
    // JpaRepository already gives you:
    // existsById(UUID id)
    // save(entity)
    // findById(id)
}
