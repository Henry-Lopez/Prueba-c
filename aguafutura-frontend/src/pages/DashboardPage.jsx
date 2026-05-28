import { analyticsApi } from '../api/services';
import { useAuth } from '../auth/AuthContext';
import EmptyState from '../components/EmptyState';
import ErrorState from '../components/ErrorState';
import LoadingState from '../components/LoadingState';
import PageHeader from '../components/PageHeader';
import StatusBadge from '../components/StatusBadge';
import { useAsync } from '../hooks/useAsync';
import { tenantLabel } from '../utils/display';

const statusLabels = {
  OPEN: 'Abiertas',
  IN_PROGRESS: 'En proceso',
  RESOLVED: 'Resueltas',
  CLOSED: 'Cerradas',
  PENDING: 'Pendientes',
  SCHEDULED: 'Programadas',
  COMPLETED: 'Completadas',
  CANCELLED: 'Canceladas',
  LOW: 'Baja',
  MEDIUM: 'Media',
  HIGH: 'Alta',
  CRITICAL: 'Critica',
};

function labelFor(value) {
  return statusLabels[value] || value;
}

function numberFormat(value, options) {
  return new Intl.NumberFormat('es-BO', options).format(Number(value) || 0);
}

function cubicMeters(value) {
  return `${numberFormat(value, { maximumFractionDigits: 2 })} m3`;
}

function liters(value) {
  return `${numberFormat(value, { maximumFractionDigits: 0 })} L`;
}

function gallons(value) {
  return `${numberFormat(value, { maximumFractionDigits: 0 })} gal`;
}

function sumValues(data) {
  return Object.values(data || {}).reduce((total, value) => total + (Number(value) || 0), 0);
}

function Breakdown({ title, data }) {
  const entries = Object.entries(data || {});
  const max = Math.max(...entries.map(([, value]) => Number(value) || 0), 0);

  return (
    <div className="panel">
      <h2 className="section-title">{title}</h2>
      {entries.length ? (
        <div className="breakdown-list">
          {entries.map(([key, value]) => (
            <div className="breakdown-row" key={key}>
              <strong>{labelFor(key)}</strong>
              <div className="breakdown-bar">
                <span style={{ width: `${max ? ((Number(value) || 0) / max) * 100 : 0}%` }} />
              </div>
              <span className="badge">{value}</span>
            </div>
          ))}
        </div>
      ) : (
        <EmptyState message="El backend aun no devuelve datos para este desglose." />
      )}
    </div>
  );
}

function KpiCard({ label, value, help, tone }) {
  return (
    <div className={`metric-card ${tone ? `metric-${tone}` : ''}`}>
      <span>{label}</span>
      <strong>{value}</strong>
      <small>{help}</small>
    </div>
  );
}

