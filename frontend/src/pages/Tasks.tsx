import React, { useRef, useState } from 'react';
import axios from 'axios';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import TaskSubmitModal from '../components/TaskSubmitModal';
import '../styles/Tasks.css';

const Tasks: React.FC = () => {
  const [messages, setMessages] = useState<string[]>([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [waitingForTaskResult, setWaitingForTaskResult] = useState(false);
  const [taskResult, setTaskResult] = useState<any>(null);
  const [permissionError, setPermissionError] = useState<string>(''); // 권한 에러 상태 추가
  const token = localStorage.getItem('token');

  const stompClientRef = useRef<Client | null>(null);

  const startTask = () => {
    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }
    setModalOpen(true);
    setTaskResult(null);
    setMessages([]);
    setPermissionError(''); // 모달 열 때 권한 에러 리셋
  };

  const submitTaskFile = async (file: File, task: string) => {
    if (!token) {
      alert('로그인 필요');
      return;
    }
    try {
      setWaitingForTaskResult(true);
      setTaskResult(null);
      setPermissionError(''); // 제출 시 권한 에러 리셋

      const formData = new FormData();
      formData.append('file', file);
      formData.append('task', task);

      await axios.post(
        `${import.meta.env.VITE_API_BASE_URL}/grading/task`,
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );
      setModalOpen(false);

      subscribeTaskWebSocket(token);

    } catch (e: any) {
      setWaitingForTaskResult(false);

      // 서버에서 접근 권한이 없는 경우
      const responseMessage = e.response?.data?.message;
      if (responseMessage === '접근 권한이 없습니다.') {
        setPermissionError(responseMessage);
        alert(responseMessage);
        setModalOpen(false);
        return;
      }

      alert('파일 제출 실패');
    }
  };

  const subscribeTaskWebSocket = (token: string) => {
    if (stompClientRef.current) {
      stompClientRef.current.deactivate();
      stompClientRef.current = null;
    }

    const client = new Client({
      webSocketFactory: () => new SockJS('/ws-progress'),
      reconnectDelay: 5000,
      debug: (str) => console.log('[STOMP]', str),
      onConnect: () => {
        client.subscribe(`/topic/task/${token}`, (message) => {
          setWaitingForTaskResult(false);
          setTaskResult(message.body);
          setMessages(prev => [...prev, message.body]);
        });
      },
      onStompError: (frame) => {
        setWaitingForTaskResult(false);
        alert('WebSocket STOMP 에러: ' + frame.headers['message']);
      },
      onWebSocketError: (event) => {
        setWaitingForTaskResult(false);
        alert('WebSocket 연결 실패');
      },
      onDisconnect: () => {
        stompClientRef.current = null;
      }
    });

    stompClientRef.current = client;
    client.activate();
  };

  React.useEffect(() => {
    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
        stompClientRef.current = null;
      }
    };
  }, []);

  // 성공 메시지 파싱
  let showSuccess = false;

  if (taskResult) {
    try {
      const resultObj = typeof taskResult === 'string' ? JSON.parse(taskResult) : taskResult;
      const status = resultObj.status || (resultObj.data && resultObj.data.status) || '';
      showSuccess = status === 'success';

      // WebSocket에서 "접근 권한이 없습니다."면 permissionError로 표시
      if (status === 'fail' && resultObj.message === '접근 권한이 없습니다.') {
        setPermissionError(resultObj.message);
      }
    } catch (e) {
      showSuccess = false;
    }
  }

  return (
    <div className="tasks-container">
      <h1 className="tasks-title">📄 Tasks Page</h1>
      <div className="tasks-action-row">
        <button className="tasks-button" onClick={startTask}>
          Task Dataset Register
        </button>
        {showSuccess && (
          <span style={{ color: 'green', fontWeight: 'bold', fontSize: '1.1rem' }}>
            success
          </span>
        )}
      </div>

      {waitingForTaskResult && (
        <div className="task-waiting">
          <p>⏳ Waiting for registering task dataset...</p>
        </div>
      )}

      <TaskSubmitModal
        show={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={submitTaskFile}
        taskInfo={null}
      />
    </div>
  );
};

export default Tasks;
