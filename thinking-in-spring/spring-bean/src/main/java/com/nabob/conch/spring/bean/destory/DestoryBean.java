package ltd.beihu.spring.bean.destory;

import org.springframework.beans.factory.DisposableBean;

import javax.annotation.PreDestroy;

/**
 * 销毁 Spring Bean
 * @author Adam
 * @since 2020/4/3
 */
public class DestoryBean implements DisposableBean {

    @PreDestroy
    public void destory1() {
        System.out.println("使用：@PreDestroy 方法销毁Bean");
    }

    public void destoryMethod() {
        System.out.println("使用：@Bean(destoryMethod) 方法销毁Bean");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("使用 DisposableBean 方法销毁Bean");
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("DestoryBean 正在被GC回收...(finalize方法并不是每次都会被回调，但这并不意味着不GC)");
    }
}
