package com.nabob.conch.sample.byteBuddy.abtest;

import com.nabob.conch.sample.byteBuddy.agent.ABClientCache;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

/**
 * @author Adam
 * @since 2023/8/9
 */
public class EnhanceAbTest {

    public static void enhance(Class<?> clazz) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass;
        try {
//            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//            pool.insertClassPath(new LoaderClassPath(cl));
            pool.insertClassPath(new ClassClassPath(ABClientCache.class));
            ctClass = pool.getCtClass(clazz.getName());
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
//            ctClass.defrost();
//            int modifiers = ctClass.getModifiers();
//            boolean aFinal = Modifier.isFinal(modifiers);
//            if (aFinal) {
//                int notFinalModifier = Modifier.clear(modifiers, Modifier.FINAL);
//                ctClass.setModifiers(notFinalModifier);
//            }

//            Class<?> aClass = ctClass.toClass();

//            Method getOid = aClass.getMethod("getOid");

//            pool.importPackage("com.ctrip.train.ztrain.common.framework.log.TLogger");

            CtMethod m = ctClass.getDeclaredMethod("sync");
//            m.setModifiers(Modifier.PUBLIC + Modifier.STATIC); // remove native flag
            m.insertBefore("System.out.println(\"enhance sync is called\");");
//            m.setBody("{" +
//                    "System.out.println(\"来了\");" +
//                    "}");

            retransformClass(clazz, ctClass.toBytecode());
        } catch (NotFoundException | IOException | CannotCompileException e) {
            e.printStackTrace();
        }
    }

    private static void retransformClass(Class<?> clazz, byte[] byteCode) {
        ClassFileTransformer cft = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                return byteCode;
            }
        };

        Instrumentation instrumentation = ByteBuddyAgent.install();
        try {
            instrumentation.addTransformer(cft, true);
            instrumentation.retransformClasses(clazz);
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        } finally {
            instrumentation.removeTransformer(cft);
        }
    }

}
