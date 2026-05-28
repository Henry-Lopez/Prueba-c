import { Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';
import { ROLES } from '../auth/permissions';

export default function ForbiddenPage() {
  const { roles } = useAuth();
  const isTechnician = roles?.includes(ROLES.TECHNICIAN);
  const target = isTechnician ? '/work-orders' : '/';
  const label = isTechnician ? 'Volver a ordenes' : 'Volver al dashboard';

  return (
    <div className="auth-shell">
      <div className="auth-card">
        <h1>403</h1>
        <p>No tienes permisos para acceder a este recurso.</p>
        <div className="actions">
          <Link className="button" to={target}>{label}</Link>
        </div>
      </div>
    </div>
  );
}
