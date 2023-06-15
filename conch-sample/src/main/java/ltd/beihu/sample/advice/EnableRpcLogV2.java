package ltd.beihu.sample.advice;

import ltd.beihu.sample.advice.agentv2.RpcLogConfigurationSelector;
import ltd.beihu.sample.advice.agentv2.RpcLogMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启rpc记录日志功能 V2 版本，支持V1，v2模式切换
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(RpcLogConfigurationSelector.class)
public @interface EnableRpcLogV2 {

    /**
     * 默认V2
     */
    RpcLogMode mode() default RpcLogMode.V2;

    /**
     * 定义注解，可适配改注解
     */
    Class<? extends Annotation> annotation() default Annotation.class;

    @AliasFor("agentPackage")
    String value() default "";

    @AliasFor("value")
    String agentPackage() default "";

    /**
     * 是否记录ck日志，默认写入
     */
    boolean recordCk() default true;
}