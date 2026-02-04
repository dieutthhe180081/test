CREATE TABLE organizations
(
    id                UUID                        NOT NULL,
    name              VARCHAR(255)                NOT NULL,
    dha_registered    BOOLEAN                     NOT NULL,
    org_type          VARCHAR(50)                 NOT NULL,
    org_introduction  VARCHAR(500)                NOT NULL,
    manager_full_name VARCHAR(100)                NOT NULL,
    manager_cid       VARCHAR(12)                 NOT NULL,
    manager_phone     VARCHAR(10)                 NOT NULL,
    manager_email     VARCHAR(255)                NOT NULL,
    other_images      VARCHAR(500),
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by        UUID,
    CONSTRAINT pk_organizations PRIMARY KEY (id)
);

ALTER TABLE organizations
    ADD CONSTRAINT FK_ORGANIZATIONS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES sys_admins (id);