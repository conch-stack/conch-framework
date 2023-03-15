package ltd.beihu.sample;

import ltd.beihu.sample.advice.EnableRpcLogV2;
import ltd.beihu.sample.job.ConfigurationConfigSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ConfigurationConfigSupport
//第一版
//@EnableRpcLog("ltd.beihu.sample.dynamic")

//第二版 - 兼容第一版 - 测试
//@EnableRpcLogV2(agentPackage = "ltd.beihu.sample.dynamic", mode = RpcLogMode.V1)
//第二版 - 第二版默认注解 - 类and方法 - Void方法拦截 - 测试
//@EnableRpcLogV2
//第二版 - 第二版自定义注解 - 类and方法 - 测试
//@EnableRpcLogV2(annotation = SelfRpcLog.class)
//第二版 - 第二版AgentPackage拦截 - 测试
@EnableRpcLogV2(agentPackage = "ltd.beihu.sample.test")
public class BeihuSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeihuSampleApplication.class, args);
        System.out.println("end-container-1");
    }

}
