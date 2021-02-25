package ltd.beihu.akka.demos.pay.expend;

/**
 * 商户 配置
 *
 * @author Adam
 * @date 2021/2/25
 */
public class MerchantConfiguration {

    public BankIdentifier bankIdentifier;

    public MerchantConfiguration(BankIdentifier bankIdentifier) {
        this.bankIdentifier = bankIdentifier;
    }

}