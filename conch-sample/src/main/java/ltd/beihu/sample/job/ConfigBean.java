package ltd.beihu.sample.job;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author Adam
 * @since 2022/11/15
 */
@Data
@AllArgsConstructor
public class ConfigBean {

    private Object targetBean;
    private Method targetMethod;

}
