package ltd.beihu.akka.demos.pay.expend;

import java.util.Objects;

/**
 * 商户 id
 *
 * @author Adam
 * @date 2021/2/25
 */
public class MerchantId {

    public String id;

    public MerchantId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MerchantId that = (MerchantId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}