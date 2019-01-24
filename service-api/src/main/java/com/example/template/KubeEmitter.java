package com.example.template;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class KubeEmitter extends SseEmitter {

    private String nameSpace;

    public KubeEmitter(String nameSpace) {
        super();
        this.nameSpace = nameSpace;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }
}
