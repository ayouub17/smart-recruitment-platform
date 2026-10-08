import { useEffect, useState } from 'react';
import { Briefcase, CalendarDays, Clock3 } from 'lucide-react';
import api from '../services/api';

const CandidateApplicationsPage = () => {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => { api.get('/applications/my-applications').then((response) => setApplications(response.data)).finally(() => setLoading(false)); }, []);
  const label = (status) => status === 'ENTRETIEN_PROGRAMME' ? 'Entretien programmé' : status === 'ACCEPTEE' ? 'Acceptée' : status === 'REFUSEE' ? 'Refusée' : 'En attente';
  const style = (status) => status === 'ENTRETIEN_PROGRAMME' || status === 'ACCEPTEE' ? 'status-success' : status === 'REFUSEE' ? 'status-danger' : 'status-warning';
  if (loading) return <div className="page-loading">Chargement de vos candidatures...</div>;
  return <section className="dashboard-page"><header className="dashboard-heading"><div><p className="page-eyebrow">Espace candidat</p><h1>Mes candidatures</h1><p>Suivez l’avancement de chaque candidature envoyée.</p></div></header><section className="dashboard-panel">{applications.length ? <div className="application-list">{applications.map((application) => <article className="application-row" key={application.id}><span className="job-mark"><Briefcase size={15} /></span><div className="application-main"><h3>{application.jobTitre}</h3><p>{application.entrepriseNom || 'Entreprise'} <span>|</span> envoyée le {new Date(application.datePostulation).toLocaleDateString('fr-FR')}</p>{application.dateEntretien && <p className="interview-confirmation"><CalendarDays size={14} />Entretien prévu le {new Date(application.dateEntretien).toLocaleString('fr-FR')}{application.messageRecruteur ? ` — ${application.messageRecruteur}` : ''}</p>}</div><span className={`application-status ${style(application.statut)}`}><Clock3 size={12} /> {label(application.statut)}</span></article>)}</div> : <p className="empty-state">Vous n’avez pas encore postulé à une offre.</p>}</section></section>;
};
export default CandidateApplicationsPage;
