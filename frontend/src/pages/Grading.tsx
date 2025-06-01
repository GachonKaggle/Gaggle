import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import SubmitModal from '../components/SubmitModal';
import '../styles/Grading.css';

interface Task {
  task: string;
}

const Grading: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [selectedTask, setSelectedTask] = useState<string>('');
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [showModal, setShowModal] = useState<boolean>(false);
  const navigate = useNavigate();
  const token = localStorage.getItem('token') || '';

  // 1. 토큰 없으면 에러 표시 및 로그인 페이지 유도(선택)
  useEffect(() => {
    if (!token) {
      setError('로그인이 필요합니다.');
      navigate('/login');
    }
  }, [token, navigate]);

  useEffect(() => {
    // 토큰이 없으면 tasks를 가져오지 않음
    if (!token) return;
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
      } catch (err) {
        setError('네트워크 오류 또는 서버 오류');
      } finally {
        setLoading(false);
      }
    };
    fetchTasks();
  }, [token]);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!token) {
      setError('로그인이 필요합니다.');
      return;
    }
    if (!selectedTask || !file) {
      setError('태스크와 파일을 모두 선택해주세요.');
      return;
    }
    const formData = new FormData();
    formData.append('file', file);
    formData.append('task', selectedTask);

    try {
      setLoading(true);
      setError(null);

      const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/grading`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });

      const data = await response.json();
      if (data.status === 'success') {
        setShowModal(true);
      } else {
        setError(data.message || '제출 중 오류가 발생했습니다.');
      }
    } catch (err) {
      setError('네트워크 오류 또는 서버 오류');
    } finally {
      setLoading(false);
    }
  };

  const handleGoToLeaderboard = () => {
    navigate(`/leaderboard/${selectedTask}`);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setFile(null);
    setSelectedTask('');
  };

  return (
    <div className="grading-container">
      <h2>Grading</h2>
      {loading && <p>처리 중...</p>}
      {error && <p className="error-message">{error}</p>}

      {/* 토큰 없으면 폼 비활성화 */}
      <form onSubmit={handleSubmit} className="grading-form">
        <div className="form-group">
          <label htmlFor="task">Task 선택:</label>
          <select
            id="task"
            value={selectedTask}
            onChange={e => setSelectedTask(e.target.value)}
            required
            disabled={!token}
          >
            <option value="">태스크를 선택하세요</option>
            {tasks.map(t => (
              <option key={t.task} value={t.task}>
                {t.task}
              </option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="file">파일 업로드:</label>
          <input
            type="file"
            id="file"
            accept=".zip"
            onChange={handleFileChange}
            required
            disabled={!token}
          />
        </div>

        <button type="submit" disabled={loading || !token}>제출하기</button>
      </form>

      <SubmitModal
        isOpen={showModal}
        task={selectedTask}
        onClose={handleCloseModal}
        onGoToLeaderboard={handleGoToLeaderboard}
      />
    </div>
  );
};

export default Grading;
