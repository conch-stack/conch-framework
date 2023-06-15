package com.nabob.conch.akka.demos.pay.protocol;

import com.nabob.conch.akka.demos.pay.expend.MerchantConfiguration;
import com.nabob.conch.akka.demos.pay.expend.MerchantId;

/**
 * 配置返回协议接口
 *
 * @author Adam
 * @date 2021/2/25
 */
public interface ConfigurationResponse {

    /**
     * 配置Found
     */
    class ConfigurationFound implements ConfigurationResponse {
        public MerchantId merchantId;
        public MerchantConfiguration merchantConfiguration;

        public ConfigurationFound(MerchantId merchantId, MerchantConfiguration merchantConfiguration) {
            this.merchantId = merchantId;
            this.merchantConfiguration = merchantConfiguration;
        }
    }

    /**
     * 配置NotFound
     */
    class ConfigurationNotFound implements ConfigurationResponse {
        public MerchantId merchantId;

        public ConfigurationNotFound(MerchantId merchantId) {
            this.merchantId = merchantId;
        }
    }
}
