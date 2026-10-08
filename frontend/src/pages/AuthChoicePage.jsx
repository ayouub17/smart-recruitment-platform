import { ChevronRight, UserRound, UsersRound } from 'lucide-react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const AuthChoicePage = () => {
  const { isAuthenticated, user } = useAuth();
  if (isAuthenticated) return <Navigate to={user.role === 'RECRUITER' ? '/dashboard/recruiter' : '/dashboard/candidate'} replace />;

  return <main className="auth-choice-page">
    <section className="auth-choice-card">
      <div className="auth-brand"><span className="brand-logo" aria-hidden="true"><img src="/logo.jpg" alt="" /></span><span>RecruitPro</span></div>
      <p className="page-eyebrow">Bienvenue</p>
      <h1>Comment souhaitez-vous utiliser RecruitPro ?</h1>
      <p className="auth-choice-intro">Choisissez votre profil pour acceder a votre espace personnalise.</p>
      <div className="auth-role-grid">
        <article className="auth-role-card"><span className="auth-role-icon"><UserRound size={29} /></span><h2>Je suis candidat</h2><p>Je souhaite trouver des offres et suivre mes candidatures.</p><Link to="/login?role=CANDIDATE" className="btn btn-primary">Se connecter <ChevronRight size={16} /></Link><Link to="/register?role=CANDIDATE" className="auth-role-secondary">Creer un compte</Link></article>
        <article className="auth-role-card"><span className="auth-role-icon recruiter"><UsersRound size={29} /></span><h2>Je suis recruteur</h2><p>Je souhaite publier des offres et trouver des talents.</p><Link to="/login?role=RECRUITER" className="btn btn-primary">Se connecter <ChevronRight size={16} /></Link><Link to="/register?role=RECRUITER" className="auth-role-secondary">Creer un compte</Link></article>
      </div>
    </section>
  </main>;
};

export default AuthChoicePage;
