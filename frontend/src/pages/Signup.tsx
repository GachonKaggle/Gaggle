import React, { useState, FormEvent, ChangeEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { CSSProperties } from 'react';

const Signup: React.FC = () => {
  const [id, setId] = useState<string>('');           
  const [password, setPassword] = useState<string>(''); 
  const [confirmPassword, setConfirmPassword] = useState<string>(''); // confirm password state 추가
  const [error, setError] = useState<string>(''); // 에러 메시지 상태 추가
  const navigate = useNavigate();

  const handleSignup = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // 비밀번호 일치 유효성 검사
    if (password !== confirmPassword) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    setError(''); // 에러 메시지 초기화

    try {
      const response = await axios.post(`${import.meta.env.VITE_API_BASE_URL}/user-info/signup`, {
        loginId: id.trim(),
        password: password,
      });

      alert('✅ 회원가입 성공');
      console.log('Signup response:', response.data);
      navigate('/login');
    } catch (error: any) {
      console.log(error);
      console.error('Signup error:', error);
      alert(`❌ 회원가입 실패: ${error.response?.data?.message || '오류 발생'}`);
    }
  };

  return (
    <div style={containerStyle}>
      <h2>Sign Up</h2>
      <form onSubmit={handleSignup} style={formStyle} autoComplete="off">
        <input
          type="text"
          placeholder="Login Id"
          value={id}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setId(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Confirm Password"
          value={confirmPassword}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setConfirmPassword(e.target.value)}
          required
        />
        {error && (
          <div style={{ color: 'red', fontSize: '0.9rem' }}>{error}</div>
        )}
        <button type="submit">Sign Up</button>
      </form>
      <p>Already have an account? <Link to="/login">Login here</Link></p>
    </div>
  );
};

const containerStyle: CSSProperties = {
  maxWidth: '400px',
  margin: 'auto',
  padding: '2rem',
};

const formStyle: CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  gap: '1rem',
};

export default Signup;
