/// <reference types="vite/client" />

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/leaderboardStyles.css';

interface Task {
  task: string;
}

const Leaderboard: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const token = localStorage.getItem('token') || '';

  useEffect(() => {
    const fetchTasks = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await fetch(
          `${import.meta.env.VITE_API_BASE_URL}/leaderboard/task`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        const json = await res.json();
        if (json.status === 'success') {
          setTasks(json.data);
        } else {
          setError(json.message || '태스크 목록을 불러오지 못했습니다.');
        }
      } catch (err: any) {
        setError('네트워크 오류 또는 서버 오류');
      } finally {
        setLoading(false);
      }
    };
    fetchTasks();
  }, [token]);

  return (
    <div style={{ padding: '2rem', textAlign: 'center' }}>
      <h2 style={{
        fontWeight: 700, 
        fontSize: '2rem', 
        letterSpacing: '-0.01em', 
        marginBottom: '1.6rem'
      }}>
        Task 목록
      </h2>
      {loading && <p>불러오는 중...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <div className="task-card-list">
        {tasks.map((t) => (
          <button
            key={t.task}
            className="task-card"
            onClick={() => navigate(`/leaderboard/${t.task}`)}
            type="button"
          >
            {/* 아이콘 예시: <span style={{marginRight:'0.5rem'}}>🗂️</span> */}
            {t.task}
          </button>
        ))}
      </div>
    </div>
  );
};

export default Leaderboard;
