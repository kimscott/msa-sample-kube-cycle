package com.example.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class KubePodService {

    private static final Logger LOG = LoggerFactory.getLogger(KubePodService.class);

    @Autowired
    KubePodRepository kubePodRepository;

    @CachePut(value="pod" ,key="#pod.kubePodId.namespace + #pod.kubePodId.name")
    public KubePod savePodStatus(KubePod pod){
        LOG.info("pod.namespace='{}' pod.name='{}' " , pod.kubePodId.namespace , pod.kubePodId.name );
        return kubePodRepository.save(pod);
    }

    @Cacheable(value="pod" ,key="#pod.kubePodId.namespace + #pod.kubePodId.name")
    public KubePod checkPodStatus(KubePod pod){
        return kubePodRepository.findById(pod.getKubePodId()).orElse(new KubePod());
    }
}
