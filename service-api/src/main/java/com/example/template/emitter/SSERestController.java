package com.example.template.emitter;

import com.example.template.AppEntityBaseMessage;
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

    @GetMapping("/{nameSpace}")
    public SseEmitter getNewKube(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @PathVariable(value = "nameSpace") String nameSpace
    ) {
        KubeEmitter emitter = new KubeEmitter(nameSpace);
        userBaseEmitters.add(emitter);
        this.nameSpace = nameSpace;

        emitter.onCompletion(() -> this.userBaseEmitters.remove(emitter));
        emitter.onTimeout(() -> {

            this.userBaseEmitters.remove(emitter);
        });

        return emitter;
    }

    @EventListener
    public void onKubeSse(AppEntityBaseMessage appEntityBaseMessage) {

        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.userBaseEmitters.forEach(emitter -> {
            try {
                /*
                    todo : nameSpace 조건 부분
                 */
//                if(appEntityBaseMessage.getNameSpace().equals(nameSpace)) {
                    emitter.send(appEntityBaseMessage);
//                }
            } catch (Exception e) {
                LOGGER.info("dead");
                deadEmitters.add(emitter);
            }
        });
        this.userBaseEmitters.remove(deadEmitters);
    }
}
