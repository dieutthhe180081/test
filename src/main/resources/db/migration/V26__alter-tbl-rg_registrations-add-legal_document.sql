ALTER TABLE org_registrations
    ADD legal_document VARCHAR(255);

UPDATE org_registrations
SET legal_document = ''
WHERE legal_document IS NULL;

ALTER TABLE org_registrations
    ALTER COLUMN legal_document SET NOT NULL;