package ltd.beihu.sample.test;

import org.springframework.stereotype.Service;

/**
 * @author Adam
 * @see ltd.beihu.sample.advice.agentv2.processor.AgentPackageBeanPostProcessor
 * @since 2023/3/15
 */
@Service
public class AgentTestService {

    public void testAgentPackageAop() {
        System.out.println("testAgentPackageAop called");
    }

}
