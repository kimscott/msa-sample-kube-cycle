package com.example.template.emitter;

import com.example.template.AppEntityBaseMessage;
import com.example.template.AppEntityBaseMessageHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
public class KubeSSEJobService {
    public final ApplicationEventPublisher eventPublisher;

    public KubeSSEJobService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    @Qualifier(value = "messageHandler")
    private AppEntityBaseMessageHandler messageHandler;

    @Async
    @Scheduled(fixedRate = 1000, initialDelay = 1000)
    public void publishJobKubeSse() throws InterruptedException {
        int jobId = AppEntityBaseMessage.getNextJobId();
        if (jobId % 2 == 1) {
            messageHandler.publish("One", "Service", "Pod" + jobId, "Deployment", "ReplicaSet");

        } else if (jobId % 2 == 0) {
            messageHandler.publish("Two", "Service", "Pod" + jobId, "Deployment", "ReplicaSet");
        }
        Thread.sleep(4000);

//        KubeEmitter user_Two = new KubeEmitter("NameSpace : NameSpace"+ jobId,"Service : Test Service ; ", "Pods : Test Pods", "Deployments : Test Deployments", "ReplicaSet : Test ReplicaSet");
//        this.eventPublisher.publishEvent(user_Two);
    }
}
