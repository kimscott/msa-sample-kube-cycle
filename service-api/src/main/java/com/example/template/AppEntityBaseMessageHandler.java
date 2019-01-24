package com.example.template;

import com.example.template.emitter.SSERestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component("messageHandler")
public class AppEntityBaseMessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppEntityBaseMessageHandler.class);

    @Autowired
    private SSERestController sseController;
    private ApplicationEventPublisher eventPublisher;
    public AppEntityBaseMessageHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Async
    public void publish(String id, String jsonData) {
        try {

            AppEntityBaseMessage message = new AppEntityBaseMessage();
            message.setId("data");
            message.setMessage(jsonData);

            this.eventPublisher.publishEvent(message);

        } catch (Exception ex) {
            LOGGER.error("Publish Error");
        }
    }
}
