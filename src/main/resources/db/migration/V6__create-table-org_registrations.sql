CREATE TABLE org_registrations
(
    id                  UUID                        NOT NULL,
    name                VARCHAR(255)                NOT NULL,
    dha_registered      BOOLEAN                     NOT NULL,
    org_type            VARCHAR(50)                 NOT NULL,
    org_introduction    VARCHAR(500)                NOT NULL,
    manager_full_name   VARCHAR(100)                NOT NULL,
    manager_cid         VARCHAR(12)                 NOT NULL,
    manager_phone       VARCHAR(10)                 NOT NULL,
    manager_email       VARCHAR(255)                NOT NULL,
    manager_cid_front   VARCHAR(255)                NOT NULL,
    manager_cid_back    VARCHAR(255)                NOT NULL,
    manager_cid_holding VARCHAR(255)                NOT NULL,
    other_images        VARCHAR(500),
    application_reason  VARCHAR(255),
    status              VARCHAR(30)                 NOT NULL,
    rejection_reason    VARCHAR(255),
    created_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    reviewed_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    reviewed_by         UUID,
    organization_id     UUID,
    org_manager_id      UUID,
    CONSTRAINT pk_org_registrations PRIMARY KEY (id)
);

ALTER TABLE org_registrations
    ADD CONSTRAINT FK_ORG_REGISTRATIONS_ON_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organizations (id);

ALTER TABLE org_registrations
    ADD CONSTRAINT FK_ORG_REGISTRATIONS_ON_ORG_MANAGER FOREIGN KEY (org_manager_id) REFERENCES org_managers (id);

ALTER TABLE org_registrations
    ADD CONSTRAINT FK_ORG_REGISTRATIONS_ON_REVIEWED_BY FOREIGN KEY (reviewed_by) REFERENCES sys_admins (id);