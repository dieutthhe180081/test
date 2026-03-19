CREATE TABLE volunteer_saved_events
(
    id           UUID NOT NULL,
    volunteer_id UUID,
    event_id     UUID,
    created_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_volunteer_saved_events PRIMARY KEY (id)
);

ALTER TABLE volunteer_saved_events
    ADD CONSTRAINT uk_volunteer_saved_events UNIQUE (volunteer_id, event_id);

ALTER TABLE volunteer_saved_events
    ADD CONSTRAINT FK_VOLUNTEER_SAVED_EVENTS_ON_EVENT FOREIGN KEY (event_id) REFERENCES events (id);

ALTER TABLE volunteer_saved_events
    ADD CONSTRAINT FK_VOLUNTEER_SAVED_EVENTS_ON_VOLUNTEER FOREIGN KEY (volunteer_id) REFERENCES volunteers (id);

CREATE INDEX idx_volunteer_saved_events_volunteer_id ON volunteer_saved_events (volunteer_id);