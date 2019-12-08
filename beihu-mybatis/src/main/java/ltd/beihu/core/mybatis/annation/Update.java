package ltd.beihu.core.mybatis.annation;

import java.lang.annotation.*;

/**
 * Update Annotation
 * @author Adam
 * @since 2019/12/7
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Update {

    String value();
}
