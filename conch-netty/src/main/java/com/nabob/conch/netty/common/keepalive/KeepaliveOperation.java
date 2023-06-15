package com.nabob.conch.netty.common.keepalive;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;
import com.nabob.conch.netty.common.Operation;
import com.nabob.conch.netty.common.OperationResult;

/**
 * @author Adam
 * @since 2022/1/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Log
public class KeepaliveOperation extends Operation {

    private long time;

    public KeepaliveOperation() {
        this.time = System.currentTimeMillis();
    }

    @Override
    public OperationResult execute() {
        return new KeepaliveOperationResult(this.time);
    }
}
