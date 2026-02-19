CREATE SEQUENCE IF NOT EXISTS activity_domains_id_seq;
ALTER TABLE activity_domains
    ALTER COLUMN id SET NOT NULL;
ALTER TABLE activity_domains
    ALTER COLUMN id SET DEFAULT nextval('activity_domains_id_seq');

ALTER SEQUENCE activity_domains_id_seq OWNED BY activity_domains.id;

CREATE SEQUENCE IF NOT EXISTS activity_sub_domains_id_seq;
ALTER TABLE activity_sub_domains
    ALTER COLUMN id SET NOT NULL;
ALTER TABLE activity_sub_domains
    ALTER COLUMN id SET DEFAULT nextval('activity_sub_domains_id_seq');

ALTER SEQUENCE activity_sub_domains_id_seq OWNED BY activity_sub_domains.id;