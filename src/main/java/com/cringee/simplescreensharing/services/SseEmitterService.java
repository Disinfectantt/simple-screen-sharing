package com.cringee.simplescreensharing.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseEmitterService {
    private final SpringTemplateEngine templateEngine;
    Logger logger = LoggerFactory.getLogger(SseEmitterService.class);

    public SseEmitterService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public SseEmitter createSseEmitter(ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters, String username) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitters.putIfAbsent(username, new CopyOnWriteArrayList<>());
        sseEmitters.get(username).add(sseEmitter);
        sseEmitter.onCompletion(() -> removeEmitter(sseEmitters, sseEmitter, username, null));
        sseEmitter.onTimeout(() -> {
            removeEmitter(sseEmitters, sseEmitter, username, null);
            sseEmitter.complete();
        });
        sseEmitter.onError((e) -> removeEmitter(sseEmitters, sseEmitter, username, e));
        return sseEmitter;
    }

    public SseEmitter.SseEventBuilder buildData(String templateName, Object data, String dataName) {
        Context context = new Context();
        context.setVariable(dataName, data);
        String html = templateEngine.process(templateName, context);
        SseEmitter.SseEventBuilder event = SseEmitter.event();
        event.data(html);
        return event;
    }

    public void sendEvent(ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters, SseEmitter.SseEventBuilder event) {
        // TODO UNHANDLED EXCEPTIONS ????
        sseEmitters.forEach((key, emitters) -> emitters.forEach(emitter -> {
            try {
                emitter.send(event);
            } catch (IOException e) {
                logger.warn("Failed to send event: {}", e.getMessage());
            }
        }));
    }

    protected void removeEmitter(ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters,
                                 SseEmitter sseEmitter,
                                 String username,
                                 Throwable e) {
        sseEmitters.get(username).remove(sseEmitter);
        if (sseEmitters.get(username).isEmpty()) {
            sseEmitters.remove(username);
        }
        if (e != null)
            logger.warn("SseEmitter got error: {}", e.getMessage());
    }
}
