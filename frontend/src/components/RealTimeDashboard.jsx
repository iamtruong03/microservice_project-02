import React, { useContext, useState, useEffect } from 'react';
import { StatisticsContext } from '../context/StatisticsContext';
import './RealTimeDashboard.css';

const RealTimeDashboard = () => {
  const { statistics, systemHealth, isConnected, loading, error } =
    useContext(StatisticsContext);
  const [expandedCard, setExpandedCard] = useState(null);

  const getHealthColor = (health) => {
    switch (health) {
      case 'HEALTHY':
        return '#10b981';
      case 'WARNING':
        return '#f59e0b';
      case 'CRITICAL':
        return '#ef4444';
      default:
        return '#6b7280';
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'ACTIVE':
        return '#10b981';
      case 'WARNING':
        return '#f59e0b';
      case 'CRITICAL':
        return '#ef4444';
      default:
        return '#6b7280';
    }
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Connecting to statistics service...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard-container">
        <div className="error-message">
          <p>⚠️ {error}</p>
          <p>Please ensure the notification service is running on port 8080</p>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>📊 Real-Time Statistics Dashboard</h1>
        <div className="connection-status">
          <span
            className="status-indicator"
            style={{
              backgroundColor: isConnected ? '#10b981' : '#ef4444',
            }}
          ></span>
          <span>{isConnected ? 'Connected' : 'Disconnected'}</span>
        </div>
      </div>

      {statistics && (
        <>
          <div className="system-health-banner" style={{ borderLeftColor: getHealthColor(systemHealth) }}>
            <div className="health-content">
              <h2>System Status: {systemHealth}</h2>
              <div className="health-metrics">
                <div className="health-metric">
                  <span className="metric-label">Uptime:</span>
                  <span className="metric-value">{statistics.systemUptime?.toFixed(2)}%</span>
                </div>
                <div className="health-metric">
                  <span className="metric-label">Total Transactions:</span>
                  <span className="metric-value">{statistics.totalTransactions?.toLocaleString()}</span>
                </div>
                <div className="health-metric">
                  <span className="metric-label">Total Orders:</span>
                  <span className="metric-value">{statistics.totalOrders?.toLocaleString()}</span>
                </div>
                <div className="health-metric">
                  <span className="metric-label">Active Users:</span>
                  <span className="metric-value">{statistics.activeUsers?.toLocaleString()}</span>
                </div>
              </div>
            </div>
          </div>

          <div className="statistics-grid">
            {statistics.statistics && statistics.statistics.map((stat) => (
              <div
                key={stat.statType}
                className={`stat-card ${expandedCard === stat.statType ? 'expanded' : ''}`}
                onClick={() =>
                  setExpandedCard(
                    expandedCard === stat.statType ? null : stat.statType
                  )
                }
                style={{ borderTopColor: getStatusColor(stat.status) }}
              >
                <div className="card-header">
                  <h3>{stat.statType}</h3>
                  <span className="status-badge" style={{ backgroundColor: getStatusColor(stat.status) }}>
                    {stat.status}
                  </span>
                </div>

                <div className="card-metrics">
                  <div className="metric">
                    <span className="label">Total:</span>
                    <span className="value">{stat.totalCount?.toLocaleString()}</span>
                  </div>
                  <div className="metric">
                    <span className="label">Success Rate:</span>
                    <span className="value">{stat.successRate?.toFixed(2)}%</span>
                  </div>
                  <div className="metric">
                    <span className="label">Avg Time:</span>
                    <span className="value">{stat.avgProcessingTime?.toFixed(2)}ms</span>
                  </div>
                </div>

                {expandedCard === stat.statType && (
                  <div className="card-details">
                    <div className="detail-row">
                      <span>Success Count:</span>
                      <span className="success-value">{stat.successCount?.toLocaleString()}</span>
                    </div>
                    <div className="detail-row">
                      <span>Failure Count:</span>
                      <span className="failure-value">{stat.failureCount?.toLocaleString()}</span>
                    </div>
                    <div className="detail-row">
                      <span>Last Updated:</span>
                      <span>{new Date(stat.lastUpdated).toLocaleTimeString()}</span>
                    </div>
                    <div className="progress-bar">
                      <div
                        className="progress-fill"
                        style={{ width: `${stat.successRate}%` }}
                      ></div>
                    </div>
                  </div>
                )}
              </div>
            ))}
          </div>

          <div className="dashboard-footer">
            <p>Last updated: {new Date(statistics.timestamp).toLocaleTimeString()}</p>
            <p>Report ID: {statistics.reportId}</p>
          </div>
        </>
      )}
    </div>
  );
};

export default RealTimeDashboard;
