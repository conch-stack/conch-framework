## 数据校验

Bean Validation



### 核心API

**元注解 - @javax.validation.Constraint   （约束、规范）**

- 标注在目标校验注解上，来指定Bean Validation 校验器的实现



**校验器接口：ConstraintValidator**

- 实现该接口，处理实际校验逻辑
- 主要方法：
  - 初始化方法：#initialize
    - 通过注解方法获取相关元信息
  - 校验方法： #isValid
    - 通过对象传入，并且控制 ConstraintValidatorContext （**校验器上下文**）



内建注解校验器



自定义注解校验器

```java
import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义校验注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserValidAnnotationValidator.class)   // 重点
public @interface UserValid {

    int idRange() default 0;
}

// ------------

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 定义校验注解校验器
 */
public class UserValidAnnotationValidator implements ConstraintValidator<UserValid, User> {

    private int idRange;

    public void initialize(UserValid annotation) {
        this.idRange = annotation.idRange();
    }

    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {

        // 获取模板信息
        context.getDefaultConstraintMessageTemplate();

        return false;
    }
}
```



**文案解析器 - javax.validation.MessageInterpolator**



**校验分组**



**引导** **Bean Validation** （启动）

Bean Validation SPI -javax.validation.spi.ValidationProvider

Bean Validation 配置 API - javax.validation.Configuration