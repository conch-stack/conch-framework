package com.nabob.conch.sample.uitl;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import net.bytebuddy.agent.ByteBuddyAgent;

public class JavasistUtils {

    public static void addAnnotationToClass(Class<?> clazz, Class<?> annotationClass,
                                            BiConsumer<Annotation, ConstPool> initAnnotation) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass;
        try {
            ctClass = pool.getCtClass(clazz.getName());
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            ConstPool constPool = ctClass.getClassFile().getConstPool();

            Annotation annotation = new Annotation(annotationClass.getName(), constPool);
            if (initAnnotation != null) {
                initAnnotation.accept(annotation, constPool);
            }

            AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
            ctClass.getClassFile().addAttribute(attr);
            attr.addAnnotation(annotation);

            retransformClass(clazz, ctClass.toBytecode());
        } catch (NotFoundException | IOException | CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public static void addAnnotationToField(Class<?> clazz, String fieldName, Class<?> annotationClass,
                                            BiConsumer<Annotation, ConstPool> initAnnotation) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass;
        try {
            ctClass = pool.getCtClass(clazz.getName());
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            CtField ctField = ctClass.getDeclaredField(fieldName);
            ConstPool constPool = ctClass.getClassFile().getConstPool();

            Annotation annotation = new Annotation(annotationClass.getName(), constPool);
            if (initAnnotation != null) {
                initAnnotation.accept(annotation, constPool);
            }

            AnnotationsAttribute attr = getAnnotationsAttributeFromField(ctField);
            if (attr == null) {
                attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                ctField.getFieldInfo().addAttribute(attr);
            }
            attr.addAnnotation(annotation);

            retransformClass(clazz, ctClass.toBytecode());
        } catch (NotFoundException | IOException | CannotCompileException e) {
            e.printStackTrace();
        }
    }

    public static void removeAnnotationFromField(Class<?> clazz, String fieldName, Class<?> annotationClass) {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass;
        try {
            ctClass = pool.getCtClass(clazz.getName());
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            CtField ctField = ctClass.getDeclaredField(fieldName);

            AnnotationsAttribute attr = getAnnotationsAttributeFromField(ctField);
            if (attr != null) {
                attr.removeAnnotation(annotationClass.getName());
            }

            retransformClass(clazz, ctClass.toBytecode());
        } catch (NotFoundException | IOException | CannotCompileException e) {
            e.printStackTrace();
        }
    }

    private static AnnotationsAttribute getAnnotationsAttributeFromField(CtField ctField) {
        List<AttributeInfo> attrs = ctField.getFieldInfo().getAttributes();
        AnnotationsAttribute attr = null;
        if (attrs != null) {
            Optional<AttributeInfo> optional = attrs.stream()
                                                    .filter(AnnotationsAttribute.class::isInstance)
                                                    .findFirst();
            if (optional.isPresent()) {
                attr = (AnnotationsAttribute) optional.get();
            }
        }
        return attr;
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

    /*
     public static class Extension implements BeforeAllCallback, AfterAllCallback {

        @Override
        public void beforeAll(ExtensionContext arg0) throws Exception {
            JavasistUtils.removeAnnotationFromField(Base.class, "id", GeneratedValue.class);
        }

        @Override
        public void afterAll(ExtensionContext arg0) throws Exception {
            JavasistUtils.addAnnotationToField(Base.class, "id", GeneratedValue.class, (annotation, constPool) -> {
                EnumMemberValue memberValue = new EnumMemberValue(constPool);
                memberValue.setType(GenerationType.class.getName());
                memberValue.setValue(GenerationType.IDENTITY.name());
                annotation.addMemberValue("strategy", memberValue);
            });
        }
    }
     */
}