CREATE TABLE activity_domains
(
    id                       SMALLINT                    NOT NULL,
    name                     VARCHAR(50)                 NOT NULL,
    active                   BOOLEAN                     NOT NULL,
    earliest_start_time      time WITHOUT TIME ZONE      NOT NULL,
    latest_end_time          time WITHOUT TIME ZONE      NOT NULL,
    default_session_max_time SMALLINT                    NOT NULL,
    special_session_max_time SMALLINT,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_activity_domains PRIMARY KEY (id)
);