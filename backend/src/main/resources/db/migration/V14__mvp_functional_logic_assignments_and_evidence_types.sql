ALTER TABLE evidence
    ADD COLUMN IF NOT EXISTS evidence_type VARCHAR(50) NOT NULL DEFAULT 'GENERAL_ATTACHMENT';

UPDATE evidence
SET evidence_type = CASE
    WHEN reference_type = 'INCIDENT' THEN 'INCIDENT_REPORT_PHOTO'
    ELSE 'GENERAL_ATTACHMENT'
END
WHERE evidence_type IS NULL OR evidence_type = 'GENERAL_ATTACHMENT';

UPDATE work_order
SET assigned_to = CASE id
    WHEN '11111111-1111-1111-1111-000000000501'::uuid THEN 'tecnico@aguafutura.ai'
    WHEN '11111111-1111-1111-1111-000000000502'::uuid THEN 'tecnico@aguafutura.ai'
    WHEN '11111111-1111-1111-1111-000000000503'::uuid THEN 'cuadrilla-sur@aguafutura.ai'
    ELSE assigned_to
END
WHERE tenant_id = '11111111-1111-1111-1111-111111111111'::uuid;
