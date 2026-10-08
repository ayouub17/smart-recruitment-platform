import { useState } from 'react';
import { useNavigate, Link, useSearchParams } from 'react-router-dom';
import { Lock, Mail } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const LoginPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (event) => {
    event.preventDefault(); setError(''); setLoading(true);
    try {
      const user = await login(email, password);
      navigate(user.role === 'RECRUITER' ? '/dashboard/recruiter' : '/dashboard/candidate');
    } catch { setError('Identifiants incorrects ou serveur injoignable.'); } finally { setLoading(false); }
  };

  const selectedRole = searchParams.get('role') === 'RECRUITER' ? 'Recruteur' : 'Candidat';
  const registerLink = searchParams.get('role') === 'RECRUITER' ? '/register?role=RECRUITER' : '/register?role=CANDIDATE';
  return <div className="auth-page"><section className="auth-card"><div className="auth-brand"><span className="brand-logo" aria-hidden="true"><img src="/logo.jpg" alt="" /></span><span>RecruitPro</span></div><h1>Bienvenue !</h1><p className="auth-subtitle">Connexion espace {selectedRole}.</p>{error && <div className="auth-error" role="alert">{error}</div>}<form className="auth-form" onSubmit={handleLogin}><label>Email<div className="input-with-icon"><Mail size={17} /><input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="votre@email.com" required /></div></label><label>Mot de passe<div className="input-with-icon"><Lock size={17} /><input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder="Votre mot de passe" required /></div></label><button type="submit" className="btn btn-primary auth-submit" disabled={loading}>{loading ? 'Connexion...' : 'Se connecter'}</button></form><p className="auth-switch">Pas encore de compte ? <Link to={registerLink}>Creer un compte</Link></p></section></div>;
};

export default LoginPage;
