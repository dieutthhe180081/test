CREATE TABLE activity_sub_domains
(
    id                 SMALLINT                    NOT NULL,
    activity_domain_id SMALLINT                    NOT NULL,
    name               VARCHAR(50)                 NOT NULL,
    active             BOOLEAN                     NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_activity_sub_domains PRIMARY KEY (id)
);

ALTER TABLE activity_sub_domains
    ADD CONSTRAINT FK_ACTIVITY_SUB_DOMAINS_ON_ACTIVITY_DOMAIN FOREIGN KEY (activity_domain_id) REFERENCES activity_domains (id);