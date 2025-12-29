import React, { createContext, useState, useEffect, useCallback } from 'react';
import StatisticsWebSocketService from '../services/statisticsWebSocket';

export const StatisticsContext = createContext();

export const StatisticsProvider = ({ children }) => {
  const [statistics, setStatistics] = useState(null);
  const [systemHealth, setSystemHealth] = useState('HEALTHY');
  const [isConnected, setIsConnected] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const wsService = new StatisticsWebSocketService(
    process.env.REACT_APP_WS_URL || 'ws://localhost:8080/ws/statistics'
  );

  useEffect(() => {
    const initWebSocket = async () => {
      try {
        setLoading(true);
        await wsService.connect();
        setIsConnected(true);
        setError(null);

        // Handle statistics updates
        wsService.on('STATS_UPDATE', (message) => {
          if (message.data) {
            setStatistics(message.data);
            if (message.data.systemHealth) {
              setSystemHealth(message.data.systemHealth);
            }
          }
        });

        // Handle connection status
        wsService.on('CONNECTED', () => {
          setIsConnected(true);
          setError(null);
        });

        // Handle errors
        wsService.on('ERROR', (message) => {
          console.error('WebSocket error:', message.message);
          setError(message.message);
        });

        wsService.on('PONG', () => {
          console.log('PONG received');
        });

        // Setup ping interval
        const pingInterval = setInterval(() => {
          if (wsService.isReady()) {
            wsService.ping();
          }
        }, 30000); // Ping every 30 seconds

        return () => {
          clearInterval(pingInterval);
          wsService.disconnect();
        };
      } catch (err) {
        console.error('Failed to initialize WebSocket:', err);
        setError('Failed to connect to statistics service');
        setIsConnected(false);
      } finally {
        setLoading(false);
      }
    };

    initWebSocket();
  }, []);

  const value = {
    statistics,
    systemHealth,
    isConnected,
    loading,
    error,
    wsService,
  };

  return (
    <StatisticsContext.Provider value={value}>{children}</StatisticsContext.Provider>
  );
};
