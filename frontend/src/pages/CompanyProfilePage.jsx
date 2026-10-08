import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Building2, Globe, Mail, MapPin, Phone, Briefcase, ExternalLink } from 'lucide-react';
import api from '../services/api';

const CompanyProfilePage = () => {
  const { id } = useParams();
  const [company, setCompany] = useState(null);
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [companyRes, jobsRes] = await Promise.all([
          api.get(`/companies/${id}`),
          api.get(`/jobs/company/${id}`)
        ]);
        setCompany(companyRes.data);
        setJobs(jobsRes.data.filter(j => j.statut === 'ACTIVE'));
      } catch (err) {
        console.error(err);
        setError("Impossible de charger le profil de l'entreprise.");
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  if (loading) return <div className="page-loading">Chargement du profil...</div>;
  if (error) return <div className="page-loading" style={{ color: 'var(--error-color)' }}>{error}</div>;
  if (!company) return <div className="page-loading">Entreprise introuvable.</div>;

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto' }}>
      <Link to={-1} className="back-link"><ArrowLeft size={16} /> Retour</Link>

      {/* Company Header */}
      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', marginBottom: '1.5rem' }}>
          {company.logoUrl ? (
            <img src={`http://localhost:8080/${company.logoUrl}`} alt={company.nom}
              style={{ width: 80, height: 80, borderRadius: '12px', objectFit: 'cover', border: '1px solid var(--border-color)' }} />
          ) : (
            <span style={{ display: 'inline-grid', width: 80, height: 80, placeItems: 'center', borderRadius: '12px', background: '#e8f0ff', color: '#0a45a3' }}>
              <Building2 size={36} />
            </span>
          )}
          <div>
            <h1 style={{ fontSize: '1.75rem', marginBottom: '0.3rem' }}>{company.nom}</h1>
            {company.secteur && <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{company.secteur}</p>}
          </div>
        </div>

        {company.description && (
          <div style={{ marginBottom: '1.5rem' }}>
            <h3 style={{ marginBottom: '0.5rem', fontSize: '1rem' }}>À propos</h3>
            <p style={{ color: 'var(--text-secondary)', lineHeight: '1.7', whiteSpace: 'pre-wrap' }}>{company.description}</p>
          </div>
        )}

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem' }}>
          {company.emailContact && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
              <Mail size={16} /> <a href={`mailto:${company.emailContact}`} style={{ color: '#0a45a3' }}>{company.emailContact}</a>
            </div>
          )}
          {company.telephone && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
              <Phone size={16} /> {company.telephone}
            </div>
          )}
          {company.siteWeb && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem' }}>
              <Globe size={16} />
              <a href={company.siteWeb.startsWith('http') ? company.siteWeb : `https://${company.siteWeb}`}
                target="_blank" rel="noopener noreferrer" style={{ color: '#0a45a3', display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                Site web <ExternalLink size={12} />
              </a>
            </div>
          )}
        </div>
      </div>

      {/* Active Job Listings */}
      <div className="card">
        <h2 style={{ fontSize: '1.1rem', marginBottom: '1rem' }}>
          <Briefcase size={18} style={{ verticalAlign: 'text-bottom', marginRight: '0.4rem' }} />
          Offres actives ({jobs.length})
        </h2>
        {jobs.length > 0 ? (
          <div style={{ display: 'grid', gap: '0.75rem' }}>
            {jobs.map((job) => (
              <article className="offer-card" key={job.id}>
                <span className="job-mark">{job.titre?.charAt(0) || 'O'}</span>
                <div className="offer-card-main">
                  <h2>{job.titre}</h2>
                  <p><MapPin size={15} />{job.ville || 'Non précisé'} <span>•</span> {job.typeContrat} <span>•</span> {job.niveauRequis}</p>
                </div>
                <Link to={`/jobs/${job.id}`} className="btn btn-outline">Voir l'offre</Link>
              </article>
            ))}
          </div>
        ) : (
          <p className="empty-state">Aucune offre active pour cette entreprise.</p>
        )}
      </div>
    </div>
  );
};

export default CompanyProfilePage;
