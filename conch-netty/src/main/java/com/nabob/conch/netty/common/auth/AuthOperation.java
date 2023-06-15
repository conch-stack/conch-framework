package com.nabob.conch.netty.common.auth;

import com.nabob.conch.netty.common.Operation;
import com.nabob.conch.netty.common.OperationResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;

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
