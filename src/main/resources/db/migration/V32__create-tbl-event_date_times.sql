CREATE TABLE event_date_times
(
    id         UUID                        NOT NULL,
    event_id   UUID                        NOT NULL,
    date       date                        NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_event_date_times PRIMARY KEY (id)
);

ALTER TABLE event_date_times
    ADD CONSTRAINT FK_EVENT_DATE_TIMES_ON_EVENT FOREIGN KEY (event_id) REFERENCES events (id);

CREATE INDEX idx_event_date_times_eventId ON event_date_times (event_id);