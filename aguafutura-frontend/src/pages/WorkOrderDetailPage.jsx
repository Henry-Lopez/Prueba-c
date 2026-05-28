import { Link, useParams } from 'react-router-dom';
import { workOrdersApi } from '../api/services';
import ErrorState from '../components/ErrorState';
import EvidencePanel from '../components/EvidencePanel';
import LoadingState from '../components/LoadingState';
import PageHeader from '../components/PageHeader';
import ShortId from '../components/ShortId';
import StatusBadge from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import { workOrderLabel } from '../utils/display';
import { normalizeApiError } from '../utils/errors';
import { formatDate } from '../utils/format';

export default function WorkOrderDetailPage() {
  const { workOrderId } = useParams();
  const workOrderQuery = useAsync(() => workOrdersApi.get(workOrderId), [workOrderId]);
  const workOrder = workOrderQuery.data;

  if (workOrderQuery.loading) {
    return <LoadingState message="Cargando detalle de orden..." />;
  }

  if (workOrderQuery.error) {
    return (
      <div className="page">
        <PageHeader eyebrow="Orden de trabajo" title="Detalle de orden" description={`ID ${workOrderId}`} />
        <ErrorState {...normalizeApiError(workOrderQuery.error, 'No se pudo cargar la orden')} />
        <Link className="button secondary" to="/work-orders">Volver a ordenes</Link>
      </div>
    );
  }

  return (
    <div className="page">
      <PageHeader eyebrow="Orden de trabajo" title={workOrderLabel(workOrder)} description={workOrder?.description || `ID ${workOrderId}`} />
      <div className="actions">
        <Link className="button secondary" to="/work-orders">Volver a ordenes</Link>
      </div>
      <div className="panel">
        <h2 className="section-title">Ficha de orden</h2>
        <p className="muted">
          Para cerrar una orden de forma defendible debe existir evidencia final, checklist completado o una justificacion tecnica registrada en la descripcion operativa.
        </p>
        <dl className="detail-list">
          <div><dt>ID</dt><dd><ShortId value={workOrderId} copyable /></dd></div>
          <div><dt>Activo</dt><dd>{workOrder?.assetName || workOrder?.assetCode ? [workOrder.assetCode, workOrder.assetName].filter(Boolean).join(' / ') : workOrder?.assetId || '-'}</dd></div>
          <div><dt>Incidencia</dt><dd>{workOrder?.incidentTitle || workOrder?.incidentId || '-'}</dd></div>
          <div><dt>Descripcion</dt><dd>{workOrder?.description || '-'}</dd></div>
          <div><dt>Estado</dt><dd><StatusBadge value={workOrder?.status} /></dd></div>
          <div><dt>Prioridad</dt><dd><StatusBadge value={workOrder?.priority} /></dd></div>
          <div><dt>Asignado a</dt><dd>{workOrder?.assignedTo || '-'}</dd></div>
          <div><dt>Programada</dt><dd>{formatDate(workOrder?.scheduledAt)}</dd></div>
          <div><dt>Completada</dt><dd>{formatDate(workOrder?.completedAt)}</dd></div>
          <div><dt>Creacion</dt><dd>{formatDate(workOrder?.createdAt)}</dd></div>
          <div><dt>Actualizacion</dt><dd>{formatDate(workOrder?.updatedAt)}</dd></div>
        </dl>
      </div>
      <EvidencePanel referenceType="WORK_ORDER" referenceId={workOrderId} referenceLabel={workOrderLabel(workOrder)} />
    </div>
  );
}
