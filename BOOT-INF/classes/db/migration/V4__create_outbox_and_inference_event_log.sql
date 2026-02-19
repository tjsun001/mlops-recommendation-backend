-- Outbox table: written by backend, read by outbox publisher
CREATE TABLE IF NOT EXISTS outbox_events (
                                             id UUID PRIMARY KEY,
                                             event_type TEXT NOT NULL,
                                             aggregate_id TEXT NOT NULL,
                                             payload JSONB NOT NULL,

                                             status TEXT NOT NULL DEFAULT 'NEW',  -- NEW, SENT, FAILED
                                             attempt_count INT NOT NULL DEFAULT 0,
                                             last_error TEXT NULL,

                                             created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                             sent_at TIMESTAMP NULL
);

CREATE INDEX IF NOT EXISTS idx_outbox_events_status_created_at
    ON outbox_events (status, created_at);


-- Inference event log: written by Kafka consumer (ml-events-worker)
CREATE TABLE IF NOT EXISTS inference_event_log (
                                                   event_id UUID PRIMARY KEY,
                                                   event_type TEXT NOT NULL,
                                                   event_time TIMESTAMP NOT NULL,

                                                   user_id TEXT NOT NULL,
                                                   model_version TEXT NULL,
                                                   latency_ms INT NULL,

                                                   recommendations JSONB NOT NULL,

                                                   ingested_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_inference_event_log_user_time
    ON inference_event_log (user_id, event_time);

CREATE INDEX IF NOT EXISTS idx_inference_event_log_event_time
    ON inference_event_log (event_time);
