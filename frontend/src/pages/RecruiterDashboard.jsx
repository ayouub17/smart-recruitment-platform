import { useCallback, useEffect, useState } from 'react';
import { Briefcase, Edit, Plus, RefreshCw, Trash2, Users } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

const RecruiterDashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [jobs, setJobs] = useState([]);
  const [applicationCounts, setApplicationCounts] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [deleting, setDeleting] = useState(null);

  const loadJobs = useCallback(async () => {
    setLoading(true); setError('');
    try {
      const response = await api.get('/jobs/recruiter/offers');
      const recruiterJobs = response.data || [];
      setJobs(recruiterJobs);
      const results = await Promise.allSettled(recruiterJobs.map((job) => api.get(`/applications/job/${job.id}`)));
      setApplicationCounts(Object.fromEntries(results.map((result, index) => [recruiterJobs[index].id, result.status === 'fulfilled' ? result.value.data.length : 0])));
    } catch (requestError) {
      setError(requestError.response?.data?.error || 'Impossible de récupérer vos offres. Vérifiez que le serveur backend est démarré puis réessayez.');
    } finally { setLoading(false); }
  }, []);

  const handleDelete = async (jobId, titre) => {
    if (!window.confirm(`Êtes-vous sûr de vouloir supprimer l'offre "${titre}" ? Cette action est irréversible.`)) return;
    setDeleting(jobId);
    try {
      await api.delete(`/jobs/${jobId}`);
      setJobs(prev => prev.filter(j => j.id !== jobId));
    } catch (err) {
      alert(err.response?.data || "Erreur lors de la suppression.");
    } finally { setDeleting(null); }
  };

  useEffect(() => { loadJobs(); }, [loadJobs]);
  if (loading) return <div className="page-loading">Chargement de votre espace...</div>;
  const totalApplications = Object.values(applicationCounts).reduce((total, count) => total + count, 0);

  return (
    <section className="dashboard-page">
      <header className="dashboard-heading">
        <div>
          <p className="page-eyebrow">Espace recruteur</p>
          <h1>Bonjour, {user.prenom}</h1>
          <p>Créez vos offres, publiez-les et suivez les candidatures reçues.</p>
        </div>
        <Link to="/jobs/create" className="btn btn-primary"><Plus size={17} />Publier une offre</Link>
      </header>

      {error && (
        <div className="profile-notice dashboard-error">
          {error}
          <button type="button" className="btn btn-outline compact-btn" onClick={loadJobs}><RefreshCw size={14} />Réessayer</button>
        </div>
      )}

      <div className="dashboard-stats">
        <article className="dashboard-stat">
          <span className="stat-icon blue"><Briefcase size={21} /></span>
          <div><strong>{jobs.length}</strong><span>Offres de mon entreprise</span></div>
        </article>
        <article className="dashboard-stat">
          <span className="stat-icon green"><Users size={21} /></span>
          <div><strong>{totalApplications}</strong><span>Candidatures reçues</span></div>
        </article>
        <article className="dashboard-stat">
          <span className="stat-icon amber"><Briefcase size={21} /></span>
          <div><strong>{jobs.filter((job) => job.statut === 'ACTIVE').length}</strong><span>Offres actives</span></div>
        </article>
      </div>

      <section className="dashboard-panel">
        <div className="panel-heading">
          <div>
            <h2>Offres publiées par mon entreprise</h2>
            <p>Cliquez sur une offre pour la consulter ou suivre ses candidatures.</p>
          </div>
          <button type="button" className="icon-button" onClick={loadJobs} title="Actualiser"><RefreshCw size={17} /></button>
        </div>

        {jobs.length ? (
          <div className="application-list">
            {jobs.map((job) => (
              <article className="application-row" key={job.id}>
                <span className="job-mark">{job.titre?.charAt(0) || 'O'}</span>
                <div className="application-main">
                  <Link to={`/jobs/${job.id}`} className="job-title-link"><h3>{job.titre}</h3></Link>
                  <p>{job.entrepriseNom || 'Votre entreprise'} <span>|</span> {job.ville || 'Localisation non renseignée'} <span>|</span> {applicationCounts[job.id] || 0} candidature(s)</p>
                </div>
                <span className={`application-status ${job.statut === 'ACTIVE' ? 'status-success' : 'status-warning'}`}>
                  {job.statut === 'ACTIVE' ? 'Publiée' : job.statut}
                </span>
                <button className="btn btn-outline compact-btn" onClick={() => navigate(`/jobs/${job.id}/edit`)} title="Modifier">
                  <Edit size={14} />Modifier
                </button>
                <button className="btn btn-outline compact-btn danger-action" onClick={() => handleDelete(job.id, job.titre)} disabled={deleting === job.id} title="Supprimer">
                  <Trash2 size={14} />{deleting === job.id ? '...' : 'Supprimer'}
                </button>
                <Link className="btn btn-primary compact-btn" to={`/jobs/${job.id}/applications`}>Candidatures</Link>
              </article>
            ))}
          </div>
        ) : (
          <div className="empty-offers">
            <p>Aucune offre n'est encore associée à votre entreprise.</p>
            <Link to="/jobs/create" className="btn btn-primary"><Plus size={16} />Publier une offre</Link>
          </div>
        )}
      </section>
    </section>
  );
};

export default RecruiterDashboard;
