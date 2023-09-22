package com.nabob.conch.sample.pipeline.test;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Adam
 * @since 2023/9/18
 */
@Component
public class AppointPipelineTest {

    @Resource
    private AppointPipeline appointPipeline;

    @PostConstruct
    void test() {
        AppointRequestHolder appointRequestHolder = new AppointRequestHolder();
        Boolean invoke = appointPipeline.invoke(appointRequestHolder);
        System.out.println(invoke);
    }

    public static void main(String[] args) {
        AppointPipeline appointPipeline = new AppointPipeline();

        AppointRequestHolder appointRequestHolder = new AppointRequestHolder();

        Boolean invoke = appointPipeline.invoke(appointRequestHolder);
        System.out.println(invoke);
    }

}
