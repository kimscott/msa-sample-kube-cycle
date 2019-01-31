package com.example.template;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@EnableScheduling
public class AwsInstanceTask implements InitializingBean {

    @Autowired
    Environment environment;

    @Value("${topic.kubepod}")
    private String kubepod;

    @Value("${topic.instanceTopic}")
    private String instanceTopic;

    @Value("${producerReplicas}")
    private String producerReplicas;

    @Value("${producerId}")
    private String producerId;

    @Autowired
    KafkaTemplate kafkaTemplate;

    private String accesskey;
    private String secretkey;

    AmazonEC2 ec2Client;

    Map<String, String> preState;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.accesskey = environment.getProperty("aws.accesskey");
        this.secretkey = environment.getProperty("aws.secretkey");

        AWSCredentials credentials = new BasicAWSCredentials(
                this.accesskey,
                this.secretkey
        );

        AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build();
        this.ec2Client = ec2Client;

        this.preState = new HashMap<>();

    }

    public String getHost(){
        return this.accesskey;
    }

    public String getToken(){
        return this.secretkey;
    }



    @Scheduled(fixedRate = 1000)
    public void watchAwsInstance() throws IOException{

        boolean done = false;

        while(!done) {
            DescribeInstancesRequest request = new DescribeInstancesRequest();
            DescribeInstancesResult response = ec2Client.describeInstances(request);

            for (Reservation reservation : response.getReservations()) {
                for (Instance instance : reservation.getInstances()) {
                    // 기존 상태값과 비교하여 변경된 내용만 보낸다.
                    String instanceId = instance.getInstanceId();
                    String state = instance.getState().getName();
                    if( !(this.preState.containsKey(instanceId) && this.preState.get(instanceId).equals(state))){
                        System.out.println(instance);
                        this.preState.put(instanceId, state);
                        String name = "";
                        try{
                            name = instance.getTags() != null ? instance.getTags().get(0).getValue() : "";
                        }catch (Exception e){}
                        Date createDate =  instance.getLaunchTime();
                        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateStr = transFormat.format(createDate);
                        InstanceModel im = new InstanceModel();
                        im.setId(instanceId);
                        im.setProvider("EC2");
                        im.setName(name);
                        im.setStatus(state);
                        im.setType(instance.getInstanceType());
                        im.setCreateDate(dateStr);

                        JSONObject properties = new JSONObject();
                        properties.put("PublicDnsName", instance.getPublicDnsName());
                        properties.put("PrivateDnsName", instance.getPrivateDnsName());
                        im.setProperties(properties.toJSONString());

                        if(state.equals("DELETED")){
                            im.setInstanceState(InstanceState.DELETE);
                        }else if(state.equals("MODIFIED")) {
                            im.setInstanceState(InstanceState.MODIFY);
                        }

                        kafkaTemplate.send(new ProducerRecord<String, InstanceModel>(instanceTopic, instanceId , im));
                    }
                }
            }
            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
//                System.out.println(" ========= finish ========= ");
                done = true;
            }
        }

    }
}
