package com.nabob.conch.sample.pipeline.test;

/**
 * @author Adam
 * @since 2023/9/18
 */
public class AppointPipelineTest {

    public static void main(String[] args) {
        AppointPipeline appointPipeline = new AppointPipeline();

        AppointRequestHolder appointRequestHolder = new AppointRequestHolder();

        Boolean invoke = appointPipeline.invoke(appointRequestHolder);
        System.out.println(invoke);
    }

}
