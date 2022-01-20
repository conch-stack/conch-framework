package ltd.beihu.core.netty.common.keepalive;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;
import ltd.beihu.core.netty.common.Operation;
import ltd.beihu.core.netty.common.OperationResult;

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
