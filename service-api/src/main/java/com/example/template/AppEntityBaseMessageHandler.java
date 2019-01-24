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
    public void subscribe() {
        try {
//            String str = stringRedisTemplate.getStringSerializer().deserialize(message.getBody());
//            AppEntityBaseMessage appEntityBaseMessage =
//                    JsonUtils.convertValue(JsonUtils.unmarshal(str), AppEntityBaseMessage.class);
//
//            //If body is null, set appEntity as body.
//            if (appEntityBaseMessage.getBody() == null) {
//                appEntityBaseMessage.setBody(appEntityBaseMessage.getAppEntity());
//            }

//            sseController.onKubeSse(appEntityBaseMessage);

//            LOGGER.info("subscribe from redis , {} , {}",
//                    appEntityBaseMessage.getTopic().toString(),
//                    appEntityBaseMessage.getAppEntity() == null ? null : appEntityBaseMessage.getAppEntity().getName());
        } catch (Exception ex) {
//            LOGGER.error("subscribe from redis failed");
        }
    }
    @Async
    public void publish(String nameSpace, String service, String pod, String deployment, String replicaSet) {
        try {
//            LOGGER.info("publish message");

            AppEntityBaseMessage message = new AppEntityBaseMessage();
            message.setNameSpace(nameSpace);
            message.setService(service);
            message.setPod(pod);
            message.setDeployment(deployment);
            message.setReplicaSet(replicaSet);

            this.eventPublisher.publishEvent(message);

        } catch (Exception ex) {
//            LOGGER.error("publish to redis failed, {} , {}", appEntity == null ? null : appEntity.getName());
        }
    }
}
