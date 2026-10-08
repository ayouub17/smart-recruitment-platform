import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import CandidateDashboard from './pages/CandidateDashboard';
import RecruiterDashboard from './pages/RecruiterDashboard';
import RegisterPage from './pages/RegisterPage';
import CandidateProfilePage from './pages/CandidateProfilePage';
import HelpPage from './pages/HelpPage';
import AuthChoicePage from './pages/AuthChoicePage';
import PrivateRoute from './components/PrivateRoute';
import JobDetailPage from './pages/JobDetailPage';
import CreateJobPage from './pages/CreateJobPage';
import EditJobPage from './pages/EditJobPage';
import CandidateApplicationsPage from './pages/CandidateApplicationsPage';
import RecruiterApplicationsPage from './pages/RecruiterApplicationsPage';
import CompanyProfilePage from './pages/CompanyProfilePage';
import RecruiterProfilePage from './pages/RecruiterProfilePage';
import LandingPage from './pages/LandingPage';

function App() {
  return (
    <BrowserRouter>
      <div className="app-container">
        <Navbar />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/bienvenue" element={<AuthChoicePage />} />
            <Route path="/home" element={<HomePage />} />
            <Route path="/jobs/:id" element={<JobDetailPage />} />
            <Route path="/companies/:id" element={<CompanyProfilePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/help" element={<HelpPage />} />
            
            {/* Protected Routes for Candidate */}
            <Route element={<PrivateRoute allowedRoles={['CANDIDATE']} />}>
              <Route path="/dashboard/candidate" element={<CandidateDashboard />} />
              <Route path="/profile" element={<CandidateProfilePage />} />
              <Route path="/applications" element={<CandidateApplicationsPage />} />
            </Route>

            {/* Protected Routes for Recruiter */}
            <Route element={<PrivateRoute allowedRoles={['RECRUITER']} />}>
              <Route path="/dashboard/recruiter" element={<RecruiterDashboard />} />
              <Route path="/profile/recruiter" element={<RecruiterProfilePage />} />
              <Route path="/jobs/create" element={<CreateJobPage />} />
              <Route path="/jobs/:id/edit" element={<EditJobPage />} />
              <Route path="/jobs/:id/applications" element={<RecruiterApplicationsPage />} />
            </Route>
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
