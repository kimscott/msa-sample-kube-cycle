package com.example.template;

import com.example.template.model.KubePod;
import com.example.template.model.KubePodId;
import com.example.template.service.KubePodService;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;



@Service
public class KafkaReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaReceiver.class);

    @Autowired
    KubePodService kubePodService;

    @Autowired
    private AppEntityBaseMessageHandler messageHandler;

    @KafkaListener(topics = "${topic.kubepod}")
    public void listenByObject(@Payload String message, ConsumerRecord<?, ?> consumerRecord) {
        System.out.println(message);
        Gson gson = new Gson();
        KubePod kubePod = gson.fromJson(message, KubePod.class);
        kubePodService.update(kubePod);
        kubePodService.deleteCacheList(kubePod);
        messageHandler.publish(kubePod.getKubePodId().getNamespace(), gson.toJson(kubePod));
    }
}
