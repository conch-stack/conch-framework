package ltd.beihu.sample.advice.agentv2;

import ltd.beihu.sample.advice.EnableRpcLogV2;
import ltd.beihu.sample.advice.agentv2.old.AgentBeanDefinitionRegistrarDelegator;

/**
 * 选择 RpcLog 模式
 *
 * @author Adam
 * @since 2023/3/15
 */
public class RpcLogConfigurationSelector extends RpcLogModeImportSelector<EnableRpcLogV2> {

    @Override
    protected String[] selectImports(RpcLogMode rpcLogMode) {
        switch (rpcLogMode) {
            case V1:
                return new String[] {AgentBeanDefinitionRegistrarDelegator.class.getName()};
            case V2:
                return new String[] {RpcLogConfiguration.class.getName()};
            default:
                return null;
        }
    }
}
