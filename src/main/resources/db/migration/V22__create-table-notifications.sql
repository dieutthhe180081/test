CREATE TABLE notifications
(
    id         UUID                        NOT NULL,
    user_id    UUID,
    topic      VARCHAR(255),
    type       VARCHAR(255),
    title      VARCHAR(255)                NOT NULL,
    body       VARCHAR(255)                NOT NULL,
    data       JSONB,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id)
);

ALTER TABLE notifications
    ADD CONSTRAINT FK_NOTIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);