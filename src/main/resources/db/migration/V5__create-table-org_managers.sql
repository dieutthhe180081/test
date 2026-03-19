CREATE TABLE org_managers
(
    id              UUID                        NOT NULL,
    cid             VARCHAR(12)                 NOT NULL,
    email           VARCHAR(255)                NOT NULL,
    phone           VARCHAR(10)                 NOT NULL,
    full_name       VARCHAR(100),
    gender          BOOLEAN,
    dob             date,
    avatar_url      VARCHAR(255),
    address         VARCHAR(50),
    detail_address  VARCHAR(100),
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by      UUID,
    organization_id UUID                        NOT NULL,
    CONSTRAINT pk_org_managers PRIMARY KEY (id)
);

ALTER TABLE org_managers
    ADD CONSTRAINT uc_org_managers_email UNIQUE (email);

ALTER TABLE org_managers
    ADD CONSTRAINT FK_ORG_MANAGERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES sys_admins (id);

ALTER TABLE org_managers
    ADD CONSTRAINT FK_ORG_MANAGERS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);