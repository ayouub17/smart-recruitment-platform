
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { UserRound, FileText, HelpCircle, LogOut, ChevronDown, ClipboardList } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dashboardLink = user?.role === 'RECRUITER' ? '/dashboard/recruiter' : '/dashboard/candidate';

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className={`navbar ${isAuthenticated ? 'navbar-authenticated' : ''}`}>
      <Link to={isAuthenticated ? dashboardLink : '/'} className="nav-brand"><span className="brand-logo" aria-hidden="true"><img src="/logo.jpg" alt="" /></span><span>RecruitPro</span></Link>
      <div className="nav-links">
        {!isAuthenticated ? <Link to="/login" className="landing-login-link">Se connecter <span aria-hidden="true">↗</span></Link> : (
          <>
          <Link to={dashboardLink} className="nav-main-link">{user.role === 'CANDIDATE' ? 'Offres' : 'Mes offres'}</Link>
          {user.role === 'CANDIDATE' && <Link to="/applications" className="nav-main-link">Mes candidatures</Link>}
          <div className="profile-menu">
            <button type="button" className="profile-menu-trigger" onClick={() => setDropdownOpen((open) => !open)} aria-expanded={dropdownOpen} aria-haspopup="menu">
              <span className="profile-menu-name">{user.prenom} {user.nom}</span>
              <span className="profile-menu-avatar" aria-hidden="true" style={{ overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                {user.photoUrl ? <img src={user.photoUrl} alt="Avatar" style={{ width: '100%', height: '100%', objectFit: 'cover' }} /> : <UserRound size={19} />}
              </span>
              <ChevronDown size={16} aria-hidden="true" />
            </button>
            {dropdownOpen && <div className="profile-menu-dropdown" role="menu">
              <Link to={user.role === 'CANDIDATE' ? '/profile' : '/profile/recruiter'} className="profile-menu-item" role="menuitem" onClick={() => setDropdownOpen(false)}><UserRound size={16} />Mon profil</Link>
              {user.role === 'CANDIDATE' && <Link to={{ pathname: '/profile', hash: '#cv' }} className="profile-menu-item" role="menuitem" onClick={() => setDropdownOpen(false)}><FileText size={16} />Mon CV</Link>}
              {user.role === 'CANDIDATE' && <Link to="/applications" className="profile-menu-item" role="menuitem" onClick={() => setDropdownOpen(false)}><ClipboardList size={16} />Mes candidatures</Link>}
              <Link to="/help" className="profile-menu-item" role="menuitem" onClick={() => setDropdownOpen(false)}><HelpCircle size={16} />Aide</Link>
              <button type="button" onClick={handleLogout} className="profile-menu-item profile-menu-logout" role="menuitem"><LogOut size={16} />Deconnexion</button>
            </div>}
          </div>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
