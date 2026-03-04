ALTER TABLE volunteers
    ADD credit_score SMALLINT;

UPDATE volunteers
SET credit_score = 0
WHERE credit_score IS NULL;

ALTER TABLE volunteers
    ADD honor_score SMALLINT;

UPDATE volunteers
SET honor_score = 0
WHERE honor_score IS NULL;

ALTER TABLE volunteers
    ALTER COLUMN credit_score SET NOT NULL;

ALTER TABLE volunteers
    ALTER COLUMN honor_score SET NOT NULL;