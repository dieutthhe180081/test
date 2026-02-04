ALTER TABLE volunteers
    ADD bio VARCHAR(100);

ALTER TABLE volunteers
    ADD phone_verified BOOLEAN;

ALTER TABLE volunteers
    ALTER COLUMN phone_verified SET NOT NULL;