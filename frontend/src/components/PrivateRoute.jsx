import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const PrivateRoute = ({ allowedRoles }) => {
  const { user, isAuthenticated, loading } = useAuth();

  if (loading) return <div>Chargement...</div>;

  // Si non connecté, rediriger vers login
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Si connecté mais rôle non autorisé, rediriger vers un dashboard approprié
  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to={user.role === 'RECRUITER' ? '/dashboard/recruiter' : '/dashboard/candidate'} replace />;
  }

  // Si tout est bon, afficher le composant enfant
  return <Outlet />;
};

export default PrivateRoute;
