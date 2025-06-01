/// <reference types="vite/client" />
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../styles/leaderboardStyles.css';
import { LeaderboardEntry, LeaderboardResponse } from '../types/webSocketTypes';

const LeaderboardDetail: React.FC = () => {
  const { taskId } = useParams<{ taskId: string }>();
  const navigate = useNavigate();

  const token = localStorage.getItem('token') || '';
  const myLoginId = localStorage.getItem('loginId') || '';

  const [entries, setEntries] = useState<LeaderboardEntry[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // API 요청
  useEffect(() => {
    const fetchTaskLeaderboard = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await fetch(
          `${import.meta.env.VITE_API_BASE_URL}/leaderboard?task=${taskId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        const json: LeaderboardResponse = await res.json();
        if (json.status === 'success') {
          // 중복된 사용자의 최고 점수만 선택
          const uniqueEntries = json.data.reduce((acc: LeaderboardEntry[], current) => {
            const existingEntry = acc.find(entry => entry.loginId === current.loginId);
            if (!existingEntry || current.psnrAvg > existingEntry.psnrAvg) {
              // 기존 항목이 있으면 제거
              if (existingEntry) {
                acc = acc.filter(entry => entry.loginId !== current.loginId);
              }
              acc.push(current);
            }
            return acc;
          }, []);

          // PSNR 점수 기준으로 내림차순 정렬
          const sortedEntries = uniqueEntries.sort((a, b) => b.psnrAvg - a.psnrAvg);
          
          // 랭크 재계산
          const rankedEntries = sortedEntries.map((entry, index) => ({
            ...entry,
            rank: index + 1
          }));

          setEntries(rankedEntries);
        } else {
          setError(json.message || '리더보드 데이터를 불러오지 못했습니다.');
        }
      } catch (err) {
        setError('네트워크 오류 또는 서버 오류');
      } finally {
        setLoading(false);
      }
    };

    fetchTaskLeaderboard();
  }, [taskId, token]);

  // 정렬된 entries 사용 (이미 정렬되어 있으므로 추가 정렬 불필요)
  const sorted = entries;

  // 내 팀(유저) 하이라이트
  const myTeamIndex = sorted.findIndex((e) => e.loginId === myLoginId);

  return (
    <div style={{ padding: '2rem' }}>
      <button
        onClick={() => navigate('/leaderboard')}
        style={{
          marginBottom: '1rem',
          padding: '0.5rem 1.2rem',
          border: '1px solid #ddd',
          background: '#fafafa',
          borderRadius: '5px',
          cursor: 'pointer',
        }}
      >
        &lt; Return to Leaderboard
      </button>
      <h2 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
        Leaderboard for Task <span style={{ color: '#6366f1' }}>{taskId}</span>
      </h2>

      {loading && <p style={{ textAlign: 'center' }}>불러오는 중...</p>}
      {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}

      {!loading && !error && (
        <table className="leaderboard-table">
          <thead>
            <tr>
              <th>rank</th>
              <th>Id</th>
              <th>PSNR</th>
              <th>SSIM</th>
              <th>Last</th>
            </tr>
          </thead>
          <tbody>
            {sorted.map((entry, idx) => {
              const isMe = entry.loginId === myLoginId;
              return (
                <tr
                  key={`${entry.loginId}-${entry.task}`}
                  className={isMe ? 'highlight-row' : ''}
                >
                  <td>{entry.rank}</td>
                  <td>{entry.loginId}</td>
                  <td>{entry.psnrAvg?.toFixed(2) ?? '-'}</td>
                  <td>{entry.ssimAvg?.toFixed(2) ?? '-'}</td>
                  <td>{entry.days ? entry.days : '-'}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default LeaderboardDetail;
