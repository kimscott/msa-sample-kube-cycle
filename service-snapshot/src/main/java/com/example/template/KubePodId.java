package com.example.template;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class KubePodId implements Serializable {

    /**
     * 2 개를 primary key 로 잡을 경우 mysql 의 기본키는 1000byte 안쪽으로 만들어야 한다는 에러가 발생함.
     * Specified key was too long; max key length is 1000 bytes
     * 그래서 length 를 줄여주었음
     */
    @Column(length = 100)
    String name;

    @Column(length = 100)
    String namespace;

}
