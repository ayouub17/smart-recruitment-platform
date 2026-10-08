import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { Camera, FileText, MapPin, Mail, Phone, Save, Upload, UserRound } from 'lucide-react';
import api from '../services/api';

const CandidateProfilePage = () => {
  const fileInputRef = useRef(null);
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState({ adresse: '', bio: '', niveauExperience: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [uploadingPhoto, setUploadingPhoto] = useState(false);
  const [message, setMessage] = useState('');
  const location = useLocation();

  useEffect(() => {
    if (!loading && location.hash === '#cv') {
      const element = document.getElementById('cv');
      if (element) {
        element.scrollIntoView({ behavior: 'smooth' });
      }
    }
  }, [loading, location.hash]);

  const loadProfile = async () => {
    const response = await api.get('/candidates/profile');
    setProfile(response.data);
    setForm({ adresse: response.data.adresse || '', bio: response.data.bio || '', niveauExperience: response.data.niveauExperience || '' });
  };

  useEffect(() => { loadProfile().catch(() => setMessage('Impossible de charger votre profil.')).finally(() => setLoading(false)); }, []);

  const handleSave = async (event) => {
    event.preventDefault(); setSaving(true); setMessage('');
    try {
      const params = new URLSearchParams();
      if (form.adresse) params.set('adresse', form.adresse);
      if (form.bio) params.set('bio', form.bio);
      if (form.niveauExperience) params.set('niveauExperience', form.niveauExperience);
      const response = await api.put(`/candidates/profile?${params.toString()}`);
      setProfile(response.data); setMessage('Profil mis a jour.');
    } catch { setMessage('La mise a jour du profil a echoue.'); } finally { setSaving(false); }
  };

  const handleUpload = async (event) => {
    const file = event.target.files?.[0]; if (!file) return;
    setUploading(true); setMessage(''); const formData = new FormData(); formData.append('file', file);
    try { await api.post('/candidates/cv/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } }); await loadProfile(); setMessage('CV importe avec succes.'); }
    catch { setMessage("L'importation du CV a echoue."); }
    finally { setUploading(false); event.target.value = ''; }
  };

  const handlePhotoUpload = async (event) => {
    const file = event.target.files?.[0]; if (!file) return;
    setUploadingPhoto(true); setMessage(''); const formData = new FormData(); formData.append('file', file);
    try { const response = await api.post('/candidates/photo/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } }); setProfile(response.data); setMessage('Photo de profil mise à jour.'); }
    catch { setMessage("L'importation de l'image a échoué."); }
    finally { setUploadingPhoto(false); event.target.value = ''; }
  };

  if (loading) return <div className="page-loading">Chargement du profil...</div>;
  return <section className="profile-page">
    <div className="profile-page-heading"><div><p className="page-eyebrow">Espace candidat</p><h1>Mon profil</h1><p>Gardez vos informations et votre CV a jour.</p></div></div>
    {message && <div className="profile-notice" role="status">{message}</div>}
    <div className="profile-layout">
      <aside className="profile-summary"><div className="profile-photo-wrap">{profile?.photo ? <img className="profile-large-avatar profile-image" src={`http://localhost:8080/${profile.photo}`} alt="Photo de profil" /> : <span className="profile-large-avatar"><UserRound size={42} /></span>}<label className="photo-upload" title="Importer une photo"><Camera size={15} /><input type="file" accept="image/*" onChange={handlePhotoUpload} disabled={uploadingPhoto} hidden /></label></div><h2>{profile?.prenom} {profile?.nom}</h2><p className="profile-contact"><Mail size={15} />{profile?.email}</p><p className="profile-contact"><Phone size={15} />{profile?.telephone || 'Telephone non renseigne'}</p><p className="profile-contact"><MapPin size={15} />{profile?.adresse || 'Adresse non renseignee'}</p></aside>
      <form className="profile-form" onSubmit={handleSave}>
        <div className="section-heading"><h2>Informations personnelles</h2></div>
        <label>Adresse<input value={form.adresse} onChange={(e) => setForm({ ...form, adresse: e.target.value })} placeholder="Ville, pays" /></label>
        <label>Niveau d'experience<select value={form.niveauExperience} onChange={(e) => setForm({ ...form, niveauExperience: e.target.value })}><option value="">Non renseigne</option><option value="DEBUTANT">Debutant</option><option value="INTERMEDIAIRE">Intermediaire</option><option value="CONFIRME">Confirme</option><option value="EXPERT">Expert</option></select></label>
        <label className="profile-form-full">A propos<textarea value={form.bio} onChange={(e) => setForm({ ...form, bio: e.target.value })} placeholder="Presentez votre parcours et vos objectifs." rows="5" /></label>
        <div className="profile-form-actions"><button className="btn btn-primary" disabled={saving}><Save size={16} />{saving ? 'Enregistrement...' : 'Enregistrer'}</button></div>
      </form>
    </div>
    <section className="cv-section" id="cv"><div><p className="page-eyebrow">Documents</p><h2>Mon CV</h2><p>Formats acceptes : PDF, DOC et DOCX.</p></div><div className="cv-upload-area"><FileText size={27} /><strong>{uploading ? 'Importation en cours...' : 'Ajouter un CV'}</strong><span>Choisissez le fichier a utiliser pour vos candidatures.</span><button type="button" className="btn btn-outline" onClick={() => fileInputRef.current?.click()} disabled={uploading}><Upload size={16} />Importer un fichier</button><input ref={fileInputRef} type="file" accept=".pdf,.doc,.docx" onChange={handleUpload} hidden /></div><div className="cv-list">{profile?.cvs?.length ? profile.cvs.map((cv) => <div className="cv-row" key={cv.id}><FileText size={18} /><span>{cv.nomFichier}</span>{cv.estPrincipal && <span className="cv-primary">CV principal</span>}</div>) : <p className="empty-state">Aucun CV importe pour le moment.</p>}</div></section>
  </section>;
};

export default CandidateProfilePage;
