package ltd.beihu.core.netty.common.auth;

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
public class AuthOperation extends Operation {

    private final String userName;
    private final String password;

    @Override
    public OperationResult execute() {
        if ("admin".equalsIgnoreCase(this.userName)) {
            return new AuthOperationResult(true);
        }
        return new AuthOperationResult(false);
    }
}
