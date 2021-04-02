package ltd.beihu.core.dubbo.server;

import ltd.beihu.core.dubbo.facade.UserService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

import java.io.IOException;

/**
 * @author Adam
 * @date 2021/4/2
 */
public class Sample {

    public static void main(String[] args) throws IOException {

        ApplicationConfig applicationConfig = new ApplicationConfig("server");

        RegistryConfig registryConfig = new RegistryConfig(RegistryConfig.NO_AVAILABLE);

        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(-1);

        ServiceConfig<UserService> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(UserService.class);
        serviceConfig.setRef(new UserServiceImpl());

        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setProtocol(protocolConfig);

        // 暴露
        serviceConfig.export();

        System.out.println("Server Started");
        System.in.read();
    }

}
