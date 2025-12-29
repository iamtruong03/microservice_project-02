package com.example.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String type;  // "SUBSCRIBE", "UNSUBSCRIBE", "STATS_UPDATE", "ERROR"
    private String channel;  // "statistics", "transactions", "orders", etc.
    private Object data;
    private String message;
    private Long timestamp;
}
