package com.example.template.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "KUBE_POD")
public class KubePod implements Serializable {

    @EmbeddedId
    KubePodId kubePodId;

    String statusType;
    String phase;
    String startTime;
    String updateTime;
    String endTime;

}
