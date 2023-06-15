package com.nabob.conch.netty.common.auth;

import com.nabob.conch.netty.common.OperationResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Adam
 * @since 2022/1/20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AuthOperationResult extends OperationResult {

    private final boolean passAuth;
}
