import React, { useState, useContext, FormEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { AuthContext } from '../contexts/AuthContext';
import axios from 'axios';

const Login: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useContext(AuthContext);
  const [loginId, setLoginId] = useState<string>('');  // 변경된 변수명
  const [password, setPassword] = useState<string>('');

  const handleLogin = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
  
    try {
      const response = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/user-info/login`, {
        loginId: loginId.trim(),
        password: password,
      });
  
      const res = response.data;
  
      if (res.status === 'success' && res.data) {
        const { token } = res.data;
  
        // 로그인 성공 처리 (임시로 userId/teamId null 설정)
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('loginId', loginId.trim());
        localStorage.setItem('token', token);
  
        login(token, '', ''); // userId, teamId는 백엔드 응답에 없음
  
        alert('✅ 로그인 성공!');
        navigate('/mypage');
      } else {
        alert(`❌ 로그인 실패: ${res.message || '서버 오류'}`);
      }
    } catch (error: any) {
      console.error('Login error:', error);
      alert(`❌ 로그인 실패: ${error.response?.data?.message || '오류 발생'}`);
    }
  };
  

  return (
    <div style={{ maxWidth: '400px', margin: 'auto', padding: '2rem' }}>
      <h2>Login</h2>
      <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <input
          type="text"
          placeholder="Login Id"
          value={loginId}
          onChange={(e) => setLoginId(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Login</button>
      </form>
      <p>Don't have an account? <Link to="/signup">Sign up here</Link></p>
    </div>
  );
};

export default Login;

