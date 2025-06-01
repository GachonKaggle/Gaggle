import { useEffect, useCallback, useRef, useState } from 'react';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { GradingProgressData } from '../types/webSocketTypes';
import { GradingResultData } from '../types/webSocketTypes';
import { TaskResultData } from '../types/webSocketTypes';
import { WebSocketMessage } from '../types/webSocketTypes';


interface UseWebSocketOptions {
    token: string;
    onProgress?: (data: GradingProgressData) => void;
    onGradingResult?: (data: GradingResultData) => void;
    onTaskResult?: (data: TaskResultData) => void;
}

export const useWebSocket = ({ token, onProgress, onGradingResult, onTaskResult }: UseWebSocketOptions) => {
    const [isConnected, setIsConnected] = useState(false);
    const clientRef = useRef<Client | null>(null);

    const subscribe = useCallback(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS(`${import.meta.env.VITE_WS_BASE_URL}/ws-progress`),
            connectHeaders: {
                Authorization: `Bearer ${localStorage.getItem('token') || ''}`,
            },
            debug: (str) => console.log('[WebSocket]', str),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
            onConnect: () => {
                console.log('✅ WebSocket connected');
                setIsConnected(true);

                const progressSub = client.subscribe(`/topic/progress/${token}`, handleProgress);
                const resultSub = client.subscribe(`/topic/result/${token}`, handleResult);
                const taskSub = client.subscribe(`/topic/task/${token}`, handleTask);

                clientRef.current = client;

                return () => {
                    progressSub.unsubscribe();
                    resultSub.unsubscribe();
                    taskSub.unsubscribe();
                };
            },
            onDisconnect: () => {
                console.log('❌ WebSocket disconnected');
                setIsConnected(false);
            },
            onStompError: (frame) => {
                console.error('❗ STOMP error:', frame.headers['message'], frame.body);
            },
        });

        client.activate();
    }, [token]);

    const handleProgress = (message: IMessage) => {
        const parsed = JSON.parse(message.body) as WebSocketMessage;
        if (parsed.status === 'progress') {
            console.log('Progress 수신:', parsed.data);
            onProgress?.(parsed.data);
        }
    };

    const handleResult = (message: IMessage) => {
        const parsed = JSON.parse(message.body) as WebSocketMessage;
        if (parsed.status === 'success' || parsed.status === 'fail') {
            if ('psnrAvg' in parsed.data) {
                console.log('Grading Result 수신:', parsed.data);
                onGradingResult?.(parsed.data as GradingResultData);
            }
        }
    };

    const handleTask = (message: IMessage) => {
        const parsed = JSON.parse(message.body) as WebSocketMessage;
        if (parsed.status === 'success' || parsed.status === 'fail') {
            if (!('psnrAvg' in parsed.data)) {
                console.log('Task 결과 수신:', parsed.data);
                onTaskResult?.(parsed.data as TaskResultData);
            }
        }
    };

    useEffect(() => {
        const cleanup = subscribe();
        return typeof cleanup === 'function' ? cleanup : undefined;
    }, [subscribe]);

    return { isConnected };
};
