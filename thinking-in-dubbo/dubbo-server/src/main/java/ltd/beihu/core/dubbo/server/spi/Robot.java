package ltd.beihu.core.dubbo.server.spi;

import org.apache.dubbo.common.extension.SPI;

@SPI
public interface Robot {
    void sayHello();
}