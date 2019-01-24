package com.example.template.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface KubePodRepository extends CrudRepository<KubePod, KubePodId> {

    Iterable<KubePod> findByKubePodIdNamespace(@Param("namespace") String namespace);

}
