package com.nabob.conch.sample.guavaevent;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Adam
 * @since 2024/9/2
 */
@Data
@AllArgsConstructor
public class ExecutionEvent1 implements ExecutionEvent {

    private String name;

}
