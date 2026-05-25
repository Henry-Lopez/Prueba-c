ALTER TABLE territorial_zone
    ADD COLUMN description VARCHAR(500),
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT NOW();

UPDATE territorial_zone
SET updated_at = created_at
WHERE updated_at IS NULL;
