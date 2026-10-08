import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Building, MapPin, Briefcase, Clock, ArrowLeft, Building2, GraduationCap, BookOpen, Award, FileText, Upload, ChevronDown, ChevronUp, Globe, Mail, ExternalLink } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

const JobDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  
  const [job, setJob] = useState(null);
  const [loading, setLoading] = useState(true);
  const [applying, setApplying] = useState(false);
  const [applicationSuccess, setApplicationSuccess] = useState(false);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);

  // Form state
  const [formData, setFormData] = useState({
    nomComplet: '',
    email: '',
    telephone: '',
    ecole: '',
    diplome: '',
    anneeObtention: '',
    experiencePoste: '',
    experienceEntreprise: '',
    experienceDuree: '',
    experienceDescription: '',
    lettreMotivation: ''
  });

  useEffect(() => {
    const fetchJob = async () => {
      try {
        const response = await api.get(`/jobs/${id}`);
        setJob(response.data);
      } catch (err) {
        console.error(err);
        setError("Erreur lors de la récupération des détails de l'offre.");
      } finally {
        setLoading(false);
      }
    };
    fetchJob();
  }, [id]);

  // Pre-fill user info
  useEffect(() => {
    if (user) {
      setFormData(prev => ({
        ...prev,
        nomComplet: `${user.prenom || ''} ${user.nom || ''}`.trim(),
        email: user.email || ''
      }));
    }
  }, [user]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleApply = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    setApplying(true);
    setError('');

    // Build a rich motivation letter with all form data
    const fullMotivation = [
      `📋 Informations personnelles :`,
      `Nom complet : ${formData.nomComplet}`,
      `Email : ${formData.email}`,
      formData.telephone ? `Téléphone : ${formData.telephone}` : null,
      '',
      formData.ecole || formData.diplome ? `🎓 Formation :` : null,
      formData.ecole ? `École/Université : ${formData.ecole}` : null,
      formData.diplome ? `Diplôme : ${formData.diplome}` : null,
      formData.anneeObtention ? `Année d'obtention : ${formData.anneeObtention}` : null,
      '',
      formData.experiencePoste ? `💼 Expérience professionnelle :` : null,
      formData.experiencePoste ? `Poste : ${formData.experiencePoste}` : null,
      formData.experienceEntreprise ? `Entreprise : ${formData.experienceEntreprise}` : null,
      formData.experienceDuree ? `Durée : ${formData.experienceDuree}` : null,
      formData.experienceDescription ? `Description : ${formData.experienceDescription}` : null,
      '',
      formData.lettreMotivation ? `✉️ Lettre de motivation :` : null,
      formData.lettreMotivation || null,
    ].filter(Boolean).join('\n');

    try {
      await api.post('/applications', {
        jobId: job.id,
        lettreMotivation: fullMotivation
      });
      setApplicationSuccess(true);
    } catch (err) {
      console.error(err);
      const errMsg = err.response?.data;
      setError(typeof errMsg === 'string' ? errMsg : "Erreur lors de la candidature.");
    } finally {
      setApplying(false);
    }
  };

  if (loading) return <div className="page-loading">Chargement de l'offre...</div>;
  if (error && !job) return <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--error-color)' }}>{error}</div>;
  if (!job) return <div className="page-loading">Offre introuvable.</div>;

  const isRecruiter = user?.role === 'RECRUITER';

  const inputStyle = { width: '100%', padding: '0.7rem 0.75rem', borderRadius: '6px', border: '1px solid var(--border-color)', font: 'inherit', fontSize: '0.875rem' };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <Link to={isRecruiter ? '/dashboard/recruiter' : '/dashboard/candidate'} className="back-link">
        <ArrowLeft size={16} /> Retour aux offres
      </Link>

      <div className="card" style={{ marginBottom: '1.5rem' }}>
        <h1 style={{ fontSize: '2rem', color: 'var(--primary-color)', marginBottom: '1rem' }}>{job.titre}</h1>
        
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1.5rem', marginBottom: '2rem', color: 'var(--text-secondary)' }}>
          {job.companyId ? (
            <Link to={`/companies/${job.companyId}`} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#0a45a3', fontWeight: 600 }}>
              <Building size={18} /> {job.entrepriseNom}
            </Link>
          ) : (
            <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Building size={18} /> {job.entrepriseNom}</span>
          )}
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><MapPin size={18} /> {job.ville || 'Non spécifié'}</span>
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Briefcase size={18} /> {job.typeContrat}</span>
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}><Clock size={18} /> Publié le {new Date(job.datePublication).toLocaleDateString()}</span>
        </div>

        <div style={{ marginBottom: '2rem' }}>
          <h3 style={{ marginBottom: '0.5rem' }}>Niveau requis</h3>
          <p>{job.niveauRequis}</p>
        </div>

        <div style={{ marginBottom: '1.5rem' }}>
          <h3 style={{ marginBottom: '0.5rem' }}>Description du poste</h3>
          <div style={{ whiteSpace: 'pre-wrap', lineHeight: '1.7' }}>{job.description}</div>
        </div>

        {job.companyId && (
          <Link to={`/companies/${job.companyId}`} className="btn btn-outline" style={{ marginTop: '0.5rem' }}>
            <Building2 size={16} /> Voir le profil complet de l'entreprise
          </Link>
        )}
      </div>

      {/* Section À propos de l'entreprise */}
      {(job.companyDescription || job.companySecteur || job.companyEmail || job.companySiteWeb) && (
        <div className="card" style={{ marginBottom: '1.5rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.25rem' }}>
            {job.companyLogoUrl ? (
              <img src={`http://localhost:8080/${job.companyLogoUrl}`} alt={job.entrepriseNom}
                style={{ width: 56, height: 56, borderRadius: '10px', objectFit: 'cover', border: '1px solid var(--border-color)' }} />
            ) : (
              <span style={{ display: 'inline-grid', width: 56, height: 56, placeItems: 'center', borderRadius: '10px', background: '#e8f0ff', color: '#0a45a3' }}>
                <Building2 size={28} />
              </span>
            )}
            <div>
              <h3 style={{ margin: 0, fontSize: '1.1rem' }}>À propos de {job.entrepriseNom}</h3>
              {job.companySecteur && <p style={{ margin: '0.2rem 0 0', color: 'var(--text-secondary)', fontSize: '0.85rem' }}>{job.companySecteur}</p>}
            </div>
          </div>

          {job.companyDescription && (
            <p style={{ color: 'var(--text-secondary)', lineHeight: '1.7', whiteSpace: 'pre-wrap', marginBottom: '1rem' }}>{job.companyDescription}</p>
          )}

          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1.25rem' }}>
            {job.companyEmail && (
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                <Mail size={15} /> <a href={`mailto:${job.companyEmail}`} style={{ color: '#0a45a3' }}>{job.companyEmail}</a>
              </span>
            )}
            {job.companySiteWeb && (
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.85rem' }}>
                <Globe size={15} />
                <a href={job.companySiteWeb.startsWith('http') ? job.companySiteWeb : `https://${job.companySiteWeb}`}
                  target="_blank" rel="noopener noreferrer" style={{ color: '#0a45a3', display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
                  Site web <ExternalLink size={12} />
                </a>
              </span>
            )}
          </div>
        </div>
      )}

      {!isRecruiter && (
        <div className="card">
          {applicationSuccess ? (
            <div style={{ padding: '1.5rem', backgroundColor: '#dcfce7', color: '#166534', borderRadius: 'var(--radius-md)', textAlign: 'center' }}>
              <h4 style={{ margin: '0 0 0.5rem 0' }}>Candidature envoyée avec succès ! 🎉</h4>
              <p style={{ margin: 0 }}>Le recruteur a bien reçu votre profil et vos informations.</p>
            </div>
          ) : (
            <>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: showForm ? '1.5rem' : 0 }}>
                <h3>Postuler à cette offre</h3>
                <button type="button" className="btn btn-primary" onClick={() => { if (!isAuthenticated) { navigate('/login'); return; } setShowForm(!showForm); }}>
                  {showForm ? <><ChevronUp size={16} /> Réduire</> : <><FileText size={16} /> Postuler maintenant</>}
                </button>
              </div>

              {showForm && (
                <form onSubmit={handleApply} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                  {error && (
                    <div style={{ backgroundColor: '#fef2f2', color: '#b91c1c', padding: '0.75rem', borderRadius: '6px', fontSize: '0.85rem' }}>
                      {error}
                    </div>
                  )}

                  {/* Section: Infos personnelles */}
                  <fieldset style={{ border: '1px solid var(--border-color)', borderRadius: '8px', padding: '1.25rem' }}>
                    <legend style={{ fontWeight: 650, fontSize: '0.9rem', padding: '0 0.5rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                      📋 Informations personnelles
                    </legend>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.82rem', fontWeight: 600 }}>Nom complet *</label>
                        <input name="nomComplet" value={formData.nomComplet} onChange={handleChange} style={inputStyle} required />
                      </div>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.82rem', fontWeight: 600 }}>Email *</label>
                        <input type="email" name="email" value={formData.email} onChange={handleChange} style={inputStyle} required />
                      </div>
                      <div style={{ gridColumn: '1 / -1' }}>
                        <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.82rem', fontWeight: 600 }}>Téléphone</label>
                        <input name="telephone" value={formData.telephone} onChange={handleChange} placeholder="06 xx xx xx xx" style={inputStyle} />
                      </div>
                    </div>
                  </fieldset>

                  {/* Section: Formation */}
                  <fieldset style={{ border: '1px solid var(--border-color)', borderRadius: '8px', padding: '1.25rem' }}>
                    <legend style={{ fontWeight: 650, fontSize: '0.9rem', padding: '0 0.5rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                      <GraduationCap size={16} /> Formation
                    </legend>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.82rem', fontWeight: 600 }}>École / Université</label>
                        <input name="ecole" value={formData.ecole} onChange={handleChange} placeholder="Ex : Université de Blida" style={inputStyle} />
                      </div>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.82rem', fontWeight: 600 }}>Diplôme</label>
                        <input name="diplome" value={formData.diplome} onChange={handleChange} placeholder="Ex : Master en Informatique" style={inputStyle} />
                      </div>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.82rem', fontWeight: 600 }}>Année d'obtention</label>
                        <input name="anneeObtention" value={formData.anneeObtention} onChange={handleChange} placeholder="Ex : 2024" style={inputStyle} />
                      </div>
                    </div>
                  </fieldset>

                  {/* Section: Expérience */}
                  <fieldset style={{ border: '1px solid var(--border-color)', borderRadius: '8px', padding: '1.25rem' }}>
                    <legend style={{ fontWeight: 650, fontSize: '0.9rem', padding: '0 0.5rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                      <Award size={16} /> Expérience professionnelle
                    </legend>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.82rem', fontWeight: 600 }}>Poste occupé</label>
                        <input name="experiencePoste" value={formData.experiencePoste} onChange={handleChange} placeholder="Ex : Développeur web" style={inputStyle} />
                      </div>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.82rem', fontWeight: 600 }}>Entreprise</label>
                        <input name="experienceEntreprise" value={formData.experienceEntreprise} onChange={handleChange} placeholder="Ex : TechCorp" style={inputStyle} />
                      </div>
                      <div>
                        <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.82rem', fontWeight: 600 }}>Durée</label>
                        <input name="experienceDuree" value={formData.experienceDuree} onChange={handleChange} placeholder="Ex : 2 ans" style={inputStyle} />
                      </div>
                      <div style={{ gridColumn: '1 / -1' }}>
                        <label style={{ display: 'block', marginBottom: '0.35rem', fontSize: '0.82rem', fontWeight: 600 }}>Description</label>
                        <textarea name="experienceDescription" value={formData.experienceDescription} onChange={handleChange} rows="3"
                          placeholder="Décrivez vos missions et responsabilités..."
                          style={{ ...inputStyle, resize: 'vertical' }} />
                      </div>
                    </div>
                  </fieldset>

                  {/* Section: Motivation */}
                  <fieldset style={{ border: '1px solid var(--border-color)', borderRadius: '8px', padding: '1.25rem' }}>
                    <legend style={{ fontWeight: 650, fontSize: '0.9rem', padding: '0 0.5rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                      <BookOpen size={16} /> Lettre de motivation
                    </legend>
                    <textarea name="lettreMotivation" value={formData.lettreMotivation} onChange={handleChange} rows="5"
                      placeholder="Expliquez pourquoi vous êtes le candidat idéal pour ce poste..."
                      style={{ ...inputStyle, resize: 'vertical' }} />
                  </fieldset>

                  <p style={{ fontSize: '0.82rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                    <Upload size={14} /> Votre CV principal sera automatiquement joint à cette candidature. Assurez-vous d'en avoir un dans votre profil.
                  </p>

                  <button type="submit" className="btn btn-primary" style={{ padding: '0.85rem', fontSize: '1rem' }} disabled={applying}>
                    {applying ? 'Envoi en cours...' : 'Envoyer ma candidature'}
                  </button>
                </form>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
};

export default JobDetailPage;
