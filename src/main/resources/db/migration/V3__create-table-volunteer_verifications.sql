CREATE TABLE volunteer_verifications
(
    id            UUID         NOT NULL,
    cid           VARCHAR(12)  NOT NULL,
    email         VARCHAR(255) NOT NULL,
    phone         VARCHAR(10)  NOT NULL,
    verify_photos TEXT         NOT NULL,
    cid_front     VARCHAR(255) NOT NULL,
    cid_back      VARCHAR(255) NOT NULL,
    cid_holding   VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    reviewed_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    reviewed_by   UUID,
    volunteer_id  UUID,
    CONSTRAINT pk_volunteer_verifications PRIMARY KEY (id)
);

ALTER TABLE volunteer_verifications
    ADD CONSTRAINT FK_VOLUNTEER_VERIFICATIONS_ON_REVIEWED_BY FOREIGN KEY (reviewed_by) REFERENCES sys_admins (id);

ALTER TABLE volunteer_verifications
    ADD CONSTRAINT FK_VOLUNTEER_VERIFICATIONS_ON_VOLUNTEER FOREIGN KEY (volunteer_id) REFERENCES volunteers (id);