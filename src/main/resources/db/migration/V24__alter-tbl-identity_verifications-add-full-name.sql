ALTER TABLE identity_verifications
    ADD full_name VARCHAR(100);

UPDATE identity_verifications
SET full_name = 'UNKNOWN'
WHERE full_name IS NULL;

ALTER TABLE identity_verifications
    ALTER COLUMN full_name SET NOT NULL;