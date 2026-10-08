import { ArrowRight, Briefcase, Github, Linkedin, Mail } from 'lucide-react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const LandingPage = () => {
  const { isAuthenticated, user } = useAuth();
  if (isAuthenticated) return <Navigate to={user.role === 'RECRUITER' ? '/dashboard/recruiter' : '/dashboard/candidate'} replace />;

  return <div className="landing-page">
    <section className="landing-hero">
      <div className="landing-copy">
        <p className="landing-kicker"><span /> La plateforme de recrutement nouvelle generation</p>
        <h1>Les bonnes opportunites <em>commencent ici.</em></h1>
        <p className="landing-description">Un espace simple et intelligent pour rapprocher les talents, les recruteurs et les opportunites qui comptent.</p>
        <div className="landing-actions"><Link to="/register" className="landing-primary-action">Commencer maintenant <ArrowRight size={18} /></Link><Link to="/login" className="landing-secondary-action">J'ai deja un compte</Link></div>
        <div className="landing-trust"><Briefcase size={18} /><span>Votre prochain chapitre professionnel vous attend.</span></div>
      </div>
      <div className="landing-image-wrap"><img src="/image.jpg" alt="Des professionnels en discussion" className="landing-image" /><div className="landing-image-shade" /><div className="landing-image-label"><span>RECRUITPRO</span><strong>Votre avenir,<br />en mouvement.</strong></div></div>
    </section>
    <footer className="landing-footer"><span>© 2026 RecruitPro. Tous droits reserves.</span><div className="landing-footer-links"><a href="https://www.linkedin.com/in/ayoub-elhanafi-92873b333/" target="_blank" rel="noreferrer" aria-label="LinkedIn"><Linkedin size={18} /> LinkedIn</a><a href="https://github.com/ayouub17" target="_blank" rel="noreferrer" aria-label="GitHub"><Github size={18} /> GitHub</a><a href="mailto:contact@example.com"><Mail size={18} /> Me contacter</a></div></footer>
  </div>;
};

export default LandingPage;
