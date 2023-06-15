package com.nabob.conch.sample.advice.agentv2.advisor.pointcut;

import org.springframework.aop.ClassFilter;

/**
 * PackageClassFilter
 *
 * @author Adam
 * @since 2023/3/15
 */
public class PackageClassFilter implements ClassFilter {

    private final String packageName;

    public PackageClassFilter(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        if (!clazz.getName().contains(packageName)) {
            return false;
        }
        if (clazz.isInterface()) {
            return false;
        }
        return true;
    }
}
