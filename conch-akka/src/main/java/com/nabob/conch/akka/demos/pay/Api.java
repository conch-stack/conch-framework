package com.nabob.conch.akka.demos.pay;

import akka.actor.typed.ActorSystem;

/**
 * 协议 + 行为
 *
 * 1. API 系统入口，负责认证，接收多种格式的请求，转发给对应的输出组件。本教程会采用非常简单的实现。
 * 2. Payment Handler 系统核心，根据请求参数从配置组件获取具体的处理器，并控制整个支付流程，如验证、执行等。
 * 3. Configuration 存储API用户和可用的支付方式关系（契约）
 * 4. Payment processors 它们负责处理具体的支付逻辑，真实系统中会包含很多支付方式，在这里我们以简单的信用卡支付为例，
 *    它通常会需要调用其它组件或者第三方系统才能完成支付逻辑，但为了简单起见，我们不考虑这些外部依赖。
 *
 * @author Adam
 * @date 2021/2/25
 */
public class Api {

    public static void main(String[] args) {
        ActorSystem<Void> paymentProcessorActorSystem = ActorSystem.create(PaymentProcessor.create(), "Api-Payment-Processor-System");
    }
}