function OperationalAlerts({ data }) {
  const openIncidents = Number(data?.incidentsByStatus?.OPEN || 0);
  const criticalIncidents = Number(data?.incidentsBySeverity?.CRITICAL || 0);
  const highIncidents = Number(data?.incidentsBySeverity?.HIGH || 0);
  const pendingOrders = Number(data?.workOrdersByStatus?.PENDING || 0);
  const inProgressOrders = Number(data?.workOrdersByStatus?.IN_PROGRESS || 0);
  const alerts = [
    {
      label: 'Incidencias abiertas',
      value: openIncidents,
      detail: openIncidents ? 'Requieren seguimiento operativo.' : 'No hay incidencias abiertas registradas.',
      status: openIncidents ? 'HIGH' : 'LOW',
    },
    {
      label: 'Severidad alta o critica',
      value: highIncidents + criticalIncidents,
      detail: criticalIncidents ? `${criticalIncidents} incidencia(s) critica(s).` : 'Sin criticidad maxima activa.',
      status: criticalIncidents ? 'CRITICAL' : highIncidents ? 'HIGH' : 'LOW',
    },
    {
      label: 'Ordenes pendientes',
      value: pendingOrders,
      detail: inProgressOrders ? `${inProgressOrders} orden(es) en proceso.` : 'Sin ordenes en proceso.',
      status: pendingOrders ? 'PENDING' : 'COMPLETED',
    },
  ];

  return (
    <div className="panel">
      <h2 className="section-title">Alertas operativas</h2>
      <div className="priority-list">
        {alerts.map((alert) => (
          <div className="priority-item" key={alert.label}>
            <div>
              <strong>{alert.label}</strong>
              <span>{alert.detail}</span>
            </div>
            <div className="priority-value">
              <b>{alert.value}</b>
              <StatusBadge value={alert.status} />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default function DashboardPage() {
  const { data, loading, error } = useAsync(() => analyticsApi.dashboard(), []);
  const auth = useAuth();
  const tenant = tenantLabel(auth);
  const openIncidents = Number(data?.incidentsByStatus?.OPEN || 0);
  const closedIncidents = Number(data?.incidentsByStatus?.CLOSED || 0) + Number(data?.incidentsByStatus?.RESOLVED || 0);
  const pendingOrders = Number(data?.workOrdersByStatus?.PENDING || 0);
  const activeOrders = Number(data?.workOrdersByStatus?.IN_PROGRESS || 0);
  const completedOrders = Number(data?.workOrdersByStatus?.COMPLETED || 0);
  const scheduledOrders = Number(data?.workOrdersByStatus?.SCHEDULED || 0);
  const readingCount = Number(data?.consumptionReadingCount || 0);
  const monitoredAssets = Number(data?.monitoredAssets || 0);
  const hasMetrics = Boolean(data && (
    Number(data.totalAssets) ||
    Number(data.totalIncidents) ||
    Number(data.totalWorkOrders) ||
    Number(data.totalConsumptionVolume) ||
    Number(data.totalEvidence)
  ));

  if (loading) return <div className="page"><LoadingState message="Cargando centro operativo..." /></div>;

  return (
    <div className="page">
      <PageHeader
        eyebrow="Operaciones"
        badge="Live"
        title="Centro operativo hidrico"
        description={`Resumen ejecutivo conectado al backend real para ${tenant}.`}
      />
      {error && <ErrorState message={error} />}

      <section className="command-hero">
        <div>
          <h2>Vista municipal unificada</h2>
          <p>Infraestructura, consumo, incidencias, ordenes y evidencia consolidadas por tenant.</p>
        </div>
        <dl className="hero-summary">
          <div><dt>Tenant</dt><dd>{tenant}</dd></div>
          <div><dt>Rol activo</dt><dd>{auth.roles?.join(', ') || '-'}</dd></div>
          <div><dt>Estado</dt><dd>Datos demo reales</dd></div>
        </dl>
      </section>

      {!error && !hasMetrics && <EmptyState title="Dashboard sin datos" message="No hay registros demo suficientes para calcular indicadores." />}

      <div className="grid">
        <KpiCard label="Activos hidricos" value={numberFormat(data?.totalAssets)} help="Infraestructura registrada" />
        <KpiCard label="Consumo total" value={cubicMeters(data?.totalConsumptionVolume)} help="Volumen consolidado en metros cubicos" />
        <KpiCard label="Equivalente informativo" value={liters(data?.totalConsumptionLiters)} help="Conversion visual: 1 m3 = 1000 litros" />
        <KpiCard label="Equivalente galones" value={gallons(data?.totalConsumptionGallons)} help="Conversion visual: 1 galon US = 0.00378541 m3" />
        <KpiCard label="Lecturas registradas" value={numberFormat(readingCount)} help={`${monitoredAssets} activo(s) monitoreado(s)`} />
        <KpiCard label="Promedio por lectura" value={cubicMeters(data?.averageConsumptionPerReading)} help="Derivado del consumo total / lecturas" />
        <KpiCard label="Incidencias" value={numberFormat(data?.totalIncidents)} help={`${openIncidents} abiertas / ${closedIncidents} cerradas o resueltas`} tone={openIncidents ? 'warning' : 'success'} />
        <KpiCard label="Ordenes de trabajo" value={numberFormat(data?.totalWorkOrders)} help={`${pendingOrders} pendientes / ${scheduledOrders} programadas / ${activeOrders} en proceso / ${completedOrders} completadas`} tone={pendingOrders ? 'warning' : 'success'} />
        <KpiCard label="Evidencias" value={numberFormat(data?.totalEvidence)} help="Archivos trazables por tenant" />
      </div>

      <div className="grid">
        <Breakdown title="Incidentes por severidad" data={data?.incidentsBySeverity} />
        <Breakdown title="Incidentes por estado" data={data?.incidentsByStatus} />
        <Breakdown title="Ordenes por estado" data={data?.workOrdersByStatus} />
        <Breakdown title="Lecturas por unidad original" data={data?.consumptionReadingsByOriginalUnit} />
        <Breakdown title="Consumo por activo (m3)" data={data?.consumptionByAsset} />
        <Breakdown title="Carga activa de tecnicos" data={data?.technicianActiveWorkload} />
      </div>

      <div className="grid two-columns">
        <OperationalAlerts data={data} />
        <div className="panel">
          <h2 className="section-title">Resumen del tenant demo</h2>
          <dl className="detail-list">
            <div><dt>Tenant</dt><dd>{tenant}</dd></div>
            <div><dt>Activos</dt><dd>{numberFormat(data?.totalAssets)}</dd></div>
            <div><dt>Eventos operativos</dt><dd>{numberFormat(sumValues(data?.incidentsByStatus))}</dd></div>
            <div><dt>Ordenes activas</dt><dd>{numberFormat(data?.activeWorkOrders ?? (pendingOrders + activeOrders))}</dd></div>
            <div><dt>Ordenes cerradas</dt><dd>{numberFormat(data?.completedWorkOrders ?? completedOrders)}</dd></div>
            <div><dt>Incidencias con consumo</dt><dd>{numberFormat(data?.consumptionRelatedIncidents)}</dd></div>
            <div><dt>Unidad de consumo</dt><dd>m3</dd></div>
            <div><dt>Evidencia</dt><dd>{Number(data?.totalEvidence || 0) ? 'Disponible' : 'Sin archivos registrados'}</dd></div>
          </dl>
        </div>
      </div>
    </div>
  );
}
