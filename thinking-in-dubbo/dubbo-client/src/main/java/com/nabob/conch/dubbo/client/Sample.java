package com.nabob.conch.dubbo.client;

import com.nabob.conch.dubbo.facade.UserService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Adam
 * @date 2021/4/2
 */
public class Sample {

    public static void main(String[] args) throws IOException {

        ApplicationConfig applicationConfig = new ApplicationConfig("client");

        ReferenceConfig<UserService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(applicationConfig);
        referenceConfig.setInterface(UserService.class);
        referenceConfig.setUrl("dubbo://10.38.124.33:20880/com.nabob.conch.dubbo.facade.UserService?anyhost=true&application=server&bind.ip=10.38.124.33&bind.port=20880&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&interface=com.nabob.conch.dubbo.facade.UserService&methods=getUserById&pid=11748&release=2.7.9&side=provider&timestamp=1617352828236");

        UserService userService = referenceConfig.get();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String line = bufferedReader.readLine();
            if (line.equals("quit")) {
                break;
            }
            System.out.println(userService.getUserById());
        }
    }

}
