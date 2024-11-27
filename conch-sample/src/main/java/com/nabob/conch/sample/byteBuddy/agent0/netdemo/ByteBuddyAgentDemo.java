package com.nabob.conch.sample.byteBuddy.agent0.netdemo;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class ByteBuddyAgentDemo {

    private static final Logger log = LoggerFactory.getLogger(ByteBuddyAgentDemo.class);

    private final static String scanPackage = "com.nabob.conch.sample";

    private final static String targetMethod = "sync";

//    private final static String implInterface = "org.springframework.cloud.gateway.filter.GlobalFilter";


    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println(">>>>> ByteBuddyAgentDemo - premain()");
        final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(false));
        new AgentBuilder.Default(byteBuddy)
                .type(nameStartsWith(scanPackage))
                .transform(new Transformer()) // update the byte code
                .with(new Listener())
                .installOn(inst);
    }

    /**
     *
     */
    private static class Transformer implements AgentBuilder.Transformer {
//        @Override
//        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module) {
//            if (typeDescription.getPackage().getActualName().equals(scanPackage)
////                    && typeDescription.getInterfaces().size() > 0
////                    && typeDescription.getInterfaces().get(0).getActualName().equals(implInterface)
//                    ) {
//                String targetClassName = typeDescription.getSimpleName();
//                System.out.println("----------------------- target class:" + targetClassName);
//
//                // 委托
//                return builder.method(named(targetMethod)
//                        .and(isPublic())).intercept(MethodDelegation.to(MethodCostTime.class));
//
//            }
//            return builder;
//        }

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
            if (typeDescription.getPackage().getActualName().equals(scanPackage)
//                    && typeDescription.getInterfaces().size() > 0
//                    && typeDescription.getInterfaces().get(0).getActualName().equals(implInterface)
            ) {
                String targetClassName = typeDescription.getSimpleName();
                System.out.println("----------------------- target class:" + targetClassName);

                // 委托
                return builder.method(named(targetMethod)
                        .and(isPublic())).intercept(MethodDelegation.to(MethodCostTime.class));

            }
            return builder;
        }
    }

    /**
     * Listener
     */
    private static class Listener implements AgentBuilder.Listener {

        private int count;

        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
            if (typeName.startsWith(scanPackage)) {
                System.out.println("--- onDiscovery ---" + typeName);
            }
        }

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
            if (typeDescription.getSimpleName().startsWith(scanPackage)) {
                System.out.println("--- onTransformation ---" + typeDescription.getSimpleName());
            }
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
            if (typeName.startsWith(scanPackage)) {
                System.out.println("--- onError ---" + throwable);
            }
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
            if (typeName.startsWith(scanPackage)) {
                System.out.println("--- onComplete ---" + typeName);
            }
        }
    }

}