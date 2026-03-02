CREATE TABLE notification_topic_subscriptions
(
    id         UUID                        NOT NULL,
    user_id    UUID                        NOT NULL,
    topic      VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_notification_topic_subscription PRIMARY KEY (id)
);

ALTER TABLE notification_topic_subscriptions
    ADD CONSTRAINT FK_NOTIFICATION_TOPIC_SUBSCRIPTION_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX idx_notification_topic_subscription_user ON notification_topic_subscriptions (user_id);