package ltd.beihu.core.netty.common.auth;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ltd.beihu.core.netty.common.OperationResult;

/**
 * @author Adam
 * @since 2022/1/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AuthOperationResult extends OperationResult {

    private final boolean passAuth;
}
