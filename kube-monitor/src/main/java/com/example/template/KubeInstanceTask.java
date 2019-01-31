package com.example.template;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class KubeInstanceTask implements InitializingBean {

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

    private String host;
    private String token;
    private ApiClient client;


    @Override
    public void afterPropertiesSet() throws Exception {
        this.host = environment.getProperty("kube.host");
        this.token = environment.getProperty("kube.token");

        // kubenate Configuration setting
        ApiClient client = Config.fromToken(this.getHost(), this.getToken(), false);
        this.client = client;
        client.getHttpClient().setReadTimeout(10000, TimeUnit.MINUTES);
        Configuration.setDefaultApiClient(client);
    }

    public String getHost(){
        return this.host;
    }

    public String getToken(){
        return this.token;
    }



    @Scheduled(fixedRate = 10000)
    public void watchPod() throws IOException, ApiException{
        CoreV1Api api = new CoreV1Api();
        Watch<V1Pod> watch =
                Watch.createWatch(
                        client,
                        api.listPodForAllNamespacesCall(
                                null, null, null, null, null, null, null, null, Boolean.TRUE, null, null),
                        new TypeToken<Watch.Response<V1Pod>>() {}.getType());

        try {
            for (Watch.Response<V1Pod> item : watch) {
                Date createDate =  item.object.getStatus().getStartTime() != null ? item.object.getStatus().getStartTime().toDate() : null;
                String dateStr = "";
                if( createDate != null ){
                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateStr = transFormat.format(createDate);
                }
                InstanceModel im = new InstanceModel();
                im.setId(item.object.getMetadata().getName());
                im.setProvider("K8S");
                im.setName(item.object.getMetadata().getNamespace());
                im.setStatus(item.object.getStatus().getPhase());
                im.setType(item.object.getKind());
                im.setCreateDate(dateStr);

                JSONObject properties = new JSONObject();
                properties.put("apiVersion", item.object.getApiVersion());
                properties.put("image", item.object.getSpec().getContainers() != null ? item.object.getSpec().getContainers().get(0).getImage() : "");

                im.setProperties(properties.toJSONString());

                if(item.type.equals("DELETED")){
                    im.setInstanceState(InstanceState.DELETE);
                }else if(item.type.equals("MODIFIED")) {
                    im.setInstanceState(InstanceState.MODIFY);
                }
//                System.out.println("Message: " + item.type + " sent to topic: " + item.object.getMetadata().getName());
                kafkaTemplate.send(new ProducerRecord<String, InstanceModel>(instanceTopic, item.object.getMetadata().getNamespace() , im));

                System.out.printf("%s : %s %n" , item.type, im.toString() );
            }
        } finally {
            watch.close();
        }
    }

    /* old version
    @Scheduled(fixedRate = 10000)
    public void watchPod() throws IOException, ApiException{
        CoreV1Api api = new CoreV1Api();
        Watch<V1Pod> watch =
                Watch.createWatch(
                        client,
                        api.listPodForAllNamespacesCall(
                                null, null, null, null, null, null, null, null, Boolean.TRUE, null, null),
                        new TypeToken<Watch.Response<V1Pod>>() {}.getType());

        try {
            for (Watch.Response<V1Pod> item : watch) {
//                System.out.printf("%s : %s , %s %n" , item.object.getMetadata().getName(), item.type, item.object.getStatus().getPhase() );

                KubePod kp = new KubePod();

                KubePodId kpid = new KubePodId();
                kpid.setName(item.object.getMetadata().getName());
                kpid.setNamespace(item.object.getMetadata().getNamespace());
                kp.setKubePodId(kpid);
                kp.setStatusType(item.type);
                kp.setPhase(item.object.getStatus().getPhase());
                kp.setStartTime(item.object.getStatus().getStartTime() != null ? item.object.getStatus().getStartTime().toString() : null);


                if( item.type.equals("MODIFIED")){
                    kp.setUpdateTime(new DateTime().toString());
                }
                if( item.type.equals("DELETED")){
                    kp.setEndTime(new DateTime().toString());
                }

//                System.out.println("Message: " + item.type + " sent to topic: " + item.object.getMetadata().getName());
                System.out.println("Message: " + kp.toString() + " sent to topic: " + kubepod);
                kafkaTemplate.send(new ProducerRecord<String, KubePod>(kubepod, kp.getKubePodId().getNamespace() , kp));

//                System.out.printf("%s : %s %n" , item.type, kp.toString() );
            }
        } finally {
            watch.close();
        }
    }
    */

//    @Scheduled(fixedRate = 10000)
    public void watchService() throws IOException, ApiException{
        CoreV1Api api = new CoreV1Api();
        Watch<V1Service> watch =
                Watch.createWatch(
                        client,
                        api.listServiceForAllNamespacesCall(
                                null, null, null, null, null, null, null, null, Boolean.TRUE, null, null),
                        new TypeToken<Watch.Response<V1Service>>() {}.getType());

        try {
            for (Watch.Response<V1Service> item : watch) {
//                System.out.printf("%s : %s , %s %n" , item.object.getMetadata().getName(), item.type, item.object.getStatus() );
                KubeService ks = new KubeService();

                KubePodId kpid = new KubePodId();
                kpid.setName(item.object.getMetadata().getName());
                kpid.setNamespace(item.object.getMetadata().getNamespace());
                ks.setKubePodId(kpid);
                ks.setStatusType(item.type);
                ks.setServiceType(item.object.getSpec().getType());
                ks.setClusterIp(item.object.getSpec().getClusterIP());
                ks.setExternalIp(item.object.getStatus().getLoadBalancer().getIngress() != null ?  item.object.getStatus().getLoadBalancer().getIngress().get(0).getHostname() : "<none>");

                ks.setStartTime(item.object.getMetadata().getCreationTimestamp().toString());

                List<V1ServicePort> portList = item.object.getSpec().getPorts();
                String ports = "";
                for(V1ServicePort servicePort : portList){
                    // TODO 여러개가 있으면 좀 다르겠지만 일단 하나만 있다고 가정하고 그냥 만들었음
                    ports = servicePort.getPort() + (servicePort.getNodePort() != null ? ":"+servicePort.getNodePort() : "") + "/" + servicePort.getProtocol();
                }

                ks.setPorts(ports);

            }
        } finally {
            watch.close();
        }
    }


    /**
     * 단순 조회로는 pod 가 삭제되고 변경되는 사항을 알수가 없다.
     * 결국 실시간 상태 변화는 watch 로 구현해야함

//    @Scheduled(fixedRate = 2000)
    public void schedule(){
        ArrayList<KubeNamespace> namespaceList = this.getNamespace();
//        System.out.println(namespaceList.toString());

        // 사실.. pod 조회시 바로 카프카로 날리는게 좋지만.. 조작할수 있으니 여기서 메세지를 날림
        namespaceList.forEach((item) -> {
            List<KubePod> podList = item.getPodList();
            podList.forEach((pod) -> {
                System.out.println("Message: " + pod.toString() + " sent to topic: " + kubepod);
                kafkaTemplate.send(new ProducerRecord<String, KubePod>(kubepod, pod.getKubePodId().getNamespace() , pod));
            });

        });

    }

    public ArrayList<KubeNamespace> getNamespace(){
        ArrayList<KubeNamespace> list = new ArrayList<KubeNamespace>();

        CoreV1Api api = new CoreV1Api();
        try {
            V1NamespaceList lists = api.listNamespace(false,null,null,null,null,null,null,300,false);
            List<V1Namespace> namespaceList =  lists.getItems();
            namespaceList.forEach((item) -> {
                KubeNamespace kn =  new KubeNamespace();
                kn.setName(item.getMetadata().getName());
                kn.setSelfLink(item.getMetadata().getSelfLink());
                kn.setPodList(this.getPodByNamespace(item));
                list.add(kn);
            });
        } catch (ApiException e) {
            e.printStackTrace();
        }


        return list;
    }


    public ArrayList<KubePod> getPodByNamespace(V1Namespace namespace){
        ArrayList<KubePod> list = new ArrayList<KubePod>();
        // api call
        CoreV1Api api = new CoreV1Api();
        try {
            V1PodList lists = api.listNamespacedPod(namespace.getMetadata().getName() , false,null,null,null,null,null,null,null,null);
            List<V1Pod> podList =  lists.getItems();
            podList.forEach((item) -> {
                KubePod kp = new KubePod();

                KubePodId kpid = new KubePodId();
                kpid.setName(item.getMetadata().getName());
                kpid.setNamespace(namespace.getMetadata().getName());
                kp.setKubePodId(kpid);
                kp.setPhase(item.getStatus().getPhase());
                kp.setStartTime(item.getStatus().getStartTime().toString());

                list.add(kp);
            });
        } catch (ApiException e) {
            e.printStackTrace();
        }


        return list;
    }
     */
}
