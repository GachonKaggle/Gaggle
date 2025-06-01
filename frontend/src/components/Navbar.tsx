import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../styles/Navbar.css';
import { AuthContext } from '../contexts/AuthContext';

const Navbar: React.FC = () => {
  const { isLoggedIn, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout(); // localStorage에서 토큰 완전 삭제 + 상태 갱신
    navigate('/'); // 홈으로 이동
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <Link to="/" className="navbar-logo">🏠 Gakkle</Link>
      </div>
      <div className="navbar-center">
        <Link to="/" className="navbar-link">Home</Link>
        <Link to="/tasks" className="navbar-link">Tasks</Link>
        <Link to="/grading" className="navbar-link">Grading</Link>
        <Link to="/leaderboard" className="navbar-link">Leaderboard</Link>
      </div>
      <div className="navbar-right">
        {isLoggedIn ? (
          <>
            <Link to="/mypage" className="navbar-link">MyPage</Link>
            <button className="navbar-button" onClick={handleLogout}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/login" className="navbar-link">Sign In</Link>
            <Link to="/signup" className="navbar-link">Sign Up</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
