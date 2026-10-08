import { useState } from 'react';
import { useNavigate, Link, useSearchParams } from 'react-router-dom';
import { UserRound, UsersRound } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const RegisterPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { register } = useAuth();
  const [formData, setFormData] = useState({ nom: '', prenom: '', email: '', password: '', role: searchParams.get('role') === 'RECRUITER' ? 'RECRUITER' : 'CANDIDATE', entrepriseNom: '', poste: '', departement: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const handleChange = (event) => setFormData({ ...formData, [event.target.name]: event.target.value });
  const handleRegister = async (event) => {
    event.preventDefault(); setError(''); setLoading(true);
    try { await register(formData); navigate('/login'); }
    catch (err) { setError(typeof err.response?.data === 'string' ? err.response.data : "L'inscription a echoue. Verifiez les informations saisies."); }
    finally { setLoading(false); }
  };

  return <div className="auth-page register-page"><section className="auth-card register-card"><div className="auth-brand"><span className="brand-logo" aria-hidden="true"><img src="/logo.jpg" alt="" /></span><span>RecruitPro</span></div><h1>Creer un compte</h1><p className="auth-subtitle">Rejoignez la plateforme de recrutement.</p>{error && <div className="auth-error" role="alert">{error}</div>}<form className="auth-form register-form" onSubmit={handleRegister}><div className="form-two-columns"><label>Prenom<input name="prenom" value={formData.prenom} onChange={handleChange} placeholder="Votre prenom" required /></label><label>Nom<input name="nom" value={formData.nom} onChange={handleChange} placeholder="Votre nom" required /></label></div><label>Email<input type="email" name="email" value={formData.email} onChange={handleChange} placeholder="votre@email.com" required /></label><label>Mot de passe<input type="password" name="password" value={formData.password} onChange={handleChange} placeholder="6 caracteres minimum" minLength="6" required /></label><fieldset className="role-picker"><legend>Je suis</legend><div><button type="button" className={formData.role === 'CANDIDATE' ? 'role-option selected' : 'role-option'} onClick={() => setFormData({ ...formData, role: 'CANDIDATE' })}><UserRound size={20} /><span>Candidat<small>Je recherche un emploi</small></span></button><button type="button" className={formData.role === 'RECRUITER' ? 'role-option selected' : 'role-option'} onClick={() => setFormData({ ...formData, role: 'RECRUITER' })}><UsersRound size={20} /><span>Recruteur<small>Je recrute des talents</small></span></button></div></fieldset>{formData.role === 'RECRUITER' && <div className="recruiter-fields"><label>Entreprise<input name="entrepriseNom" value={formData.entrepriseNom} onChange={handleChange} required /></label><div className="form-two-columns"><label>Poste<input name="poste" value={formData.poste} onChange={handleChange} /></label><label>Departement<input name="departement" value={formData.departement} onChange={handleChange} /></label></div></div>}<button type="submit" className="btn btn-primary auth-submit" disabled={loading}>{loading ? 'Creation...' : 'Creer mon compte'}</button></form><p className="auth-switch">Deja un compte ? <Link to="/login">Se connecter</Link></p></section></div>;
};

export default RegisterPage;
