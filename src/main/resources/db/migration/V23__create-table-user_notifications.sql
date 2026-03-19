CREATE TABLE user_notifications
(
    id              UUID                        NOT NULL,
    notification_id UUID                        NOT NULL,
    user_id         UUID                        NOT NULL,
    is_read         BOOLEAN                     NOT NULL,
    read_at         TIMESTAMP WITHOUT TIME ZONE,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_user_notifications PRIMARY KEY (id)
);

ALTER TABLE user_notifications
    ADD CONSTRAINT uk_notification_user UNIQUE (notification_id, user_id);

CREATE INDEX idx_user_created ON user_notifications (user_id, created_at DESC);

ALTER TABLE user_notifications
    ADD CONSTRAINT FK_USER_NOTIFICATIONS_ON_NOTIFICATION FOREIGN KEY (notification_id) REFERENCES notifications (id);

ALTER TABLE user_notifications
    ADD CONSTRAINT FK_USER_NOTIFICATIONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);