import { useEffect, useState } from 'react';
import { Building2, FileText, Filter, MapPin, Search, Sparkles, ClipboardList, CalendarDays, Briefcase } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

const STOP_WORDS = new Set([
  'avec', 'dans', 'pour', 'plus', 'nous', 'vous', 'votre', 'notre', 'des', 'les', 'une', 'un', 'est', 'sur', 'par', 'aux',
  'poste', 'offre', 'emploi', 'entreprise', 'equipe', 'gestion', 'suivi', 'service', 'services', 'missions', 'travail',
  'experience', 'niveau', 'projet', 'projets', 'candidat', 'candidate', 'responsable', 'junior', 'confirme', 'debutant',
  'cdi', 'cdd', 'stage', 'maroc', 'casablanca', 'rabat', 'marrakech'
]);

const TECHNICAL_TERMS = new Set([
  'java', 'javascript', 'typescript', 'react', 'angular', 'vue', 'spring', 'springboot', 'node', 'nodejs', 'python',
  'sql', 'postgresql', 'mysql', 'mongodb', 'docker', 'kubernetes', 'aws', 'azure', 'devops', 'frontend', 'backend',
  'fullstack', 'informatique', 'logiciel', 'api', 'cloud', 'linux', 'git', 'powerbi', 'machine', 'learning'
]);

const tokenize = (value = '') => value
  .toLowerCase()
  .normalize('NFD')
  .replace(/[\u0300-\u036f]/g, '')
  .split(/[^a-z0-9+#.]+/)
  .filter((term) => term.length >= 3 && !STOP_WORDS.has(term));

const CandidateDashboard = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [jobs, setJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [visibleJobs, setVisibleJobs] = useState([]);
  const [search, setSearch] = useState('');
  const [filterMessage, setFilterMessage] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const jobsResponse = await api.get('/jobs');
        setJobs(jobsResponse.data);
        setVisibleJobs(jobsResponse.data);
      } catch (error) {
        console.error('Erreur lors du chargement des offres:', error);
      }
      
      try {
        const profileResponse = await api.get('/candidates/profile');
        setProfile(profileResponse.data);
      } catch (error) {
        console.error('Erreur lors du chargement du profil:', error);
      }

      try {
        const appsResponse = await api.get('/applications/my-applications');
        setApplications(appsResponse.data || []);
      } catch (error) {
        console.error('Erreur lors du chargement des candidatures:', error);
      }
      
      setLoading(false);
    };
    
    fetchDashboardData();
  }, []);

  if (loading) return <div className="page-loading">Chargement de votre espace...</div>;

  const displayJobs = visibleJobs.filter((job) => [job.titre, job.entrepriseNom, job.ville].some((value) => value?.toLowerCase().includes(search.toLowerCase())));

  const filterWithCv = () => {
    if (!profile?.cvs?.length) { setFilterMessage('Importez votre CV pour obtenir des offres personnalisees.'); return; }
    const cvData = profile.cvs.map((cv) => cv.extractedData || '').join(' ');
    const profileData = [cvData, profile.bio, ...(profile.skills || []).map((skill) => skill.nom), ...(profile.experiences || []).map((experience) => `${experience.titre} ${experience.description || ''}`)].join(' ').toLowerCase();
    const relevant = jobs.filter((job) => job.niveauRequis === profile.niveauExperience || job.description?.toLowerCase().split(/[^a-zà-ÿ0-9+#.]+/).some((term) => term.length > 3 && profileData.includes(term)));
    const profileTerms = new Set(tokenize(profileData));
    const relevantJobs = jobs.filter((job) => {
      const jobText = [job.titre, job.description, job.companySecteur].filter(Boolean).join(' ');
      const jobTerms = new Set(tokenize(jobText));
      const matchedTerms = [...jobTerms].filter((term) => profileTerms.has(term));
      const technicalMatches = matchedTerms.filter((term) => TECHNICAL_TERMS.has(term));

      // Le niveau seul ne rend pas une offre pertinente : il faut une correspondance de compétences.
      return matchedTerms.length >= 2 || technicalMatches.length >= 1;
    });

    setVisibleJobs(relevantJobs);
    setFilterMessage(relevantJobs.length ? `${relevantJobs.length} offre(s) correspondent réellement à vos compétences.` : 'Aucune offre ne correspond suffisamment à votre CV.');
  };

  return (
    <section className="dashboard-page">
      <header className="dashboard-heading">
        <div>
          <p className="page-eyebrow">Espace candidat</p>
          <h1>Bonjour, {user.prenom}</h1>
          <p>Consultez toutes les offres, puis filtrez-les selon l’analyse de votre CV.</p>
        </div>
        <Link to="/profile#cv" className="btn btn-outline">
          <FileText size={16} />Mon profil et mon CV
        </Link>
      </header>

      <div className="dashboard-stats">
        <article className="dashboard-stat">
          <span className="stat-icon blue"><Briefcase size={21} /></span>
          <div>
            <strong>{jobs.length}</strong>
            <span>Offres disponibles</span>
          </div>
        </article>
        <article className="dashboard-stat">
          <span className="stat-icon green"><ClipboardList size={21} /></span>
          <div>
            <strong>{applications.length}</strong>
            <span>Candidatures envoyées</span>
          </div>
        </article>
        <article className="dashboard-stat">
          <span className="stat-icon amber"><CalendarDays size={21} /></span>
          <div>
            <strong>{applications.filter((app) => app.statut === 'ENTRETIEN_PROGRAMME').length}</strong>
            <span>Entretiens prévus</span>
          </div>
        </article>
      </div>

      <section className="offers-toolbar">
        <div className="home-search">
          <Search size={17} />
          <input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Poste, entreprise, ville" />
        </div>
        <button type="button" className="btn btn-primary" onClick={filterWithCv}>
          <Filter size={16} />Filtrer avec mon CV
        </button>
        <button type="button" className="text-link" onClick={() => { setVisibleJobs(jobs); setFilterMessage(''); }}>
          Toutes les offres
        </button>
      </section>

      {filterMessage && (
        <div className="profile-notice">
          <Sparkles size={16} />
          {filterMessage}
        </div>
      )}

      <section className="offers-list">
        {displayJobs.length ? displayJobs.map((job) => (
          <article className="offer-card" key={job.id}>
            {job.companyLogoUrl ? (
              <img src={`http://localhost:8080/${job.companyLogoUrl}`} alt={job.entrepriseNom} className="job-mark" style={{ objectFit: 'cover' }} />
            ) : (
              <span className="job-mark">{job.titre?.charAt(0) || 'O'}</span>
            )}
            <div className="offer-card-main">
              <h2>{job.titre}</h2>
              <p>
                <Building2 size={15} />{job.entrepriseNom || 'Entreprise'} <span>•</span> <MapPin size={15} />{job.ville || 'Localisation non renseignee'}
              </p>
              <p className="offer-description">{job.description}</p>
            </div>
            <Link to={`/jobs/${job.id}`} className="btn btn-outline">Voir l’offre</Link>
          </article>
        )) : (
          <p className="empty-state">Aucune offre ne correspond a votre recherche.</p>
        )}
      </section>
    </section>
  );
};

export default CandidateDashboard;
