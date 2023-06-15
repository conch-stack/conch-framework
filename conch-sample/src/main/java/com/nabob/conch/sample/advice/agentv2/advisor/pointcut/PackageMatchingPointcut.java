package com.nabob.conch.sample.advice.agentv2.advisor.pointcut;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

/**
 * @author Adam
 * @since 2023/3/15
 */
public class PackageMatchingPointcut implements Pointcut {

    private final ClassFilter classFilter;

    private final MethodMatcher methodMatcher;

    public PackageMatchingPointcut(String packageName) {
        this.classFilter = new PackageClassFilter(packageName);
        this.methodMatcher = MethodMatcher.TRUE;
    }

    @Override
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this.methodMatcher;
    }
}
