CREATE TABLE check_in_places
(
    id              UUID                        NOT NULL,
    location        GEOGRAPHY(Point, 4326)      NOT NULL,
    accuracy_meters FLOAT                       NOT NULL,
    event_id        UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by      UUID,
    CONSTRAINT pk_check_in_places PRIMARY KEY (id)
);

ALTER TABLE check_in_places
    ADD CONSTRAINT FK_CHECK_IN_PLACES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES hosts (id);

ALTER TABLE check_in_places
    ADD CONSTRAINT FK_CHECK_IN_PLACES_ON_EVENT FOREIGN KEY (event_id) REFERENCES events (id);

CREATE INDEX idx_check_in_places_event ON check_in_places (event_id);