package ltd.beihu.sample.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TestClassLoader extends ClassLoader {

    public TestClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 1、获取class文件二进制字节数组
        byte[] data = null;
        try {
            System.out.println(name);
            String namePath = name.replaceAll("\\.", "\\\\");
            String classFile = "D:\\project\\beihu-framework\\beihu-sample\\target\\classes\\java\\lang\\" + namePath + ".class";
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(new File(classFile));
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fis.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            data = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 2、字节码加载到 JVM 的方法区，
        // 并在 JVM 的堆区建立一个java.lang.Class对象的实例
        // 用来封装 Java 类相关的数据和方法
        return this.defineClass(name, data, 0, data.length);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> clazz = null;
        // 直接自己加载
        clazz = this.findClass(name);
        if (clazz != null) {
            return clazz;
        }

        // 自己加载不了，再调用父类loadClass，保持双亲委托模式
        return super.loadClass(name);
    }

    public static void main(String[] args) throws Exception {
        // 初始化TestClassLoader，被将加载TestClassLoader类的类加载器设置为TestClassLoader的parent
        TestClassLoader testClassLoader = new TestClassLoader(TestClassLoader.class.getClassLoader());
        System.out.println("TestClassLoader的父类加载器：" + testClassLoader.getParent());
        // 加载 Demo
        Class clazz = testClassLoader.loadClass("String");
        System.out.println("Demo的类加载器：" + clazz.getClassLoader());
    }

}