package com.nabob.conch.sample.pipeline.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AppointRequest Holder
 *
 * @author Adam
 * @since 2023/9/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointRequestHolder {

    private String orderNumber;

}
