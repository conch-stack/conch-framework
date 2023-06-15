package com.nabob.conch.akka.demos.pay;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.nabob.conch.akka.demos.pay.protocol.ConfigurationMessage;
import com.nabob.conch.akka.demos.pay.protocol.ConfigurationResponse;
import com.nabob.conch.akka.demos.pay.protocol.PaymentHandlingMessage;

/**
 * 支付处理 - 系统核心，根据请求参数从配置组件获取具体的处理器，并控制整个支付流程，如验证、执行等。
 *
 * @author Adam
 * @date 2021/2/25
 */
public class PaymentHandling extends AbstractBehavior<PaymentHandlingMessage> {

    /**
     * 包含配置协议的Actor
     */
    private ActorRef<ConfigurationMessage> configuration;

    /**
     * 配置返回适配Actor
     */
    private ActorRef<ConfigurationResponse> configurationResponseAdapter;

    /**
     * create PaymentHandling Behavior
     *
     * @return PaymentHandling Behavior
     */
    public static Behavior<PaymentHandlingMessage> create(ActorRef<ConfigurationMessage> configuration) {
        return Behaviors.setup(context -> new PaymentHandling(context, configuration));
    }

    private PaymentHandling(ActorContext<PaymentHandlingMessage> context, ActorRef<ConfigurationMessage> configuration) {
        super(context);
        this.configuration = configuration;
        this.configurationResponseAdapter = context.messageAdapter(ConfigurationResponse.class, PaymentHandlingMessage.WrappedConfigurationResponse::new);
    }

    @Override
    public Receive<PaymentHandlingMessage> createReceive() {
        return null;
    }
}
