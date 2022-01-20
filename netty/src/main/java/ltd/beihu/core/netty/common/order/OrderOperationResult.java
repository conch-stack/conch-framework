package ltd.beihu.core.netty.common.order;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ltd.beihu.core.netty.common.OperationResult;

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
