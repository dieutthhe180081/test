ALTER TABLE organizations
    ADD legal_document VARCHAR(255);

UPDATE organizations
SET legal_document = ''
WHERE legal_document IS NULL;

ALTER TABLE organizations
    ALTER COLUMN legal_document SET NOT NULL;