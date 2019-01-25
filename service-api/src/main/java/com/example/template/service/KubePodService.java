package com.example.template.service;

import com.example.template.model.KubePod;
import com.example.template.model.KubePodRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KubePodService {

    private static final Logger LOG = LoggerFactory.getLogger(KubePodService.class);

    @Autowired
    KubePodRepository kubePodRepository;

    @Cacheable(value="pod" ,key="#pod.kubePodId.namespace + #pod.kubePodId.name")
    public KubePod checkPodStatus(KubePod pod){
        return kubePodRepository.findById(pod.getKubePodId()).orElse(new KubePod());
    }

//    @Cacheable(value="pod")
    public Iterable<KubePod> getAllPod(){
        LOG.info("use getAllPod database");
        return kubePodRepository.findAll();
    }

//    @Cacheable(value="pod", key="#namespace")
    public Iterable<KubePod> getAllPodByNameSapce(String namespace){
        LOG.info("use getAllPodByNameSapce database");
        return kubePodRepository.findByKubePodIdNamespace(namespace);
    }

    @CacheEvict(value="pod")
    public String update(String name) {
        LOG.info("cache update .. {}", name);
        return name;
    }
}
