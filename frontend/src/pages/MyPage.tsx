/// <reference types="vite/client" />

import React, { useEffect, useState } from 'react';

interface MyUserEntry {
  loginId: string;
  psnrAvg: number;
  ssimAvg: number;
  task: string;
  rank: number;
  days: string;
}

interface MyUserApiResponse {
  status: 'success' | 'fail';
  data: MyUserEntry[];
  message: string | null;
}

const MyPage: React.FC = () => {
  const [records, setRecords] = useState<MyUserEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const token = localStorage.getItem('token') || '';

  useEffect(() => {
    // API 호출 함수
    const fetchUserLeaderboard = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await fetch(
          `${import.meta.env.VITE_API_BASE_URL}/leaderboard/user`,
          {
            headers: { Authorization: `Bearer ${token}` }
          }
        );
        const json: MyUserApiResponse = await res.json();
        if (json.status === 'success') {
          setRecords(json.data);
        } else {
          setError(json.message || '데이터를 불러오지 못했습니다.');
        }
      } catch (err: any) {
        setError('네트워크 오류 또는 서버 오류');
      } finally {
        setLoading(false);
      }
    };
    fetchUserLeaderboard();
  }, [token]);

  return (
    <div style={{ maxWidth: '800px', margin: '2rem auto', padding: '1rem' }}>
      <h2 style={{ textAlign: 'center' }}>My Page (User Record)</h2>

      {loading && <p style={{ textAlign: 'center' }}>불러오는 중...</p>}
      {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}

      <table className="leaderboard-table">
        <thead>
          <tr>
            <th>Task</th>
            <th>PSNR</th>
            <th>SSIM</th>
            <th>Rank</th>
            <th>Last</th>
          </tr>
        </thead>
        <tbody>
          {records.length > 0 ? (
            records.map((entry, idx) => (
              <tr key={entry.task + '-' + entry.rank}>
                <td>{entry.task}</td>
                <td>{entry.psnrAvg?.toFixed(2) ?? '-'}</td>
                <td>{entry.ssimAvg?.toFixed(2) ?? '-'}</td>
                <td>{entry.rank}</td>
                <td>{entry.days}</td>
              </tr>
            ))
          ) : (
            !loading && (
              <tr>
                <td colSpan={5} style={{ textAlign: 'center', color: '#aaa' }}>
                  No records found.
                </td>
              </tr>
            )
          )}
        </tbody>
      </table>
    </div>
  );
};

export default MyPage;
