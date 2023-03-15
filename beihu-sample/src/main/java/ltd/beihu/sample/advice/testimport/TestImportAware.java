package ltd.beihu.sample.advice.testimport;

import ltd.beihu.sample.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;

/**
 * TestImportAware
 * <p>
 * 知识点：如果想识别 ImportAware ，那么TestImportAware必须在 ImportRegistry 中存在，不然不会生效
 * <p>
 * 可使用 @Import(TestImportAware) 进行设置
 * 替代方案：
 *      ImportAware 其实就是为了获取 AnnotationMetadata 进而拿到自己想要的 注解什么的
 *      可以使用  ImportBeanDefinitionRegistrar 代替
 *
 * @author Adam
 * @since 2023/3/15
 */
@Configuration
public class TestImportAware implements ImportAware {
    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        System.out.println("TestImportAware run setImportMetadata");
    }

    @Bean(name = "user1")
    public User user() {
        return new User("TestImportAware", 19);
    }
}
