package com.example.notificationservice.handler;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.notificationservice.dto.WebSocketMessage;
import com.example.notificationservice.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class StatisticsWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private StatisticsService statisticsService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);

        // Send initial statistics
        WebSocketMessage response = WebSocketMessage.builder()
                .type("CONNECTED")
                .message("Successfully connected to statistics stream")
                .timestamp(System.currentTimeMillis())
                .build();

        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            log.debug("Received message: {}", payload);

            WebSocketMessage wsMessage = objectMapper.readValue(payload, WebSocketMessage.class);

            if ("SUBSCRIBE".equalsIgnoreCase(wsMessage.getType())) {
                // The subscription is tracked per session; broadcasting happens elsewhere
            } else if ("PING".equalsIgnoreCase(wsMessage.getType())) {
                WebSocketMessage pong = WebSocketMessage.builder()
                        .type("PONG")
                        .timestamp(System.currentTimeMillis())
                        .build();
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pong)));
            }
        } catch (Exception e) {
            log.error("Error handling WebSocket message", e);
            WebSocketMessage error = WebSocketMessage.builder()
                    .type("ERROR")
                    .message("Error processing message: " + e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(error)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void broadcastStatistics(Object statistics) {
        WebSocketMessage message = WebSocketMessage.builder()
                .type("STATS_UPDATE")
                .channel("statistics")
                .data(statistics)
                .timestamp(System.currentTimeMillis())
                .build();

        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            log.error("Error serializing statistics", e);
            return;
        }

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(json));
                } catch (IOException e) {
                    log.error("Error sending message to session: {}", session.getId(), e);
                }
            }
        }
    }

    public int getActiveConnections() {
        return (int) sessions.stream().filter(WebSocketSession::isOpen).count();
    }
}
