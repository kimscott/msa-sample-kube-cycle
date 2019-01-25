package com.example.template;

import com.example.template.model.KubePod;
import com.example.template.model.KubePodId;
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
    private AppEntityBaseMessageHandler messageHandler;

    @KafkaListener(topics = "${topic.kubepod}")
    public void listenByObject(@Payload String message, ConsumerRecord<?, ?> consumerRecord) {
        System.out.println(message);
        Gson gson = new Gson();
        KubePod kubePod = gson.fromJson(message, KubePod.class);
//        System.out.println(gson.toJson(kubePod));

//        System.out.println(gson.toJson((kubePod.getKubePodId())));
        KubePodId kubePodId = gson.fromJson(gson.toJson((kubePod.getKubePodId())), KubePodId.class);
//        System.out.println(kubePodId.getNamespace());
        messageHandler.publish(kubePodId.getNamespace(), gson.toJson(kubePod));
    }
}
