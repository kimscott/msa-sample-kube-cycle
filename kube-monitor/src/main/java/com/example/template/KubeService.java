package com.example.template;

import lombok.Data;

import java.io.Serializable;

@Data
public class KubeService  implements Serializable {

    KubePodId kubePodId;

    String statusType;
    String serviceType;
    String clusterIp;
    String externalIp;
    String ports;

    String startTime;
    String updateTime;
    String endTime;
}
