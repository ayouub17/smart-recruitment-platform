import { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem('token');
      const storedUser = localStorage.getItem('user');

      if (token && storedUser) {
        const parsedUser = JSON.parse(storedUser);
        if (parsedUser.role === 'RECRUITER') {
          try {
            // Optionnel : récupérer le logo à chaque chargement pour être à jour
            const res = await api.get('/recruiters/profile', { headers: { Authorization: `Bearer ${token}` } });
            parsedUser.companyLogoUrl = res.data.companyLogoUrl;
          } catch (e) {}
        }
        setUser(parsedUser);
      }
      setLoading(false);
    };
    initAuth();
  }, []);

  const login = async (email, password) => {
    try {
      const response = await api.post('/auth/login', { email, password });
      const { token, id, email: userEmail, nom, prenom, role } = response.data;

      const userData = { id, email: userEmail, nom, prenom, role };
      localStorage.setItem('token', token);
      
      if (role === 'RECRUITER') {
        try {
          const res = await api.get('/recruiters/profile');
          userData.companyLogoUrl = res.data.companyLogoUrl;
        } catch (e) {}
      }
      
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);
      
      return userData;
    } catch (error) {
      throw error;
    }
  };

  const register = async (formData) => {
    try {
      const response = await api.post('/auth/register', formData);
      return response.data;
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
  };

  const userWithPhoto = user ? { ...user, photoUrl: user.companyLogoUrl ? `http://localhost:8080/${user.companyLogoUrl.replace('\\', '/')}` : `https://ui-avatars.com/api/?name=${user.prenom}+${user.nom}&background=random` } : null;

  return (
    <AuthContext.Provider value={{ user: userWithPhoto, login, register, logout, loading, isAuthenticated: !!user }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
