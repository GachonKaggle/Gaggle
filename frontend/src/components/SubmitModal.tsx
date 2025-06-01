// components/SubmitModal.tsx
import React, { useRef, useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

interface SubmitModalProps {
  isOpen: boolean;
  task: string;
  token?: string;
  onClose: () => void;
  onGoToLeaderboard: () => void;
}

interface ProgressPayload {
  task: string;
  filename: string;
  current: number;
  total: number;
}

interface ResultPayload {
  status: string; // "success" 또는 "fail"
  data: {
    task: string;
    psnrAvg?: number;
    ssimAvg?: number;
    token?: string;
    userId?: string;
    requestId?: string;
    loginId?: string;
  };
  message?: string | null;
}

const SubmitModal: React.FC<SubmitModalProps> = ({
  isOpen,
  task,
  token,
  onClose,
  onGoToLeaderboard,
}) => {
  const stompClientRef = useRef<Client | null>(null);
  const [progress, setProgress] = useState<ProgressPayload | null>(null);
  const [progressPercent, setProgressPercent] = useState(0);
  const [result, setResult] = useState<ResultPayload | null>(null);

  const localToken = token || localStorage.getItem('token') || '';

  useEffect(() => {
    if (!isOpen) {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
        stompClientRef.current = null;
      }
      setProgress(null);
      setProgressPercent(0);
      setResult(null);
      return;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS('/ws-progress'),
      debug: (str) => console.log('[STOMP]', str),
      onConnect: () => {
        client.subscribe(`/topic/progress/${localToken}`, (msg) => {
          const body = JSON.parse(msg.body);
          setProgress(body.data);
          const percent = body.data && body.data.total
            ? Math.round((body.data.current / body.data.total) * 100)
            : 0;
          setProgressPercent(percent);
        });
        client.subscribe(`/topic/result/${localToken}`, (msg) => {
          const body = JSON.parse(msg.body);
          setResult(body);
        });
      },
    });
    stompClientRef.current = client;
    client.activate();

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
        stompClientRef.current = null;
      }
    };
    // eslint-disable-next-line
  }, [isOpen, localToken]);

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content" style={{
        minWidth: 360,
        maxWidth: 400,
        margin: '0 auto',
        textAlign: 'center',
        borderRadius: 16,
        boxShadow: '0 6px 32px rgba(0,0,0,0.14)',
        background: '#fff',
        padding: '2.5rem 2rem',
        position: 'relative', // ⭐️ X 버튼 배치용
      }}>
        {/* X(닫기) 버튼 추가 */}
        <button
          type="button"
          onClick={onClose}
          aria-label="닫기"
          style={{
            position: 'absolute',
            right: 18,
            top: 16,
            background: 'none',
            border: 'none',
            fontSize: 22,
            fontWeight: 'bold',
            cursor: 'pointer',
            color: '#aaa',
          }}
        >
          ×
        </button>

        <h3 style={{marginBottom: 12, fontSize: '1.22rem'}}>
          {result
            ? result.status === 'success'
              ? '채점 완료'
              : '채점 실패'
            : '채점 진행중...'}
        </h3>
        <div style={{marginBottom: 18, fontWeight: 500, color:'#0071e3'}}>
          {progress && (
            <>
              <div style={{fontSize:'1.1rem',marginBottom:8}}>Task: <b>{progress.task}</b></div>
              <div style={{fontSize:'0.97rem',margin:'8px 0 18px 0'}}>파일: <span style={{fontWeight: 600}}>{progress.filename}</span></div>
              {/* Progress Bar */}
              <div style={{
                width: '92%',
                height: '27px',
                background: '#e6eaf1',
                borderRadius: 18,
                margin: '0 auto 8px auto',
                overflow: 'hidden',
                display: 'flex',
                alignItems:'center'
              }}>
                <div
                  style={{
                    width: `${progressPercent}%`,
                    background: 'linear-gradient(90deg, #36c7ff 0%, #0076fc 100%)',
                    height: '100%',
                    borderRadius: 18,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    color: '#fff',
                    fontWeight: 700,
                    fontSize: '1.1rem',
                    transition: 'width 0.4s'
                  }}
                >{progressPercent}%</div>
              </div>
              <div style={{fontSize:'0.99rem'}}>
                {progress.current} / {progress.total}
              </div>
            </>
          )}
        </div>
        {/* 결과 보여주기 */}
        {result && (
        <>
            <div style={{ margin: '15px 0 10px 0', fontWeight: 500 }}>
            {result.status?.toLowerCase() === 'success' ? (
                <>
                <div>평균 PSNR: {result.data?.psnrAvg}</div>
                <div>평균 SSIM: {result.data?.ssimAvg}</div>
                <div>리더보드에서 결과를 확인하세요.</div>
                </>
            ) : (
                <span style={{ color: 'red' }}>
                {result.message || '채점 실패'}
                </span>
            )}
            </div>
            <div className="modal-buttons" style={{marginTop:18}}>
              <button
                onClick={onGoToLeaderboard}
                className="primary-button"
                disabled={!result}
              >
                리더보드에서 결과 보기
              </button>
              <button onClick={onClose} className="secondary-button">
                추가 제출하기
              </button>
            </div>
        </>
        )}
      </div>
    </div>
  );
};

export default SubmitModal;
