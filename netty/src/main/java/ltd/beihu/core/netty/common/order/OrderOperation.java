package ltd.beihu.core.netty.common.order;

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
public class OrderOperation extends Operation {

    private int tableId;
    private String dish;

    public OrderOperation(int tableId, String dish) {
        this.tableId = tableId;
        this.dish = dish;
    }

    @Override
    public OperationResult execute() {

        log.info("order`s executing startup with orderRequest: " + toString());

        // execute order logic

        log.info("order`s executing complete");
        return new OrderOperationResult(this.tableId, this.dish, true);
    }
}
