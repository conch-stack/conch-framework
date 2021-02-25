package ltd.beihu.akka.demos.pay;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import ltd.beihu.akka.demos.pay.protocol.PaymentHandlingMessage;

/**
 * 支付处理 - 系统核心，根据请求参数从配置组件获取具体的处理器，并控制整个支付流程，如验证、执行等。
 *
 * @author Adam
 * @date 2021/2/25
 */
public class PaymentHandling extends AbstractBehavior<PaymentHandlingMessage> {

    /**
     * create PaymentHandling Behavior
     *
     * @return PaymentHandling Behavior
     */
    public static Behavior<PaymentHandlingMessage> create() {
        return Behaviors.setup(PaymentHandling::new);
    }

    private PaymentHandling(ActorContext<PaymentHandlingMessage> context) {
        super(context);
    }

    @Override
    public Receive<PaymentHandlingMessage> createReceive() {
        return null;
    }
}
