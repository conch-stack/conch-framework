package ltd.beihu.core.dubbo.server.spring;

import ltd.beihu.core.dubbo.facade.TestService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Component;

/**
 * 测试服务
 *
 * @author Adam
 * @date 2021/4/25
 */
@DubboService
@Component
public class TestServiceImpl implements TestService {

    @Override
    public String getTestName() {
        return "Test Code Name";
    }
}
