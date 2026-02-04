CREATE TABLE hosts
(
    id              UUID                        NOT NULL,
    cid             VARCHAR(12)                 NOT NULL,
    email           VARCHAR(255)                NOT NULL,
    phone           VARCHAR(10)                 NOT NULL,
    full_name       VARCHAR(100),
    gender          BOOLEAN                     NOT NULL,
    dob             date,
    avatar_url      VARCHAR(255),
    address         VARCHAR(50),
    detail_address  VARCHAR(100),
    created_by      UUID,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    organization_id UUID                        NOT NULL,
    CONSTRAINT pk_hosts PRIMARY KEY (id)
);

ALTER TABLE hosts
    ADD CONSTRAINT uc_hosts_email UNIQUE (email);

ALTER TABLE hosts
    ADD CONSTRAINT FK_HOSTS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES org_managers (id);

ALTER TABLE hosts
    ADD CONSTRAINT FK_HOSTS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);