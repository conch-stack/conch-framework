package com.nabob.conch.netty.common.keepalive;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.nabob.conch.netty.common.OperationResult;

/**
 * @author Adam
 * @since 2022/1/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;
}
