package ltd.beihu.core.dubbo.server.spihigh;


import org.apache.dubbo.common.URL;

/**
 * 车轮制造厂
 *
 * @author Adam
 * @date 2021/4/23
 */
public interface WheelMaker {

    /**
     * 造轮子
     */
     Wheel makeWheel(URL url);
}
