package com.example.template;

import com.example.template.model.InstanceModel;
import com.example.template.model.InstanceState;
import com.example.template.service.InstanceService;
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
    InstanceService instanceService;

    /**
     * 특정 클래스로 받으려면 아래와 같이 객체로 변환을 하면 된다.
     * 주의점은 프로듀서쪽의 객체를 잘 마추어야 한다. - 별로 추천하지 않음
     * @param message
     * @param consumerRecord
     */

    @KafkaListener(topics = "${topic.instanceTopic}")
    public void listenByObject(@Payload String message, ConsumerRecord<?, ?> consumerRecord) {

        InstanceModel im = new Gson().fromJson(message, InstanceModel.class);
        System.out.println(im.toString());
        if( InstanceState.DELETE.equals(im.getInstanceState())){
            instanceService.deleteInstance(im);
            instanceService.updateCache(im);
        }else if( InstanceState.MODIFY.equals(im.getInstanceState())){
            instanceService.updateCache(im);
        }else{
            instanceService.saveInstanceStatus(im);
        }
    }

}
