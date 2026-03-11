ALTER TABLE check_in_places
    DROP CONSTRAINT fk_check_in_places_on_created_by;

ALTER TABLE check_in_places
    DROP CONSTRAINT fk_check_in_places_on_event;

ALTER TABLE events
    ADD check_in_accuracy_meters DOUBLE PRECISION;

ALTER TABLE events
    ADD check_in_location GEOGRAPHY(Point, 4326);

UPDATE events
SET
    check_in_accuracy_meters = 100,
    check_in_location = ST_SetSRID(ST_MakePoint(0,0),4326);

ALTER TABLE events
    ALTER COLUMN check_in_accuracy_meters SET NOT NULL;

ALTER TABLE events
    ALTER COLUMN check_in_location SET NOT NULL;

DROP TABLE check_in_places CASCADE;