import { Link, useParams } from 'react-router-dom';
import { incidentsApi } from '../api/services';
import { PERMISSIONS } from '../auth/permissions';
import AiSuggestionPanel from '../components/AiSuggestionPanel';
import ErrorState from '../components/ErrorState';
import EvidencePanel from '../components/EvidencePanel';
import LoadingState from '../components/LoadingState';
import PageHeader from '../components/PageHeader';
import ShortId from '../components/ShortId';
import StatusBadge from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import { useRoles } from '../hooks/useRoles';
import { incidentLabel } from '../utils/display';
import { normalizeApiError } from '../utils/errors';
import { formatDate } from '../utils/format';

export default function IncidentDetailPage() {
  const { incidentId } = useParams();
  const incidentQuery = useAsync(() => incidentsApi.get(incidentId), [incidentId]);
  const { can } = useRoles();
  const canUseAi = can(PERMISSIONS.aiUse);
  const incident = incidentQuery.data;

  if (incidentQuery.loading) {
    return <LoadingState message="Cargando detalle de incidencia..." />;
  }

  if (incidentQuery.error) {
    return (
      <div className="page">
        <PageHeader eyebrow="Incidencia" title="Detalle de incidencia" description={`ID ${incidentId}`} />
        <ErrorState {...normalizeApiError(incidentQuery.error, 'No se pudo cargar la incidencia')} />
        <Link className="button secondary" to="/incidents">Volver a incidencias</Link>
      </div>
    );
  }

  return (
    <div className="page">
      <PageHeader eyebrow="Incidencia" title={incident?.title || 'Detalle de incidencia'} description={incidentLabel(incident)} />
      <div className="actions">
        <Link className="button secondary" to="/incidents">Volver a incidencias</Link>
      </div>
      <div className="panel">
        <h2 className="section-title">Ficha de incidencia</h2>
        <dl className="detail-list">
          <div><dt>ID</dt><dd><ShortId value={incidentId} copyable /></dd></div>
          <div><dt>Activo</dt><dd>{incident?.assetName || incident?.assetCode ? [incident.assetCode, incident.assetName].filter(Boolean).join(' / ') : incident?.assetId || '-'}</dd></div>
          <div><dt>Titulo</dt><dd>{incident?.title || '-'}</dd></div>
          <div><dt>Descripcion</dt><dd>{incident?.description || '-'}</dd></div>
          <div><dt>Severidad</dt><dd><StatusBadge value={incident?.severity} /></dd></div>
          <div><dt>Estado</dt><dd><StatusBadge value={incident?.status} /></dd></div>
          <div><dt>Creacion</dt><dd>{formatDate(incident?.createdAt)}</dd></div>
        </dl>
      </div>
      {canUseAi && <AiSuggestionPanel incidentId={incidentId} />}
      <EvidencePanel referenceType="INCIDENT" referenceId={incidentId} referenceLabel={incidentLabel(incident)} />
    </div>
  );
}
