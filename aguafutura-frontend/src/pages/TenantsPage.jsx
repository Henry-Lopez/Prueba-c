import { useState } from 'react';
import { tenantsApi, usersApi } from '../api/services';
import ErrorState from '../components/ErrorState';
import FormField from '../components/FormField';
import PageHeader from '../components/PageHeader';
import StatusBadge from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import { normalizeApiError } from '../utils/errors';
import { asArray } from '../utils/format';

export default function TenantsPage() {
  const tenants = useAsync(() => tenantsApi.list(), []);
  const [tenantForm, setTenantForm] = useState({ code: '', name: '' });
  const [adminForm, setAdminForm] = useState({ tenantId: '', fullName: '', email: '', password: 'Admin123!', role: 'ADMIN' });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [created, setCreated] = useState('');
  const rows = asArray(tenants.data);

  async function createTenant(event) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const response = await tenantsApi.create(tenantForm);
      setTenantForm({ code: '', name: '' });
      setAdminForm((current) => ({ ...current, tenantId: response.data?.id || current.tenantId }));
      setCreated(`Tenant creado: ${response.data?.name || response.data?.code}`);
      await tenants.reload();
    } catch (err) {
      setError(normalizeApiError(err, 'No se pudo crear tenant'));
    } finally {
      setSaving(false);
    }
  }

  async function createAdmin(event) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const response = await usersApi.create(adminForm);
      setCreated(`ADMIN creado: ${response.data?.email} / clave temporal ${adminForm.password}`);
      setAdminForm({ tenantId: adminForm.tenantId, fullName: '', email: '', password: 'Admin123!', role: 'ADMIN' });
    } catch (err) {
      setError(normalizeApiError(err, 'No se pudo crear admin'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="page">
      <PageHeader eyebrow="Plataforma" title="Tenants" description="Administracion global restringida a SUPER_ADMIN." />
      {error && <ErrorState {...error} />}
      {created && <div className="panel success-panel">{created}</div>}

      <div className="grid two-columns">
        <form className="panel" onSubmit={createTenant}>
          <h2 className="section-title">Crear tenant</h2>
          <FormField label="Codigo">
            <input value={tenantForm.code} onChange={(event) => setTenantForm({ ...tenantForm, code: event.target.value })} placeholder="MUNICIPIO_NUEVO" required />
          </FormField>
          <FormField label="Nombre">
            <input value={tenantForm.name} onChange={(event) => setTenantForm({ ...tenantForm, name: event.target.value })} placeholder="Municipio Nuevo" required />
          </FormField>
          <div className="actions"><button className="button" disabled={saving}>Crear tenant</button></div>
        </form>

        <form className="panel" onSubmit={createAdmin}>
          <h2 className="section-title">Crear ADMIN de tenant</h2>
          <FormField label="Tenant">
            <select value={adminForm.tenantId} onChange={(event) => setAdminForm({ ...adminForm, tenantId: event.target.value })} required>
              <option value="">Seleccionar tenant</option>
              {rows.map((tenant) => <option key={tenant.id} value={tenant.id}>{tenant.name} ({tenant.code})</option>)}
            </select>
          </FormField>
          <FormField label="Nombre">
            <input value={adminForm.fullName} onChange={(event) => setAdminForm({ ...adminForm, fullName: event.target.value })} required />
          </FormField>
          <FormField label="Correo">
            <input type="email" value={adminForm.email} onChange={(event) => setAdminForm({ ...adminForm, email: event.target.value })} required />
          </FormField>
          <FormField label="Clave temporal">
            <input value={adminForm.password} onChange={(event) => setAdminForm({ ...adminForm, password: event.target.value })} required />
          </FormField>
          <div className="actions"><button className="button" disabled={saving}>Crear ADMIN</button></div>
        </form>
      </div>

      <div className="panel">
        <h2 className="section-title">Tenants registrados</h2>
        <div className="breakdown-list">
          {rows.map((tenant) => (
            <div className="breakdown-row" key={tenant.id}>
              <strong>{tenant.name}</strong>
              <span>{tenant.code}</span>
              <StatusBadge value={tenant.status} />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
