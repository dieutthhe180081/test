ALTER TABLE volunteer_verifications
    ADD rejection_reason VARCHAR(255);

ALTER TABLE volunteer_verifications
    ADD reset_password_flag BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE volunteer_verifications
    ADD status VARCHAR(30);