package com.metacoding.gateway.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class WebSocketProxyConfig extends TextWebSocketHandler {

    private final URI backendUri;
    private final StandardWebSocketClient client = new StandardWebSocketClient();
    private final Map<String, WebSocketSession> backendByClientId = new ConcurrentHashMap<>();

    // 클라이언트 연결 시 Order 서비스와도 연결 생성
    @Override
    public void afterConnectionEstablished(WebSocketSession s) throws Exception {
        WebSocketSession backend = client.doHandshake(new TextWebSocketHandler() {
            // Order 서비스 → 클라이언트 메시지 전달
            @Override
            protected void handleTextMessage(WebSocketSession bs, TextMessage msg) throws Exception {
                if (s.isOpen()) s.sendMessage(msg);
            }
            // Order 서비스 연결 종료 시 클라이언트 연결도 종료
            @Override
            public void afterConnectionClosed(WebSocketSession bs, CloseStatus cs) throws Exception {
                if (s.isOpen()) s.close(cs);
            }
        }, null, backendUri).get();

        backendByClientId.put(s.getId(), backend);
    }

    // 클라이언트 → Order 서비스 메시지 전달
    @Override
    protected void handleTextMessage(WebSocketSession s, TextMessage msg) throws Exception {
        WebSocketSession backend = backendByClientId.get(s.getId());
        if (backend != null && backend.isOpen()) backend.sendMessage(msg);
    }

    // 클라이언트 연결 종료 시 백엔드 연결도 종료
    @Override
    public void afterConnectionClosed(WebSocketSession s, CloseStatus cs) throws Exception {
        WebSocketSession backend = backendByClientId.remove(s.getId());
        if (backend != null && backend.isOpen()) backend.close(cs);
    }
}
