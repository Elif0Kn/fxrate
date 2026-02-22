package com.realtimefxrate.web;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Component
public class RateWebSocketHandler extends TextWebSocketHandler {
    private final Map<WebSocketSession, Set<String>> sessionSubscriptions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        sessionSubscriptions.put(session, new HashSet<>());
        System.out.println("WebSocket connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        sessionSubscriptions.remove(session);
        System.out.println("WebSocket disconnected: " + session.getId());
    }

    public void subscribeToPair(WebSocketSession session, String pair) {
        sessionSubscriptions.get(session).add(pair);
    }

    public void unsubscribeFromPair(WebSocketSession session, String pair) {
        sessionSubscriptions.get(session).remove(pair);
    }

    public void sendRateUpdate(String pair, String rate) {
        if (rate == null) {
            return;
        }
        for (Map.Entry<WebSocketSession, Set<String>> entry : sessionSubscriptions.entrySet()) {
            WebSocketSession session = entry.getKey();
            Set<String> interestedPairs = entry.getValue();
            if (session.isOpen() && interestedPairs.contains(pair)) {
                try {
                    session.sendMessage(new TextMessage(rate));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendAlertUpdate(String alertJson) {
        if (alertJson == null) {
            return;
        }
        for (WebSocketSession session : sessionSubscriptions.keySet()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(alertJson));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
