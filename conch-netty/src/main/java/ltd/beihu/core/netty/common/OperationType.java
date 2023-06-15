package ltd.beihu.core.netty.common;

import ltd.beihu.core.netty.common.auth.AuthOperation;
import ltd.beihu.core.netty.common.auth.AuthOperationResult;
import ltd.beihu.core.netty.common.keepalive.KeepaliveOperation;
import ltd.beihu.core.netty.common.keepalive.KeepaliveOperationResult;
import ltd.beihu.core.netty.common.order.OrderOperation;
import ltd.beihu.core.netty.common.order.OrderOperationResult;

import java.util.Arrays;
import java.util.Objects;

/**
 * OperationType
 *
 * @author Adam
 * @since 2022/1/20
 */
public enum OperationType {

    AUTH(1, AuthOperation.class, AuthOperationResult.class),
    KEEPALIVE(2, KeepaliveOperation.class, KeepaliveOperationResult.class),
    ORDER(3, OrderOperation.class, OrderOperationResult.class);

    private int opCode;

    private Class<? extends Operation> operationClazz;

    private Class<? extends OperationResult> operationResultClass;

    OperationType(int opCode, Class<? extends Operation> operationClazz, Class<? extends OperationResult> operationResultClass) {
        this.opCode = opCode;
        this.operationClazz = operationClazz;
        this.operationResultClass = operationResultClass;
    }

    public static OperationType fromOpCode(int opCode) {
        return Arrays.stream(OperationType.values()).filter(target -> Objects.equals(opCode, target.getOpCode())).findFirst().orElse(null);
    }

    public static OperationType fromOperation(Operation operation) {
        return Arrays.stream(OperationType.values()).filter(target -> Objects.equals(operation.getClass(), target.getOperationClazz())).findFirst().orElse(null);
    }

    public static OperationType fromOperationResult(OperationResult operationResult) {
        return Arrays.stream(OperationType.values()).filter(target -> Objects.equals(operationResult.getClass(), target.getOperationResultClass())).findFirst().orElse(null);
    }

    public int getOpCode() {
        return opCode;
    }

    public Class<? extends Operation> getOperationClazz() {
        return operationClazz;
    }

    public Class<? extends OperationResult> getOperationResultClass() {
        return operationResultClass;
    }
}
