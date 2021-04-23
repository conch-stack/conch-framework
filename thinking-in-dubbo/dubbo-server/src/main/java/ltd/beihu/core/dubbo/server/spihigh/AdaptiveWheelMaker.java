package ltd.beihu.core.dubbo.server.spihigh;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * WheelMaker 接口的自适应实现类
 *
 * @author Adam
 * @date 2021/4/23
 */
public class AdaptiveWheelMaker implements WheelMaker {

    /**
     * 1. 从 URL 中获取 WheelMaker 名称
     * 2. 通过 SPI 加载具体的 WheelMaker 实现类
     * 3. 调用目标方法
     */
    @Override
    public Wheel makeWheel(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }

        // 1.从 URL 中获取 WheelMaker 名称
        String wheelMakerName = url.getParameter("Wheel.maker");
        if (wheelMakerName == null) {
            throw new IllegalArgumentException("wheelMakerName == null");
        }

        // 2.通过 SPI 加载具体的 WheelMaker
        WheelMaker wheelMaker = ExtensionLoader
                .getExtensionLoader(WheelMaker.class).getExtension(wheelMakerName);

        // 3.调用目标方法
        return wheelMaker.makeWheel(url);
    }
}
