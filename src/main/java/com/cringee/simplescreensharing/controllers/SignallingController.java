package com.cringee.simplescreensharing.controllers;

import com.cringee.simplescreensharing.models.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Controller
public class SignallingController {

    private final Set<String> users = new ConcurrentSkipListSet<>();
    private final SimpMessagingTemplate simpMessagingTemplate;
    Logger logger = LoggerFactory.getLogger(SignallingController.class);

    public SignallingController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = accessor.getUser();
        if (userPrincipal == null) return;
        String sessionId = userPrincipal.getName();
        Data data = new Data(sessionId, null, null, "join");
        users.forEach(id -> simpMessagingTemplate.convertAndSendToUser(id, "/queue/current-users", data));
        users.add(sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal userPrincipal = accessor.getUser();
        if (userPrincipal == null) return;
        String sessionId = userPrincipal.getName();
        if (sessionId != null) {
            users.remove(sessionId);
            Data data = new Data(sessionId, null, null, "leave");
            users.forEach(id -> simpMessagingTemplate.convertAndSendToUser(id, "/queue/current-users", data));
        }
    }

    @MessageMapping("/dataExchange")
    public void handleOffer(Data data, Principal principal) {
        String sessionId = principal.getName();
        String targetId = data.getTargetId();
        if (sessionId == null || targetId == null) return;
        String type = data.getType();
        data.setSenderId(sessionId);
        String dest = null;
        switch (type) {
            case "offer" -> dest = "/queue/offer";
            case "ice" -> dest = "/queue/ice";
            case "answer" -> dest = "/queue/answer";
        }
        if (dest != null)
            simpMessagingTemplate.convertAndSendToUser(targetId, dest, data);
    }

}
