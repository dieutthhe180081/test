CREATE TABLE event_images
(
    id         UUID         NOT NULL,
    event_id   UUID,
    image_path VARCHAR(255) NOT NULL,
    CONSTRAINT pk_event_images PRIMARY KEY (id)
);

ALTER TABLE event_images
    ADD CONSTRAINT FK_EVENT_IMAGES_ON_EVENT FOREIGN KEY (event_id) REFERENCES events (id);

CREATE INDEX idx_event_images_event_id ON event_images (event_id);