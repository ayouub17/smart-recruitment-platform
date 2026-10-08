import { useEffect, useState } from 'react';
import { ArrowRight, Building2, FileText, MapPin, Search, Sparkles, Target } from 'lucide-react';
import { Link } from 'react-router-dom';
import api from '../services/api';

const HomePage = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  useEffect(() => { api.get('/jobs').then((response) => setJobs(response.data)).catch(console.error).finally(() => setLoading(false)); }, []);
  const filteredJobs = jobs.filter((job) => [job.titre, job.entrepriseNom, job.ville].some((value) => value?.toLowerCase().includes(search.toLowerCase())));
  return <section className="home-page"><section className="home-hero"><div><p className="page-eyebrow">Recrutement simplifie</p><h1>Le recrutement intelligent commence avec votre profil.</h1><p>Deposez votre CV et retrouvez les opportunites qui correspondent vraiment a votre parcours.</p><div className="home-actions"><Link to="/register" className="btn btn-primary">Creer mon profil <ArrowRight size={16} /></Link><a className="btn btn-outline" href="#offres">Decouvrir les offres</a></div></div><div className="hero-visual"><span className="hero-icon"><FileText size={36} /></span><div className="hero-score"><strong>98%</strong><span>Profil complet</span></div><div className="hero-line short" /><div className="hero-line" /><div className="hero-line medium" /></div></section><section className="value-section"><h2>Pourquoi choisir RecruitPro ?</h2><div className="value-grid"><article><Sparkles size={22} /><h3>Analyse intelligente</h3><p>Vos competences sont valorisees pour des recommandations pertinentes.</p></article><article><Target size={22} /><h3>Offres pertinentes</h3><p>Retrouvez rapidement les opportunites adaptees a votre profil.</p></article><article><FileText size={22} /><h3>CV centralise</h3><p>Gardez vos documents prets pour postuler en quelques clics.</p></article></div></section><section id="offres" className="home-offers"><div className="home-section-heading"><div><p className="page-eyebrow">Opportunites</p><h2>Dernieres offres</h2></div><div className="home-search"><Search size={17} /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Poste, entreprise, ville" /></div></div>{loading ? <div className="page-loading">Chargement des offres...</div> : filteredJobs.length ? <div className="home-job-grid">{filteredJobs.slice(0, 6).map((job) => <article className="home-job" key={job.id}><span className="job-mark">{job.titre?.charAt(0) || 'O'}</span><div><h3>{job.titre}</h3><p><Building2 size={14} />{job.entrepriseNom || 'Entreprise'}</p><p><MapPin size={14} />{job.ville || 'Localisation non renseignee'}</p></div><Link to={`/jobs/${job.id}`} className="text-link">Voir l'offre</Link></article>)}</div> : <p className="empty-state">Aucune offre ne correspond a votre recherche.</p>}</section></section>;
};

export default HomePage;
