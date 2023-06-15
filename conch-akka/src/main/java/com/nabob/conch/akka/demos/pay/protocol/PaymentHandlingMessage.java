package com.nabob.conch.akka.demos.pay.protocol;

import com.nabob.conch.akka.demos.pay.expend.MerchantId;

import java.math.BigDecimal;

/**
 * 支付处理协议接口
 *
 * @author Adam
 * @date 2021/2/25
 */
public interface PaymentHandlingMessage {

    /**
     * 支付处理 协议（事件）
     */
    class HandlePayment implements PaymentHandlingMessage {

        public String paymentRequestId;
        public BigDecimal amount;
        public MerchantId merchantId;
        public String userId;

        public HandlePayment(String paymentRequestId, BigDecimal amount, MerchantId merchantId, String userId) {
            this.paymentRequestId = paymentRequestId;
            this.amount = amount;
            this.merchantId = merchantId;
            this.userId = userId;
        }
    }

    /**
     * 消息转换器  （context.messageAdapter）
     *      转换 PaymentHandlingMessage 到 ConfigurationResponse
     *
     *      为了让PaymentHandling可以读取Configuration actor响应的消息，需要做两件事：
     *          1. 在PaymentHandling的协议中定义一个新的消息，此消息包含Configuration actor响应的消息
     *          2. 使用消息适配器在actor之间转换响应消息的格式
     *      鉴于Configuration actor会返回多种消息，每一种消息都应该在PaymentHandling中有对应的类型，这里我们只是简单包装一下所有的响应消息
     */
    class WrappedConfigurationResponse implements PaymentHandlingMessage {
        public ConfigurationResponse configurationResponse;

        public WrappedConfigurationResponse(ConfigurationResponse configurationResponse) {
            this.configurationResponse = configurationResponse;
        }
    }
}
