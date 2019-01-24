package com.example.template.service;

import com.example.template.model.KubePod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/kube")
public class KubeController {

    @Autowired
    KubePodService kubePodService;

    @RequestMapping(value = "/pod", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public List<KubePod> getAllPod(HttpServletRequest request,
                          HttpServletResponse response
    ) throws Exception {

        List<KubePod> list = new ArrayList<KubePod>();
        Iterable<KubePod> it = kubePodService.getAllPod();
        for (KubePod item : it) {
            list.add(item);
//            System.out.println(item.getKubePodId().getName());
        }

        return list;
    }

    @RequestMapping(value = "/pod/{namespace}", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public List<KubePod> getAllPodByNameSapce(HttpServletRequest request,
                          HttpServletResponse response,
                          @PathVariable(value = "namespace", required=false) String namespace
    ) throws Exception {
        List<KubePod> list = new ArrayList<KubePod>();
        Iterable<KubePod> it = kubePodService.getAllPodByNameSapce(namespace);
        for (KubePod item : it) {
            list.add(item);
//            System.out.println(item.getKubePodId().getName());
        }

        return list;
    }

}
