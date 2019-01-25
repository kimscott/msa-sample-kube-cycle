package com.example.template;

import com.example.template.model.KubePod;
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

    /**
     * 특정 파티션을 받겠다고 선언을 안해 놓으면, consumer 수가 늘어날수록, 알아서 partition을 분배하여 간다.
     * @param message
     * @param consumerRecord
     */
//    @KafkaListener(topics = "${topic.kubepod}")
//    public void listenWithOutPartition(@Payload String message, ConsumerRecord<?, ?> consumerRecord) {
////        doDelay();
//        LOG.info("listenWithPartition record='{}' message='{}' partition='{}'", consumerRecord.offset() , message, consumerRecord.partition());
//    }

    /**
     * 특정 클래스로 받으려면 아래와 같이 객체로 변환을 하면 된다.
     * 주의점은 프로듀서쪽의 객체를 잘 마추어야 한다. - 별로 추천하지 않음
     * @param message
     * @param consumerRecord
     */
    @KafkaListener(topics = "${topic.kubepod}")
    public void listenByObject(@Payload String message, ConsumerRecord<?, ?> consumerRecord) {
        System.out.println(message);
        KubePod kubePod = new Gson().fromJson(message, KubePod.class);

        if( "DELETED".equals(kubePod.getStatusType())){
            kubePodService.deletePod(kubePod);
        }else{
            kubePodService.savePodStatus(kubePod);
        }
    }


}
