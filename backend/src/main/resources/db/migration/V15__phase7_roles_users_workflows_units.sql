ALTER TABLE work_order
    ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMP;

ALTER TABLE work_order
    ADD COLUMN IF NOT EXISTS cancel_reason TEXT;

ALTER TABLE incident_record
    ADD COLUMN IF NOT EXISTS reporter_user_id UUID REFERENCES iam_users(id);

ALTER TABLE consumption_record
    ADD COLUMN IF NOT EXISTS original_value NUMERIC(19, 4);

ALTER TABLE consumption_record
    ADD COLUMN IF NOT EXISTS original_unit VARCHAR(50);

ALTER TABLE consumption_record
    ADD COLUMN IF NOT EXISTS normalized_volume_m3 NUMERIC(19, 6);

UPDATE consumption_record
SET original_value = COALESCE(original_value, value),
    original_unit = COALESCE(original_unit, unit),
    normalized_volume_m3 = COALESCE(
        normalized_volume_m3,
        CASE
            WHEN unit = 'LITERS' THEN value / 1000
            WHEN unit = 'GALLONS' THEN value * 0.00378541
            ELSE value
        END
    )
WHERE original_value IS NULL
   OR original_unit IS NULL
   OR normalized_volume_m3 IS NULL;

UPDATE consumption_record
SET value = normalized_volume_m3,
    unit = 'CUBIC_METERS';

INSERT INTO iam_users (id, tenant_id, full_name, email, password_hash, role, enabled)
VALUES
    (
        '11111111-1111-1111-1111-000000000005',
        '11111111-1111-1111-1111-111111111111',
        'Super Admin Demo',
        'superadmin@aguafutura.ai',
        '$2b$10$OMcp1M/sC.JSbH9rC5uaBOOjh0SKQuB.dwa4ZXO9U7jAyZJF301jm',
        'SUPER_ADMIN',
        TRUE
    ),
    (
        '11111111-1111-1111-1111-000000000006',
        '11111111-1111-1111-1111-111111111111',
        'Ciudadano Demo',
        'ciudadano@aguafutura.ai',
        '$2b$10$/9qAx2n7j95aaW7N3D11luhahwR4PFB02shx6Z7W5gNNfVs5rp2MG',
        'CITIZEN',
        TRUE
    )
ON CONFLICT (tenant_id, email) DO UPDATE
SET full_name = EXCLUDED.full_name,
    password_hash = EXCLUDED.password_hash,
    role = EXCLUDED.role,
    enabled = TRUE,
    updated_at = now();

UPDATE incident_record
SET reporter_user_id = '11111111-1111-1111-1111-000000000006'
WHERE id = '11111111-1111-1111-1111-000000000401'
  AND reporter_user_id IS NULL;
