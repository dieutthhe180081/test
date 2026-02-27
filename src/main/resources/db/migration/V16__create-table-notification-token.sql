CREATE TABLE notification_token
(
    id        UUID         NOT NULL,
    user_id   UUID,
    token     VARCHAR(255) NOT NULL,
    platform  VARCHAR(255),
    device_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_notification_token PRIMARY KEY (id)
);

ALTER TABLE notification_token
    ADD CONSTRAINT uc_notification_token_token UNIQUE (token);