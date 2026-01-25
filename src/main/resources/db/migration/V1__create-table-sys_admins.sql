CREATE TABLE sys_admins
(
    id             UUID         NOT NULL,
    cid            VARCHAR(12)  NOT NULL,
    email          VARCHAR(255) NOT NULL,
    phone          VARCHAR(10)  NOT NULL,
    full_name      VARCHAR(100),
    gender         BOOLEAN      NOT NULL,
    dob            date,
    avatar_url     VARCHAR(255),
    address        VARCHAR(50),
    detail_address VARCHAR(100),
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_sys_admins PRIMARY KEY (id)
);

ALTER TABLE sys_admins
    ADD CONSTRAINT uc_sys_admins_cid UNIQUE (cid);

ALTER TABLE sys_admins
    ADD CONSTRAINT uc_sys_admins_email UNIQUE (email);

ALTER TABLE sys_admins
    ADD CONSTRAINT uc_sys_admins_phone UNIQUE (phone);