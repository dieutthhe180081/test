CREATE TABLE volunteers
(
    id              UUID                        NOT NULL,
    vid             UUID                        NOT NULL,
    cid             VARCHAR(12)                 NOT NULL,
    email           VARCHAR(255)                NOT NULL,
    phone           VARCHAR(10)                 NOT NULL,
    phone_verified  BOOLEAN                     NOT NULL,
    nickname        VARCHAR(50),
    full_name       VARCHAR(100),
    bio             VARCHAR(100),
    gender          BOOLEAN                     NOT NULL,
    dob             date,
    level           SMALLINT,
    avatar_url      VARCHAR(255),
    address         VARCHAR(50),
    detail_address  VARCHAR(100),
    employ_status   VARCHAR(30),
    work_address    VARCHAR(255),
    education_level VARCHAR(30),
    sid             VARCHAR(50),
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by      UUID,
    CONSTRAINT pk_volunteers PRIMARY KEY (id)
);

ALTER TABLE volunteers
    ADD CONSTRAINT uc_volunteers_cid UNIQUE (cid);

ALTER TABLE volunteers
    ADD CONSTRAINT uc_volunteers_email UNIQUE (email);

ALTER TABLE volunteers
    ADD CONSTRAINT uc_volunteers_nickname UNIQUE (nickname);

ALTER TABLE volunteers
    ADD CONSTRAINT uc_volunteers_phone UNIQUE (phone);

ALTER TABLE volunteers
    ADD CONSTRAINT uc_volunteers_vid UNIQUE (vid);

ALTER TABLE volunteers
    ADD CONSTRAINT FK_VOLUNTEERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES sys_admins (id);