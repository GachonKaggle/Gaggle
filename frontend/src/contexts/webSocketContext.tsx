import React, { createContext, useContext, useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

interface WebSocketContextType {
    client: Client | null;
    isConnected: boolean;
    connect: () => void;
    disconnect: () => void;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

export const WebSocketProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [isConnected, setIsConnected] = useState(false);
    const clientRef = useRef<Client | null>(null);

    const connect = () => {
        if (clientRef.current && clientRef.current.connected) return;

        const client = new Client({
            webSocketFactory: () => new SockJS(`${import.meta.env.VITE_API_BASE_URL}/ws-progress`),
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
        clientRef.current = client;
    };

    const disconnect = () => {
        clientRef.current?.deactivate();
        setIsConnected(false);
    };

    useEffect(() => {
        return () => {
        disconnect();
        };
    }, []);

    return (
        <WebSocketContext.Provider value={{ client: clientRef.current, isConnected, connect, disconnect }}>
            {children}
        </WebSocketContext.Provider>
    );
};

export const useWebSocketContext = () => {
    const context = useContext(WebSocketContext);
    if (!context) {
        throw new Error('useWebSocketContext must be used within a WebSocketProvider');
    }
    return context;
};
