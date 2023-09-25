package com.nabob.conch.sample.enfinal;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
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
public class TestFinalClassEnhanceUtil {

    public static void enhance(Class<?> clazz) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass;
        try {
            ctClass = pool.getCtClass(clazz.getName());
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
//            int modifiers = ctClass.getModifiers();
//            boolean aFinal = Modifier.isFinal(modifiers);
//            if (aFinal) {
//                int notFinalModifier = Modifier.clear(modifiers, Modifier.FINAL);
//                ctClass.setModifiers(notFinalModifier);
//            }

//            Class<?> aClass = ctClass.toClass();

//            Method getOid = aClass.getMethod("getOid");

            CtMethod m = ctClass.getDeclaredMethod("getOid");
            m.setModifiers(Modifier.PUBLIC + Modifier.STATIC); // remove native flag
            m.setBody("{" +
                    "return \"uuuu\";" +
                    "}");

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
