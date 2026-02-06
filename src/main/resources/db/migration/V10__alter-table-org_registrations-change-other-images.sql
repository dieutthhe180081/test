ALTER TABLE org_registrations
    ADD other_evidences VARCHAR(500);

ALTER TABLE org_registrations
    DROP COLUMN other_images;