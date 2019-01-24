package com.example.template;

import lombok.Data;

import java.util.List;

@Data
public class KubeNamespace {
    String name;
    String selfLink;
    List<KubePod> podList;
}
