package com.example.template;

import lombok.Data;

import java.io.Serializable;

@Data
public class KubePodId implements Serializable {

    String name;
    String namespace;

}
