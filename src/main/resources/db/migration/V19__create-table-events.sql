CREATE TABLE events
(
    id                     UUID                        NOT NULL,
    host_id                UUID                        NOT NULL,
    organization_id        UUID                        NOT NULL,
    name                   VARCHAR(255)                NOT NULL,
    images                 VARCHAR(500),
    description            VARCHAR(255)                NOT NULL,
    address                VARCHAR(255)                NOT NULL,
    auto_approve           BOOLEAN                     NOT NULL,
    activity_sub_domain_id SMALLINT                    NOT NULL,
    expected_vol_amount    INTEGER                     NOT NULL,
    expected_ser_amount    INTEGER                     NOT NULL,
    served_target          VARCHAR(255),
    serving_place_type     VARCHAR(255),
    start_date             date                        NOT NULL,
    end_date               date                        NOT NULL,
    recruitment_end_date   date                        NOT NULL,
    start_time             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    check_in_code          VARCHAR(6),
    status                 VARCHAR(30)                 NOT NULL,
    created_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by             UUID,
    CONSTRAINT pk_events PRIMARY KEY (id)
);

ALTER TABLE events
    ADD CONSTRAINT FK_EVENTS_ON_ACTIVITY_SUB_DOMAIN FOREIGN KEY (activity_sub_domain_id) REFERENCES activity_sub_domains (id);

ALTER TABLE events
    ADD CONSTRAINT FK_EVENTS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES hosts (id);

ALTER TABLE events
    ADD CONSTRAINT FK_EVENTS_ON_HOST FOREIGN KEY (host_id) REFERENCES hosts (id);

ALTER TABLE events
    ADD CONSTRAINT FK_EVENTS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);