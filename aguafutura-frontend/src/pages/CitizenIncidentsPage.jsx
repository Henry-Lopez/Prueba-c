import { useState } from 'react';
import { incidentsApi } from '../api/services';
import DataTable from '../components/DataTable';
import ErrorState from '../components/ErrorState';
import FormField from '../components/FormField';
import PageHeader from '../components/PageHeader';
import ShortId from '../components/ShortId';
import StatusBadge from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import { normalizeApiError } from '../utils/errors';
import { asArray, formatDate } from '../utils/format';

export default function CitizenIncidentsPage() {
  const incidents = useAsync(() => incidentsApi.list(), []);
  const [form, setForm] = useState({ title: 'Reporte ciudadano', description: '', severity: 'MEDIUM' });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  async function submit(event) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      await incidentsApi.create(form);
      setForm({ title: 'Reporte ciudadano', description: '', severity: 'MEDIUM' });
      await incidents.reload();
    } catch (err) {
      setError(normalizeApiError(err, 'No se pudo enviar el reporte'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="page">
      <PageHeader eyebrow="Ciudadano" title="Mis reportes" description="Reporta incidencias y consulta el seguimiento propio." />
      <form className="panel" onSubmit={submit}>
        <h2 className="section-title">Reportar incidencia</h2>
        {error && <ErrorState {...error} />}
        <div className="form-grid">
          <FormField label="Sintoma">
            <select value={form.severity} onChange={(event) => setForm({ ...form, severity: event.target.value })}>
              <option value="LOW">Goteo menor</option>
              <option value="MEDIUM">Fuga visible</option>
              <option value="HIGH">Corte o perdida importante</option>
              <option value="CRITICAL">Riesgo critico</option>
            </select>
          </FormField>
          <FormField label="Ubicacion y descripcion">
            <textarea value={form.description} onChange={(event) => setForm({ ...form, description: event.target.value })} placeholder="Calle, referencia y descripcion del problema" required />
          </FormField>
        </div>
        <div className="actions"><button className="button" disabled={saving}>{saving ? 'Enviando...' : 'Enviar reporte'}</button></div>
      </form>

      <DataTable
        rows={asArray(incidents.data)}
        columns={[
          { key: 'id', header: 'ID', render: (row) => <ShortId value={row.id} />, searchable: false },
          { key: 'description', header: 'Descripcion' },
          { key: 'severity', header: 'Sintoma', render: (row) => <StatusBadge value={row.severity} /> },
          { key: 'status', header: 'Estado', render: (row) => <StatusBadge value={row.status} /> },
          { key: 'createdAt', header: 'Fecha', render: (row) => formatDate(row.createdAt) },
        ]}
      />
    </div>
  );
}
