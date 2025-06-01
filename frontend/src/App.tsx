import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { ResultProvider } from './contexts/ResultContext';
import Home from './pages/Home';
import Tasks from './pages/Tasks';
import Leaderboard from './pages/Leaderboard';
import LeaderboardDetail from './pages/LeaderboardDetail';
import MyPage from './pages/MyPage';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Signup from './pages/Signup';
import NotFound from './components/NotFound';
import Grading from './pages/Grading';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <ResultProvider>
        <BrowserRouter>
          <Navbar />
          <Routes>
            <Route path="/" element={<Home />} />    
            <Route element={<ProtectedRoute />}>
              <Route path="/grading" element={<Grading />} />
              <Route path="/leaderboard" element={<Leaderboard />} />
              <Route path="/leaderboard/:taskId" element={<LeaderboardDetail />} />
              <Route path="/tasks" element={<Tasks />} />
              <Route path="/mypage" element={<MyPage />} />
            </Route>
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/*" element={<NotFound />} />
          </Routes>
        </BrowserRouter>
      </ResultProvider>
    </AuthProvider>
  );
};

export default App;
