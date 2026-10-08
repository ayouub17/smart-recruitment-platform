import { useEffect, useState, useRef } from 'react';
import { Briefcase, Building2, Globe, Mail, MapPin, Phone, Save, Upload, UserRound } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';

const RecruiterProfilePage = () => {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const fileInputRef = useRef(null);
  const [uploadingLogo, setUploadingLogo] = useState(false);

  const fetchProfile = async () => {
    try {
      const response = await api.get('/recruiters/profile');
      setProfile(response.data);
    } catch (err) {
      console.error(err);
      setError("Impossible de charger le profil.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const handleLogoUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      alert("Veuillez sélectionner une image valide.");
      return;
    }

    const formData = new FormData();
    formData.append('file', file);
    setUploadingLogo(true);
    
    try {
      await api.post('/companies/logo/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      await fetchProfile(); // Recharge le profil pour avoir le nouveau logo
    } catch (err) {
      console.error(err);
      alert(err.response?.data || "Erreur lors de l'upload du logo.");
    } finally {
      setUploadingLogo(false);
    }
  };

  if (loading) return <div className="page-loading">Chargement du profil...</div>;
  if (error && !profile) return <div className="page-loading" style={{ color: 'var(--error-color)' }}>{error}</div>;

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto' }}>
      <header className="dashboard-heading">
        <div>
          <p className="page-eyebrow">Paramètres du compte</p>
          <h1>Mon Profil Recruteur</h1>
          <p>Gérez vos informations personnelles et celles de votre entreprise.</p>
        </div>
      </header>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '1.5rem' }}>
        {/* Informations Personnelles */}
        <section className="card">
          <h2 style={{ fontSize: '1.25rem', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <UserRound size={20} /> Informations Personnelles
          </h2>
          
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.5rem' }}>
            <div>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.2rem' }}>Nom complet</p>
              <p style={{ fontWeight: 500 }}>{profile.prenom} {profile.nom}</p>
            </div>
            <div>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.2rem' }}>Email</p>
              <p style={{ fontWeight: 500 }}>{profile.email}</p>
            </div>
            <div>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.2rem' }}>Téléphone</p>
              <p style={{ fontWeight: 500 }}>{profile.telephone || 'Non renseigné'}</p>
            </div>
            <div>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.2rem' }}>Poste</p>
              <p style={{ fontWeight: 500 }}>{profile.poste || 'Non renseigné'}</p>
            </div>
            <div>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.2rem' }}>Département</p>
              <p style={{ fontWeight: 500 }}>{profile.departement || 'Non renseigné'}</p>
            </div>
          </div>
        </section>

        {/* Profil de l'entreprise */}
        <section className="card">
          <h2 style={{ fontSize: '1.25rem', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Building2 size={20} /> Profil de l'Entreprise
          </h2>

          <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', marginBottom: '2rem', flexWrap: 'wrap' }}>
            <div style={{ position: 'relative' }}>
              {profile.companyLogoUrl ? (
                <img src={`http://localhost:8080/${profile.companyLogoUrl}`} alt={profile.companyNom}
                  style={{ width: 100, height: 100, borderRadius: '12px', objectFit: 'cover', border: '1px solid var(--border-color)' }} />
              ) : (
                <span style={{ display: 'inline-grid', width: 100, height: 100, placeItems: 'center', borderRadius: '12px', background: '#e8f0ff', color: '#0a45a3' }}>
                  <Building2 size={40} />
                </span>
              )}
              <input type="file" accept="image/*" ref={fileInputRef} onChange={handleLogoUpload} style={{ display: 'none' }} />
              <button 
                type="button" 
                onClick={() => fileInputRef.current?.click()}
                disabled={uploadingLogo}
                style={{ 
                  position: 'absolute', bottom: -10, right: -10, 
                  background: 'white', border: '1px solid var(--border-color)', 
                  borderRadius: '50%', padding: '0.4rem', cursor: 'pointer',
                  boxShadow: '0 2px 5px rgba(0,0,0,0.1)'
                }}
                title="Modifier le logo"
              >
                <Upload size={16} color="var(--primary-color)" />
              </button>
            </div>
            <div>
              <h3 style={{ fontSize: '1.5rem', margin: '0 0 0.2rem 0' }}>{profile.companyNom || 'Mon Entreprise'}</h3>
              <p style={{ color: 'var(--text-secondary)', margin: 0 }}>{profile.companySecteur || 'Secteur non renseigné'}</p>
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.5rem' }}>
            <div style={{ gridColumn: '1 / -1' }}>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.2rem' }}>Description</p>
              <p style={{ fontWeight: 500, whiteSpace: 'pre-wrap', lineHeight: '1.6' }}>{profile.companyDescription || 'Aucune description disponible.'}</p>
            </div>
            <div>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.2rem' }}>Email de contact</p>
              <p style={{ fontWeight: 500 }}>{profile.companyEmail || 'Non renseigné'}</p>
            </div>
            <div>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.2rem' }}>Site web</p>
              <p style={{ fontWeight: 500 }}>
                {profile.companySiteWeb ? (
                  <a href={profile.companySiteWeb.startsWith('http') ? profile.companySiteWeb : `https://${profile.companySiteWeb}`} 
                     target="_blank" rel="noopener noreferrer" style={{ color: 'var(--primary-color)' }}>
                    {profile.companySiteWeb}
                  </a>
                ) : 'Non renseigné'}
              </p>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
};

export default RecruiterProfilePage;
