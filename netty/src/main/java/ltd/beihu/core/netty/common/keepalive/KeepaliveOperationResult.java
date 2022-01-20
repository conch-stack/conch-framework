package ltd.beihu.core.netty.common.keepalive;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ltd.beihu.core.netty.common.OperationResult;

/**
 * @author Adam
 * @since 2022/1/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KeepaliveOperationResult extends OperationResult {

    private final long time;
}
