INSERT INTO tenant (id, code, name, status)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'MUNICIPIO_SANTA_CRUZ',
    'Municipio Santa Cruz',
    'ACTIVE'
)
ON CONFLICT (code) DO UPDATE
SET name = EXCLUDED.name,
    status = EXCLUDED.status,
    updated_at = now();

INSERT INTO iam_users (id, tenant_id, full_name, email, password_hash, role, enabled)
VALUES
    (
        '11111111-1111-1111-1111-000000000001',
        '11111111-1111-1111-1111-111111111111',
        'Administrador Demo',
        'admin@aguafutura.ai',
        '$2b$10$c2ALqWspVmVRrf8hBdiA3OVzEvagjH30HV5hswbzLwzJp9PXxRIFq',
        'ADMIN',
        TRUE
    ),
    (
        '11111111-1111-1111-1111-000000000002',
        '11111111-1111-1111-1111-111111111111',
        'Coordinador Demo',
        'coordinador@aguafutura.ai',
        '$2b$10$hT54cNKFNfjTSRNMP9hxpezGgpC2fe8VJReiRoisVtkb84wpTwBOe',
        'COORDINATOR',
        TRUE
    ),
    (
        '11111111-1111-1111-1111-000000000003',
        '11111111-1111-1111-1111-111111111111',
        'Tecnico Demo',
        'tecnico@aguafutura.ai',
        '$2b$10$ToGCwYhGpF7c4dBTB.I1jelNXx0Qik/AXRJps6lU7WrbB8k.JsIPS',
        'TECHNICIAN',
        TRUE
    ),
    (
        '11111111-1111-1111-1111-000000000004',
        '11111111-1111-1111-1111-111111111111',
        'Auditor Demo',
        'auditor@aguafutura.ai',
        '$2b$10$ZrUZ/uthN4Po18is4lVRpebz9M2ZG8.mFefEzsXQSUC5XX0Jm41n6',
        'AUDITOR',
        TRUE
    )
ON CONFLICT (tenant_id, email) DO UPDATE
SET full_name = EXCLUDED.full_name,
    password_hash = EXCLUDED.password_hash,
    role = EXCLUDED.role,
    enabled = TRUE,
    updated_at = now();

INSERT INTO territorial_zone (id, tenant_id, code, name, enabled, description)
SELECT *
FROM (
    VALUES
        (
            '11111111-1111-1111-1111-000000000101'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            'ZN-NORTE',
            'Zona Norte',
            TRUE,
            'Sector norte con mayor demanda residencial.'
        ),
        (
            '11111111-1111-1111-1111-000000000102'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            'ZN-CENTRO',
            'Zona Centro',
            TRUE,
            'Centro operativo y comercial del municipio.'
        ),
        (
            '11111111-1111-1111-1111-000000000103'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            'ZN-SUR',
            'Zona Sur',
            TRUE,
            'Sector sur con infraestructura critica.'
        )
) AS seed(id, tenant_id, code, name, enabled, description)
WHERE NOT EXISTS (
    SELECT 1 FROM territorial_zone existing WHERE existing.id = seed.id
);

INSERT INTO water_asset (id, tenant_id, zone_id, code, name, type, location_description, enabled)
VALUES
    (
        '11111111-1111-1111-1111-000000000201',
        '11111111-1111-1111-1111-111111111111',
        '11111111-1111-1111-1111-000000000101',
        'AST-001',
        'Medidor principal norte',
        'METER',
        'Av. Norte y Calle 4, camara principal',
        TRUE
    ),
    (
        '11111111-1111-1111-1111-000000000202',
        '11111111-1111-1111-1111-111111111111',
        '11111111-1111-1111-1111-000000000102',
        'AST-002',
        'Tanque centro',
        'TANK',
        'Plaza central, modulo de almacenamiento',
        TRUE
    ),
    (
        '11111111-1111-1111-1111-000000000203',
        '11111111-1111-1111-1111-111111111111',
        '11111111-1111-1111-1111-000000000103',
        'AST-003',
        'Bomba sector sur',
        'PUMP',
        'Estacion de bombeo sur, sala tecnica',
        TRUE
    ),
    (
        '11111111-1111-1111-1111-000000000204',
        '11111111-1111-1111-1111-111111111111',
        '11111111-1111-1111-1111-000000000101',
        'AST-004',
        'Valvula de control norte',
        'VALVE',
        'Red secundaria norte, punto V-12',
        TRUE
    )
ON CONFLICT (tenant_id, code) DO UPDATE
SET zone_id = EXCLUDED.zone_id,
    name = EXCLUDED.name,
    type = EXCLUDED.type,
    location_description = EXCLUDED.location_description,
    enabled = EXCLUDED.enabled;

