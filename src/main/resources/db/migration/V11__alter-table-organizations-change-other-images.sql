ALTER TABLE organizations
    ADD other_evidences VARCHAR(500);

ALTER TABLE organizations
    DROP COLUMN other_images;