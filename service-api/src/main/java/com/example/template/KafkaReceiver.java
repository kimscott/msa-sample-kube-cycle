package com.example.template;

import com.example.template.model.KubePod;
import com.google.gson.Gson;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;



@Service
public class KafkaReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaReceiver.class);

    @KafkaListener(topics = "${topic.kubepod}")
    public void listenByObject(@Payload String message, ConsumerRecord<?, ?> consumerRecord) {
        System.out.println(message);
        KubePod kubePod = new Gson().fromJson(message, KubePod.class);
    }
}
