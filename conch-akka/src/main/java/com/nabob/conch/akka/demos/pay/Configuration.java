package com.nabob.conch.akka.demos.pay;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.nabob.conch.akka.demos.pay.expend.MerchantConfiguration;
import com.nabob.conch.akka.demos.pay.expend.MerchantId;
import com.nabob.conch.akka.demos.pay.protocol.ConfigurationMessage;
import com.nabob.conch.akka.demos.pay.protocol.ConfigurationResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration 存储API用户和可用的支付方式关系（契约）
 *
 * @author Adam
 * @date 2021/2/25
 */
public class Configuration extends AbstractBehavior<ConfigurationMessage> {

    /**
     * create Configuration Behavior
     *
     * @return Configuration Behavior
     */
    public static Behavior<ConfigurationMessage> create() {
        return Behaviors.setup(Configuration::new);
    }

    private Configuration(ActorContext<ConfigurationMessage> context) {
        super(context);
    }

    @Override
    public Receive<ConfigurationMessage> createReceive() {
        // 执行配置校验等逻辑 - 发送ConfigurationResponse消息
        return newReceiveBuilder()
                .onMessage(ConfigurationMessage.RetrieveConfiguration.class, this::handleRetrieveConfigurationMessage)
                .build();
    }

    /**
     * 存储商户Id和支付方式的配置信息
     */
    private final Map<MerchantId, MerchantConfiguration> configurations = new HashMap<>();

    /**
     * 处理 校验配置 事件 RetrieveConfiguration
     *
     * 一个actor接收到消息之后的行为包含如下3个步骤：
     *      1. 发送一条或多条消息给其他的actor
     *      2. 创建子actor
     *      3. 返回一个新的行为，准备接收下一个消息
     *
     * @param message message
     * @return Behavior of ConfigurationMessage.RetrieveConfiguration
     */
    private Behavior<ConfigurationMessage> handleRetrieveConfigurationMessage(ConfigurationMessage.RetrieveConfiguration message) {
        MerchantId id = message.merchantId;
        MerchantConfiguration configuration = configurations.get(id);
        if (configuration != null) {
            // 使用异步通知的方式发送配置数据给请求者
            message.replyTo.tell(new ConfigurationResponse.ConfigurationFound(id, configuration));
        } else {
            message.replyTo.tell(new ConfigurationResponse.ConfigurationNotFound(id));
        }
        // 最后返回下次接收消息对应的行为
        // 这里简单的返回当前行为即可
        return this;
    }
}
