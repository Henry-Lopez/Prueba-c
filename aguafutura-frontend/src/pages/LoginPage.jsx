import { useEffect, useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { tenantsApi } from '../api/services';
import { useAuth } from '../auth/AuthContext';
import { ROLES } from '../auth/permissions';
import FormField from '../components/FormField';
import ErrorState from '../components/ErrorState';
import { asArray } from '../utils/format';
import { apiErrorMessage } from '../utils/errors';

const demoUsers = [
  { role: 'SUPER_ADMIN', email: 'superadmin@aguafutura.ai', password: 'Super123!' },
  { role: 'ADMIN', email: 'admin@aguafutura.ai', password: 'Admin123!' },
  { role: 'COORDINATOR', email: 'coordinador@aguafutura.ai', password: 'Coord123!' },
  { role: 'TECHNICIAN', email: 'tecnico@aguafutura.ai', password: 'Tec123!' },
  { role: 'AUDITOR', email: 'auditor@aguafutura.ai', password: 'Auditor123!' },
  { role: 'CITIZEN', email: 'ciudadano@aguafutura.ai', password: 'Ciudadano123!' },
];

function defaultRouteForRoles(roles = []) {
  if (roles.includes(ROLES.SUPER_ADMIN)) return '/tenants';
  if (roles.includes(ROLES.CITIZEN)) return '/my-incidents';
  if (roles.includes(ROLES.TECHNICIAN) && !roles.some((role) => [ROLES.ADMIN, ROLES.COORDINATOR, ROLES.AUDITOR].includes(role))) return '/work-orders';
  return '/';
}

export default function LoginPage() {
  const { login, isAuthenticated, roles } = useAuth();
  const [tenants, setTenants] = useState([]);
  const [form, setForm] = useState({
    tenantId: '',
    email: 'admin@aguafutura.ai',
    password: 'Admin123!',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [tenantsError, setTenantsError] = useState('');
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    tenantsApi
      .list()
      .then((response) => {
        const list = asArray(response.data);
        setTenants(list);
        if (list.length) {
          setForm((current) => ({ ...current, tenantId: current.tenantId || list[0].id }));
        }
      })
      .catch((err) => setTenantsError(apiErrorMessage(err, 'No se pudieron cargar tenants')));
  }, []);

  if (isAuthenticated) {
    return <Navigate to={defaultRouteForRoles(roles)} replace />;
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      const profile = await login({ tenantId: form.tenantId, email: form.email, password: form.password });
      const nextRoles = profile?.roles || [];
      navigate(location.state?.from?.pathname || defaultRouteForRoles(nextRoles), { replace: true });
    } catch (err) {
      setError(apiErrorMessage(err, 'Login fallido'));
    } finally {
      setLoading(false);
    }
  }

  function useDemoUser(user) {
    setForm((current) => ({
      ...current,
      email: user.email,
      password: user.password,
    }));
    setError('');
  }

  return (
    <div className="auth-shell">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>AguaFutura AI</h1>
        <p>Consola segura para operacion hidrica, evidencia e inteligencia operacional.</p>

        {tenantsError && <ErrorState message={tenantsError} detail="Puedes ingresar el tenant manualmente mientras el catalogo no este disponible." />}
        {error && <ErrorState message={error} />}

        <div className="form-grid">
          <FormField label="Tenant" help="Se usa solo para login/register; luego el backend resuelve el tenant desde el JWT.">
            {tenants.length ? (
              <select
                value={form.tenantId}
                onChange={(event) => setForm({ ...form, tenantId: event.target.value })}
                required
              >
                <option value="">Seleccionar tenant</option>
                {tenants.map((tenant) => (
                  <option key={tenant.id} value={tenant.id}>
                    {tenant.name} ({tenant.code})
                  </option>
                ))}
              </select>
            ) : (
              <input
                value={form.tenantId}
                onChange={(event) => setForm({ ...form, tenantId: event.target.value })}
                placeholder="tenant uuid"
                required
              />
            )}
          </FormField>

          <FormField label="Correo">
            <input
              type="email"
              value={form.email}
              onChange={(event) => setForm({ ...form, email: event.target.value })}
              required
            />
          </FormField>

          <FormField label="Contrasena">
            <input
              type="password"
              value={form.password}
              onChange={(event) => setForm({ ...form, password: event.target.value })}
              required
            />
          </FormField>
        </div>

        <div className="actions">
          <button className="button" type="submit" disabled={loading}>
            {loading ? 'Procesando...' : 'Ingresar'}
          </button>
        </div>

        <div className="demo-users">
          <strong>Usuarios demo</strong>
          <div className="demo-user-grid">
            {demoUsers.map((user) => (
              <button key={user.role} type="button" className="demo-user" onClick={() => useDemoUser(user)}>
                <span>{user.role}</span>
                <small>{user.email}</small>
              </button>
            ))}
          </div>
          <p>Las credenciales corresponden al tenant demo seleccionado.</p>
        </div>
      </form>
    </div>
  );
}
