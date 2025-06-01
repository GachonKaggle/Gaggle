import React, { useEffect, useState } from 'react';
import { Navigate, Outlet } from 'react-router-dom';

const ProtectedRoute: React.FC = () => {
  const token = localStorage.getItem('token');
  const [showMessage, setShowMessage] = useState(false);
  const [redirect, setRedirect] = useState(false);

  useEffect(() => {
    if (!token || token === 'undefined' || token === 'null' || token === '') {
      setShowMessage(true);
      const timer = setTimeout(() => setRedirect(true), 2000); // 2초 후 리다이렉트
      return () => clearTimeout(timer);
    }
  }, [token]);

  if (!!token && token !== 'undefined' && token !== 'null' && token !== '') {
    return <Outlet />;
  }

  if (showMessage && !redirect) {
    return (
      <div style={{
        display: 'flex', flexDirection: 'column', justifyContent: 'center',
        alignItems: 'center', minHeight: '60vh', fontSize: '1.5rem'
      }}>
        로그인이 필요합니다.<br />
        <span style={{ fontSize: '1rem', marginTop: '1rem', color: 'gray' }}>
          곧 로그인 페이지로 이동합니다.
        </span>
      </div>
    );
  }

  if (redirect) {
    return <Navigate to="/login" replace />;
  }

  return null;
};

export default ProtectedRoute;
