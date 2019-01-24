package com.example.template;

import lombok.Data;

import java.io.Serializable;

@Data
public class KubePod implements Serializable {

    KubePodId kubePodId;

    String statusType;
    String phase;
    String startTime;
    String updateTime;
    String endTime;

}
