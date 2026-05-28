import { useState } from 'react';
import { usersApi } from '../api/services';
import { ROLES } from '../auth/permissions';
import { useAuth } from '../auth/AuthContext';
import DataTable from '../components/DataTable';
import ErrorState from '../components/ErrorState';
import FormField from '../components/FormField';
import PageHeader from '../components/PageHeader';
import StatusBadge from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import { normalizeApiError } from '../utils/errors';
import { asArray } from '../utils/format';

const adminRoles = ['COORDINATOR', 'TECHNICIAN', 'AUDITOR', 'CITIZEN'];

export default function UsersPage() {
  const auth = useAuth();
  const users = useAsync(() => usersApi.list(), []);
  const [form, setForm] = useState({ fullName: '', email: '', password: 'Temporal123!', role: 'COORDINATOR' });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);
  const [created, setCreated] = useState('');
  const allowedRoles = auth.roles.includes(ROLES.ADMIN) ? adminRoles : ['ADMIN'];

  async function submit(event) {
    event.preventDefault();
    setSaving(true);
    setError(null);
    try {
      const response = await usersApi.create(form);
      setCreated(`Usuario creado: ${response.data?.email} / clave temporal ${form.password}`);
      setForm({ fullName: '', email: '', password: 'Temporal123!', role: allowedRoles[0] });
      await users.reload();
    } catch (err) {
      setError(normalizeApiError(err, 'No se pudo crear usuario'));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="page">
      <PageHeader eyebrow="IAM" title="Usuarios" description="Alta controlada de usuarios dentro del tenant." />
      <form className="panel" onSubmit={submit}>
        <h2 className="section-title">Crear usuario</h2>
        {error && <ErrorState {...error} />}
        {created && <p className="success-text">{created}</p>}
        <div className="form-grid">
          <FormField label="Nombre"><input value={form.fullName} onChange={(event) => setForm({ ...form, fullName: event.target.value })} required /></FormField>
          <FormField label="Correo"><input type="email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} required /></FormField>
          <FormField label="Rol">
            <select value={form.role} onChange={(event) => setForm({ ...form, role: event.target.value })}>
              {allowedRoles.map((role) => <option key={role} value={role}>{role}</option>)}
            </select>
          </FormField>
          <FormField label="Clave temporal"><input value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} required /></FormField>
        </div>
        <div className="actions"><button className="button" disabled={saving}>{saving ? 'Creando...' : 'Crear usuario'}</button></div>
      </form>

      <DataTable
        rows={asArray(users.data)}
        columns={[
          { key: 'fullName', header: 'Nombre' },
          { key: 'email', header: 'Correo' },
          { key: 'role', header: 'Rol', render: (row) => <StatusBadge value={row.role} /> },
          { key: 'enabled', header: 'Estado', render: (row) => row.enabled ? 'Activo' : 'Inactivo' },
        ]}
      />
    </div>
  );
}
