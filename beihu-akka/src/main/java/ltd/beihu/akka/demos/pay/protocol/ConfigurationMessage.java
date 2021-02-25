package ltd.beihu.akka.demos.pay.protocol;

import akka.actor.typed.ActorRef;
import ltd.beihu.akka.demos.pay.expend.MerchantId;

/**
 * 配置协议接口
 *
 * @author Adam
 * @date 2021/2/25
 */
public interface ConfigurationMessage {

    /**
     * 检验配置协议
     */
    class RetrieveConfiguration implements ConfigurationMessage {

        /**
         * 商户id
         */
        public MerchantId merchantId;

        /**
         * 检验配置返回ActorRef
         */
        public ActorRef<ConfigurationResponse> replyTo;

        public RetrieveConfiguration(MerchantId merchantId, ActorRef<ConfigurationResponse> replyTo) {
            this.merchantId = merchantId;
            this.replyTo = replyTo;
        }
    }

}