INSERT INTO consumption_record (id, tenant_id, asset_id, reading_date, value, unit)
SELECT *
FROM (
    VALUES
        (
            '11111111-1111-1111-1111-000000000301'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            (SELECT id FROM water_asset WHERE tenant_id = '11111111-1111-1111-1111-111111111111' AND code = 'AST-001'),
            TIMESTAMP '2026-05-20 08:00:00',
            124.5000,
            'CUBIC_METERS'
        ),
        (
            '11111111-1111-1111-1111-000000000302'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            (SELECT id FROM water_asset WHERE tenant_id = '11111111-1111-1111-1111-111111111111' AND code = 'AST-001'),
            TIMESTAMP '2026-05-21 08:00:00',
            128.7500,
            'CUBIC_METERS'
        ),
        (
            '11111111-1111-1111-1111-000000000303'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            (SELECT id FROM water_asset WHERE tenant_id = '11111111-1111-1111-1111-111111111111' AND code = 'AST-002'),
            TIMESTAMP '2026-05-21 09:30:00',
            89.2500,
            'CUBIC_METERS'
        ),
        (
            '11111111-1111-1111-1111-000000000304'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            (SELECT id FROM water_asset WHERE tenant_id = '11111111-1111-1111-1111-111111111111' AND code = 'AST-003'),
            TIMESTAMP '2026-05-22 07:45:00',
            156.0000,
            'CUBIC_METERS'
        ),
        (
            '11111111-1111-1111-1111-000000000305'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            (SELECT id FROM water_asset WHERE tenant_id = '11111111-1111-1111-1111-111111111111' AND code = 'AST-004'),
            TIMESTAMP '2026-05-22 10:15:00',
            42.3000,
            'CUBIC_METERS'
        )
) AS seed(id, tenant_id, asset_id, reading_date, value, unit)
WHERE NOT EXISTS (
    SELECT 1 FROM consumption_record existing WHERE existing.id = seed.id
);

INSERT INTO incident_record (id, tenant_id, asset_id, title, description, severity, status)
SELECT *
FROM (
    VALUES
        (
            '11111111-1111-1111-1111-000000000401'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            (SELECT id FROM water_asset WHERE tenant_id = '11111111-1111-1111-1111-111111111111' AND code = 'AST-001'),
            'Variacion anomala de consumo',
            'El medidor principal norte reporto incremento sostenido frente al promedio diario.',
            'HIGH',
            'OPEN'
        ),
        (
            '11111111-1111-1111-1111-000000000402'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            (SELECT id FROM water_asset WHERE tenant_id = '11111111-1111-1111-1111-111111111111' AND code = 'AST-002'),
            'Revision de nivel en tanque centro',
            'Lectura operativa solicita inspeccion por diferencia entre nivel esperado y consumo registrado.',
            'MEDIUM',
            'IN_PROGRESS'
        ),
        (
            '11111111-1111-1111-1111-000000000403'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            (SELECT id FROM water_asset WHERE tenant_id = '11111111-1111-1111-1111-111111111111' AND code = 'AST-003'),
            'Bomba sur requiere mantenimiento',
            'Ruido intermitente reportado por cuadrilla durante inspeccion preventiva.',
            'CRITICAL',
            'OPEN'
        )
) AS seed(id, tenant_id, asset_id, title, description, severity, status)
WHERE NOT EXISTS (
    SELECT 1 FROM incident_record existing WHERE existing.id = seed.id
);

INSERT INTO work_order (
    id,
    tenant_id,
    asset_id,
    incident_id,
    description,
    status,
    priority,
    assigned_to,
    scheduled_at,
    completed_at
)
SELECT *
FROM (
    VALUES
        (
            '11111111-1111-1111-1111-000000000501'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            (SELECT id FROM water_asset WHERE tenant_id = '11111111-1111-1111-1111-111111111111' AND code = 'AST-001'),
            '11111111-1111-1111-1111-000000000401'::uuid,
            'Inspeccionar camara del medidor principal norte y validar posible fuga.',
            'PENDING',
            'HIGH',
            'tecnico@aguafutura.ai',
            TIMESTAMP '2026-05-26 09:00:00',
            NULL::timestamp
        ),
        (
            '11111111-1111-1111-1111-000000000502'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            (SELECT id FROM water_asset WHERE tenant_id = '11111111-1111-1111-1111-111111111111' AND code = 'AST-002'),
            '11111111-1111-1111-1111-000000000402'::uuid,
            'Verificar sensor de nivel y registrar evidencia fotografica.',
            'IN_PROGRESS',
            'MEDIUM',
            'tecnico@aguafutura.ai',
            TIMESTAMP '2026-05-26 11:30:00',
            NULL::timestamp
        ),
        (
            '11111111-1111-1111-1111-000000000503'::uuid,
            '11111111-1111-1111-1111-111111111111'::uuid,
            (SELECT id FROM water_asset WHERE tenant_id = '11111111-1111-1111-1111-111111111111' AND code = 'AST-003'),
            '11111111-1111-1111-1111-000000000403'::uuid,
            'Programar mantenimiento preventivo de bomba y revisar vibracion.',
            'PENDING',
            'CRITICAL',
            'tecnico@aguafutura.ai',
            TIMESTAMP '2026-05-27 08:30:00',
            NULL::timestamp
        )
) AS seed(
    id,
    tenant_id,
    asset_id,
    incident_id,
    description,
    status,
    priority,
    assigned_to,
    scheduled_at,
    completed_at
)
WHERE NOT EXISTS (
    SELECT 1 FROM work_order existing WHERE existing.id = seed.id
);
