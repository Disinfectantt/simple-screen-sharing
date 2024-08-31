package com.cringee.simplescreensharing.controllerTest;

import com.cringee.simplescreensharing.controllers.SignallingController;
import com.cringee.simplescreensharing.models.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SignallingControllerTest {
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private SessionConnectEvent connectEvent;

    @Mock
    private SessionDisconnectEvent disconnectEvent;

    @Mock
    private Message<byte[]> message;

    @Mock
    private Principal principal;

    private SignallingController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new SignallingController(simpMessagingTemplate);
    }

    @Test
    void testHandleWebSocketConnectListener() {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put(SimpMessageHeaderAccessor.USER_HEADER, principal);
        MessageHeaders headers = new MessageHeaders(headerMap);

        when(connectEvent.getMessage()).thenReturn(message);
        when(message.getHeaders()).thenReturn(headers);
        when(principal.getName()).thenReturn("user1");

        ConcurrentSkipListSet<String> users = new ConcurrentSkipListSet<>();
        users.add("user");
        setUsersField(controller, users);

        controller.handleWebSocketConnectListener(connectEvent);

        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("user"),
                eq("/queue/current-users"),
                argThat(arg -> {
                    Data d = (Data) arg;
                    return "user1".equals(d.getSenderId()) &&
                            "join".equals(d.getType());
                })
        );
        ConcurrentSkipListSet<String> users2 = getUsersField(controller);
        assertThat(users2).isEqualTo(users);
    }

    @Test
    void testHandleWebSocketDisconnectListener() {
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put(SimpMessageHeaderAccessor.USER_HEADER, principal);
        MessageHeaders headers = new MessageHeaders(headerMap);

        when(disconnectEvent.getMessage()).thenReturn(message);
        when(message.getHeaders()).thenReturn(headers);
        when(principal.getName()).thenReturn("user1");

        ConcurrentSkipListSet<String> users = new ConcurrentSkipListSet<>();
        users.add("user1");
        users.add("user2");
        setUsersField(controller, users);

        controller.handleWebSocketDisconnectListener(disconnectEvent);

        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("user2"),
                eq("/queue/current-users"),
                argThat(arg -> {
                    Data d = (Data) arg;
                    return "user1".equals(d.getSenderId()) &&
                            "leave".equals(d.getType());
                })
        );
        ConcurrentSkipListSet<String> users2 = getUsersField(controller);
        assertThat(users2).doesNotContain("user1");
    }

    @Test
    void testHandleOffer() {
        Data data = new Data(null, "user2", null, "offer");
        when(principal.getName()).thenReturn("user1");

        controller.handleOffer(data, principal);

        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("user2"),
                eq("/queue/offer"),
                argThat(arg -> {
                    Data d = (Data) arg;
                    return "user2".equals(d.getTargetId()) &&
                            "user1".equals(d.getSenderId()) &&
                            "offer".equals(d.getType());
                })
        );
    }

    @Test
    void testHandleIce() {
        Data data = new Data(null, "user2", null, "ice");
        when(principal.getName()).thenReturn("user1");

        controller.handleOffer(data, principal);

        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("user2"),
                eq("/queue/ice"),
                argThat(arg -> {
                    Data d = (Data) arg;
                    return "user2".equals(d.getTargetId()) &&
                            "user1".equals(d.getSenderId()) &&
                            "ice".equals(d.getType());
                })
        );
    }

    @Test
    void testHandleAnswer() {
        Data data = new Data(null, "user2", null, "answer");
        when(principal.getName()).thenReturn("user1");

        controller.handleOffer(data, principal);

        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("user2"),
                eq("/queue/answer"),
                argThat(arg -> {
                    Data d = (Data) arg;
                    return "user2".equals(d.getTargetId()) &&
                            "user1".equals(d.getSenderId()) &&
                            "answer".equals(d.getType());
                })
        );
    }

    private void setUsersField(SignallingController controller, ConcurrentSkipListSet<String> users) {
        try {
            java.lang.reflect.Field usersField = SignallingController.class.getDeclaredField("users");
            usersField.setAccessible(true);
            usersField.set(controller, users);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set users field", e);
        }
    }

    private ConcurrentSkipListSet<String> getUsersField(SignallingController controller) {
        try {
            java.lang.reflect.Field usersField = SignallingController.class.getDeclaredField("users");
            usersField.setAccessible(true);
            return (ConcurrentSkipListSet<String>) usersField.get(controller);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get users field", e);
        }
    }
}
