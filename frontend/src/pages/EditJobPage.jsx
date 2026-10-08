import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { ArrowLeft, Save } from 'lucide-react';
import api from '../services/api';

const EditJobPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    titre: '',
    description: '',
    typeContrat: 'CDI',
    ville: '',
    niveauRequis: 'DEBUTANT'
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchJob = async () => {
      try {
        const response = await api.get(`/jobs/${id}`);
        const job = response.data;
        setFormData({
          titre: job.titre || '',
          description: job.description || '',
          typeContrat: job.typeContrat || 'CDI',
          ville: job.ville || '',
          niveauRequis: job.niveauRequis || 'DEBUTANT'
        });
      } catch (err) {
        console.error(err);
        setError("Impossible de charger l'offre.");
      } finally {
        setLoading(false);
      }
    };
    fetchJob();
  }, [id]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    try {
      await api.put(`/jobs/${id}`, {
        ...formData
      });
      navigate('/dashboard/recruiter');
    } catch (err) {
      console.error(err);
      setError(err.response?.data || "Erreur lors de la modification de l'offre.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="page-loading">Chargement de l'offre...</div>;

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <Link to="/dashboard/recruiter" className="back-link"><ArrowLeft size={16} /> Retour au tableau de bord</Link>
      <h2 style={{ marginBottom: '2rem' }}>Modifier l'offre</h2>

      <div className="card">
        {error && (
          <div style={{ backgroundColor: 'var(--error-color)', color: 'white', padding: '0.75rem', borderRadius: 'var(--radius-md)', marginBottom: '1rem' }}>
            {typeof error === 'string' ? error : "Une erreur s'est produite."}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Titre du poste *</label>
            <input type="text" name="titre" value={formData.titre} onChange={handleChange}
              style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} required />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Description du poste *</label>
            <textarea name="description" value={formData.description} onChange={handleChange} rows="6"
              style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)', resize: 'vertical' }} required />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Type de contrat *</label>
              <select name="typeContrat" value={formData.typeContrat} onChange={handleChange}
                style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}>
                <option value="CDI">CDI</option>
                <option value="CDD">CDD</option>
                <option value="STAGE">Stage</option>
                <option value="ALTERNANCE">Alternance</option>
                <option value="FREELANCE">Freelance</option>
              </select>
            </div>
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Ville</label>
              <input type="text" name="ville" value={formData.ville} onChange={handleChange}
                style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }} />
            </div>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Niveau d'expérience requis *</label>
            <select name="niveauRequis" value={formData.niveauRequis} onChange={handleChange}
              style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}>
              <option value="DEBUTANT">Débutant (0-2 ans)</option>
              <option value="INTERMEDIAIRE">Intermédiaire (2-5 ans)</option>
              <option value="CONFIRME">Confirmé (5-10 ans)</option>
              <option value="EXPERT">Expert (10+ ans)</option>
            </select>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem', marginTop: '1rem' }}>
            <button type="button" onClick={() => navigate('/dashboard/recruiter')} className="btn btn-outline">Annuler</button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              <Save size={16} />{saving ? 'Enregistrement...' : 'Enregistrer les modifications'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditJobPage;
