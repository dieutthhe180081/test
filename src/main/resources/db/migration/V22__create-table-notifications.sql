CREATE TABLE notifications
(
    id         UUID                        NOT NULL,
    topic      VARCHAR(255),
    type       VARCHAR(255)                NOT NULL,
    title      VARCHAR(255)                NOT NULL,
    body       TEXT                        NOT NULL,
    data       JSONB,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

CREATE INDEX idx_notification_created ON notifications (created_at DESC);