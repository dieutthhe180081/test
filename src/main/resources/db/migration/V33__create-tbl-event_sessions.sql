CREATE TABLE event_sessions
(
    id                  UUID                        NOT NULL,
    event_id            UUID                        NOT NULL,
    start_date_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date_time       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expected_vol_amount INTEGER                     NOT NULL,
    expected_ser_amount INTEGER                     NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_event_sessions PRIMARY KEY (id)
);

ALTER TABLE event_sessions
    ADD CONSTRAINT FK_EVENT_SESSIONS_ON_EVENT FOREIGN KEY (event_id) REFERENCES events (id);

CREATE INDEX idx_event_sessions_eventId ON event_sessions (event_id);