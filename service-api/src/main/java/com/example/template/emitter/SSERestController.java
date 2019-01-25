package com.example.template.emitter;

import com.example.template.AppEntityBaseMessage;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@CrossOrigin("*")
@Controller
@RequestMapping("/kubesse")
public class SSERestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SSERestController.class);

    private final CopyOnWriteArrayList<KubeEmitter> userBaseEmitters = new CopyOnWriteArrayList<>();
    public String nameSpace = null;

    @GetMapping("/")
    public SseEmitter getNewKube(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @RequestParam(value = "nameSpace", required = false) String nameSpace
    ) {
        KubeEmitter emitter = new KubeEmitter(nameSpace);
        userBaseEmitters.add(emitter);
        System.out.println("before: " + this.userBaseEmitters.size());
//        this.nameSpace = nameSpace;

        emitter.onCompletion(() -> this.userBaseEmitters.remove(emitter));
        emitter.onTimeout(() -> {
            this.userBaseEmitters.remove(emitter);
        });

        return emitter;
    }

    @EventListener
    public void onKubeSse(AppEntityBaseMessage appEntityBaseMessage) {
        LOGGER.info("appEntityBaseMessage");
        System.out.println(this.userBaseEmitters.size());
//        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.userBaseEmitters.forEach(emitter -> {
            try {
                /*
                    todo : nameSpace 조건 부분
                 */
                LOGGER.info("appEntityBaseMessage");
                if(emitter.getNameSpace() == null) {
                    emitter.send(appEntityBaseMessage);
                } else if(appEntityBaseMessage.getNamespace().equals(emitter.getNameSpace())) {
                    emitter.send(appEntityBaseMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.info("dead");
                this.userBaseEmitters.remove(emitter);
            }
        });
//        this.userBaseEmitters.remove(deadEmitters);
    }
}
