package com.cringee.simplescreensharing.serviceTest;

import com.cringee.simplescreensharing.services.SseEmitterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SseEmitterServiceTest {
    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private Logger logger;

    @InjectMocks
    private SseEmitterService sseEmitterService;

    private ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters;

    @BeforeEach
    void setUp() {
        sseEmitters = new ConcurrentHashMap<>();
    }

    @Test
    void testCreateSseEmitter() {
        SseEmitter emitter = sseEmitterService.createSseEmitter(sseEmitters, "testUser");

        assertThat(emitter).isNotNull();
        assertThat(sseEmitters).containsKey("testUser");
        assertThat(sseEmitters.get("testUser")).contains(emitter);
    }

    // TODO
//    @Test
//    void testSseEmitterOnCompletion() {
//        SseEmitter emitter = sseEmitterService.createSseEmitter(sseEmitters, "testUser", logger);
//        emitter.complete();
//
//        assertThat(sseEmitters).doesNotContainKey("testUser");
//    }
//
//    @Test
//    void testSseEmitterOnTimeout() {
//        SseEmitter emitter = sseEmitterService.createSseEmitter(sseEmitters, "testUser", logger);
//        emitter.onTimeout(emitter::complete);
//
//        assertThat(sseEmitters).doesNotContainKey("testUser");
//    }
//
//    @Test
//    void testSseEmitterOnError() {
//        SseEmitter emitter = sseEmitterService.createSseEmitter(sseEmitters, "testUser", logger);
//        emitter.completeWithError(new RuntimeException("Test error"));
//
//        verify(logger).info(eq("SseEmitter got error:"), any(RuntimeException.class));
//        assertThat(sseEmitters).doesNotContainKey("testUser");
//    }

    @Test
    void testBuildData() {
        String templateName = "testTemplate";
        Object data = new Object();
        String dataName = "testData";
        String expectedHtml = "<div>Test HTML</div>";

        when(templateEngine.process(eq(templateName), any(Context.class))).thenReturn(expectedHtml);

        SseEmitter.SseEventBuilder eventBuilder = sseEmitterService.buildData(templateName, data, dataName);

        assertThat(eventBuilder).isNotNull();
        verify(templateEngine).process(eq(templateName), any(Context.class));
    }

    @Test
    void testSendEvent() throws IOException {
        SseEmitter emitter1 = mock(SseEmitter.class);
        SseEmitter emitter2 = mock(SseEmitter.class);
        sseEmitters.put("user1", new CopyOnWriteArrayList<>(List.of(emitter1)));
        sseEmitters.put("user2", new CopyOnWriteArrayList<>(List.of(emitter2)));

        SseEmitter.SseEventBuilder eventBuilder = mock(SseEmitter.SseEventBuilder.class);

        sseEmitterService.sendEvent(sseEmitters, eventBuilder);

        verify(emitter1).send(eventBuilder);
        verify(emitter2).send(eventBuilder);
    }

    @Test
    void testSendEventWithIOException() throws IOException {
        Logger mockLogger = mock(Logger.class);
        ReflectionTestUtils.setField(sseEmitterService, "logger", mockLogger);
        SseEmitter emitter = mock(SseEmitter.class);
        sseEmitters.put("user", new CopyOnWriteArrayList<>(List.of(emitter)));
        SseEmitter.SseEventBuilder eventBuilder = mock(SseEmitter.SseEventBuilder.class);
        doThrow(new IOException("Test IO exception")).when(emitter).send(eventBuilder);
        sseEmitterService.sendEvent(sseEmitters, eventBuilder);
        verify(mockLogger).warn(eq("Failed to send event: {}"), eq("Test IO exception"));
    }
}
