CREATE TABLE users
(
    id            UUID        NOT NULL,
    cid           VARCHAR(12) NOT NULL,
    avatar_url    VARCHAR(255),
    full_name     VARCHAR(50),
    verify_photos TEXT,
    address       VARCHAR(50),
    gender        BOOLEAN,
    dob           date,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    role_id       SMALLINT    NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_cid UNIQUE (cid);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);