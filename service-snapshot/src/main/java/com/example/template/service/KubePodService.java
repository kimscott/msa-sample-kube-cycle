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

@Service
public class KubePodService {

    private static final Logger LOG = LoggerFactory.getLogger(KubePodService.class);

    @Autowired
    KubePodRepository kubePodRepository;

    @CachePut(value="pod" ,key="#pod.kubePodId.namespace + #pod.kubePodId.name")
    public KubePod savePodStatus(KubePod pod){
//        LOG.info("pod.namespace='{}' pod.name='{}' " , pod.getKubePodId().getNamespace() , pod.getKubePodId().getName() );
        return kubePodRepository.save(pod);
    }

    @CacheEvict(value="pod" ,key="#pod.kubePodId.namespace + #pod.kubePodId.name")
    public void deletePod(KubePod pod){
//        LOG.info("pod.namespace='{}' pod.name='{}' " , pod.getKubePodId().getNamespace() , pod.getKubePodId().getName() );
        kubePodRepository.deleteById(pod.getKubePodId());
    }

}
