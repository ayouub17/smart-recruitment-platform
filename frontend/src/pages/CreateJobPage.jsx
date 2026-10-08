import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

const CreateJobPage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    titre: '',
    description: '',
    typeContrat: 'CDI',
    ville: '',
    niveauRequis: 'DEBUTANT'
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Create job as draft
      const response = await api.post('/jobs', {
        ...formData
      });
      
      // Publish job immediately
      await api.put(`/jobs/${response.data.id}/publish`);
      
      navigate('/dashboard/recruiter');
    } catch (err) {
      console.error(err);
      setError("Erreur lors de la création de l'offre.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <h2 style={{ marginBottom: '2rem' }}>Publier une nouvelle offre</h2>

      <div className="card">
        {error && (
          <div style={{ backgroundColor: 'var(--error-color)', color: 'white', padding: '0.75rem', borderRadius: 'var(--radius-md)', marginBottom: '1rem' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Titre du poste *</label>
            <input 
              type="text" 
              name="titre" 
              value={formData.titre} 
              onChange={handleChange} 
              style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}
              required 
            />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Description du poste *</label>
            <textarea 
              name="description" 
              value={formData.description} 
              onChange={handleChange} 
              rows="6"
              style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)', resize: 'vertical' }}
              required 
            ></textarea>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Type de contrat *</label>
              <select 
                name="typeContrat" 
                value={formData.typeContrat} 
                onChange={handleChange}
                style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}
              >
                <option value="CDI">CDI</option>
                <option value="CDD">CDD</option>
                <option value="STAGE">Stage</option>
                <option value="ALTERNANCE">Alternance</option>
                <option value="FREELANCE">Freelance</option>
              </select>
            </div>
            
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Ville</label>
              <input 
                type="text" 
                name="ville" 
                value={formData.ville} 
                onChange={handleChange} 
                style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}
              />
            </div>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Niveau d'expérience requis *</label>
            <select 
              name="niveauRequis" 
              value={formData.niveauRequis} 
              onChange={handleChange}
              style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}
            >
              <option value="DEBUTANT">Débutant (0-2 ans)</option>
              <option value="INTERMEDIAIRE">Intermédiaire (2-5 ans)</option>
              <option value="CONFIRME">Confirmé (5-10 ans)</option>
              <option value="EXPERT">Expert (10+ ans)</option>
            </select>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '1rem', marginTop: '1rem' }}>
            <button type="button" onClick={() => navigate('/dashboard/recruiter')} className="btn btn-outline">Annuler</button>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Publication...' : 'Publier l\'offre'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateJobPage;
