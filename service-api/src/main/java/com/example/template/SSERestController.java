package com.example.template;

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
    public Integer userNumber = null;

    //test
    HashMap<String,KubeEmitter> map = new HashMap<>();

    @GetMapping("/{nameSpace}")
    public SseEmitter getNewKube(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @PathVariable(value = "nameSpace") String nameSpace
    ) {
        KubeEmitter emitter = new KubeEmitter(nameSpace);
        userBaseEmitters.add(emitter);
        this.nameSpace = nameSpace;
        //test
        map.put(nameSpace,emitter);

        emitter.onCompletion(() -> this.userBaseEmitters.remove(emitter));
        emitter.onTimeout(() -> {

            this.userBaseEmitters.remove(emitter);
        });

        return emitter;
    }

    @EventListener
    public void onKubeSse(AppEntityBaseMessage appEntityBaseMessage) {
//        LOGGER.info(Boolean.toString(appEntityBaseMessage.getNameSpace().equals(nameSpace)));
//        LOGGER.info(appEntityBaseMessage.getNameSpace());
//        LOGGER.info(nameSpace);
        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.userBaseEmitters.forEach(emitter -> {
            try {
                //test
               map.get(appEntityBaseMessage.getNameSpace()).send(appEntityBaseMessage);

                    //emitter.send(appEntityBaseMessage);
                /*
                if(appEntityBaseMessage.getNameSpace().equals(nameSpace)) {
                    emitter.send(appEntityBaseMessage);
                }*/
            } catch (Exception e) {
                LOGGER.info("dead");
                deadEmitters.add(emitter);
            }
        });
        this.userBaseEmitters.remove(deadEmitters);
    }
}
