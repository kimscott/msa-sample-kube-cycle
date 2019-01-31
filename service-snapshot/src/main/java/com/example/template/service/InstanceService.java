package com.example.template.service;

import com.example.template.model.InstanceModel;
import com.example.template.model.InstanceModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class InstanceService {

    private static final Logger LOG = LoggerFactory.getLogger(InstanceService.class);

    @Autowired
    InstanceModelRepository instanceModelRepository;

    @CachePut(value="instance" ,key="#instance.id")
    public InstanceModel saveInstanceStatus(InstanceModel instance){
        return instanceModelRepository.save(instance);
    }


    @CacheEvict(value = "instance", key="#instance.id")
    public void deleteInstance(InstanceModel instance){
        instanceModelRepository.deleteById(instance.getId());
    }

    /**
     * 데이터가 지워지거나 업데이트 되었을때, 관련된 캐쉬를 모두 업데이트 해줘야한다.
     * 그러나 리스트 관련한 것들은 업데이트가 불가능하니, 모든 케쉬를 지운다
     */
    @Caching(evict = {
            @CacheEvict(value = "instance"),
            @CacheEvict(value = "instance", key="#instance.id"),
            @CacheEvict(value = "instance", key ="#instance.provider"),
    })
    public void updateCache(){

    }

}
