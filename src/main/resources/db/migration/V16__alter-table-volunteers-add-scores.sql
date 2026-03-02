ALTER TABLE volunteers
    ADD credit_score SMALLINT;

ALTER TABLE volunteers
    ADD honor_score SMALLINT;

ALTER TABLE volunteers
    ALTER COLUMN credit_score SET NOT NULL;

ALTER TABLE volunteers
    ALTER COLUMN honor_score SET NOT NULL;