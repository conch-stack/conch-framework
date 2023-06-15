package com.nabob.conch.netty.common.order;

import com.nabob.conch.netty.common.OperationResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Adam
 * @since 2022/1/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderOperationResult extends OperationResult {

    private final int tableId;
    private final String dish;
    private final boolean complete;
}
