package ltd.beihu.sample.loader;

import org.apache.tomcat.util.ExceptionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class TestClassLoader extends URLClassLoader {

    private static final String CLASS_FILE_SUFFIX = ".class";

    /**
     * The parent class loader.
     */
    protected final ClassLoader parent;

    /**
     * The bootstrap class loader used to load the JavaSE classes. In some
     * implementations this class loader is always <code>null</code> and in
     * those cases {@link ClassLoader#getParent()} will be called recursively on
     * the system class loader and the last non-null result used.
     */
    private ClassLoader javaseClassLoader;

    /**
     * Should this class loader delegate to the parent class loader
     * <strong>before</strong> searching its own repositories (i.e. the
     * usual Java2 delegation model)?  If set to <code>false</code>,
     * this class loader will search its own repositories first, and
     * delegate to the parent only if the class or resource is not
     * found locally. Note that the default, <code>false</code>, is
     * the behavior called for by the servlet specification.
     */
    protected boolean delegate = false;

    /**
     * Return the "delegate first" flag for this class loader.
     * @return <code>true</code> if the class lookup will delegate to
     *   the parent first. The default in Tomcat is <code>false</code>.
     */
    public boolean getDelegate() {
        return this.delegate;
    }


    /**
     * Set the "delegate first" flag for this class loader.
     * If this flag is true, this class loader delegates
     * to the parent class loader
     * <strong>before</strong> searching its own repositories, as
     * in an ordinary (non-servlet) chain of Java class loaders.
     * If set to <code>false</code> (the default),
     * this class loader will search its own repositories first, and
     * delegate to the parent only if the class or resource is not
     * found locally, as per the servlet specification.
     *
     * @param delegate The new "delegate first" flag
     */
    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    protected ClassLoader getJavaseClassLoader() {
        return javaseClassLoader;
    }

    public TestClassLoader() {
        super(new URL[0]);

        ClassLoader p = getParent();
        if (p == null) {
            p = getSystemClassLoader();
        }
        this.parent = p;

        ClassLoader j = String.class.getClassLoader();
        if (j == null) {
            j = getSystemClassLoader();
            // 获取 ExtClassLoader
            while (j.getParent() != null) {
                j = j.getParent();
            }
        }
        // 这就是一个 ExtClassLoader
        this.javaseClassLoader = j;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 1、先自己的路径找
        Class<?> clazz = null;
        try {
            clazz = findClassInternal(name);
        } catch (Exception e) {
            // Ignore
        }
        if (clazz != null) {
            return clazz;
        }
        // 在 父类路径 找
        return super.findClass(name);
    }

    private Class<?> findClassInternal(String name) throws IOException {
        // 1、获取class文件二进制字节数组
        byte[] data = null;
        try {
            try {
                System.out.println(name);
                String namePath = name.replaceAll("\\.", "\\\\");
                String classFile = "D:\\project\\beihu-framework\\beihu-sample\\src\\main\\resources\\" + namePath + ".class";
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
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = null;

            // 1.尝试读本地缓存

            // 2.先尝试 走 Java SE 的ClassLoader加载
            //  Try loading the class with the system class loader, to prevent
            //  the webapp from overriding Java SE classes. This implements SRV.10.7.2
            String resourceName = binaryNameToPath(name, false);
            ClassLoader javaseLoader = getJavaseClassLoader();
            boolean tryLoadingFromJavaseLoader;
            try {
                URL url = javaseLoader.getResource(resourceName);
                tryLoadingFromJavaseLoader = (url != null);
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                tryLoadingFromJavaseLoader = true;
            }
            if (tryLoadingFromJavaseLoader) {
                try {
                    clazz = javaseLoader.loadClass(name);
                    if (clazz != null) {
                        return clazz;
                    }
                } catch (ClassNotFoundException e) {
                    // Ignore
                }
            }

            // 3.如果需要，尝试走 parent 进行加载 | parent 就是我们的 AppClassLoader | 一些通用的 Tomcat 自身的
            boolean delegateLoad = delegate || filter(name, true);
            if (delegateLoad) {
                try {
                    System.out.println("loadClass from delegateLoad to parent");
                    clazz = Class.forName(name, false, parent);
                    if (clazz != null) {
                        return clazz;
                    }
                } catch (ClassNotFoundException e) {
                    // Ignore
                }
            }

            // 4.尝试从本地repositories中加载 | 打破直接委派
            try {
                clazz = findClass(name);
                if (clazz != null) {
                    System.out.println("loadClass from local repositories - 打破直接委派");
                    return clazz;
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }

            // 5.最后，强制走 parent 加载 | AppClassLoader
            if (!delegateLoad) {
                try {
                    clazz = Class.forName(name, false, parent);
                    if (clazz != null) {
                        System.out.println("loadClass from 强制走 parent 加载 ");
                        return clazz;
                    }
                } catch (ClassNotFoundException e) {
                    // Ignore
                }
            }
        }

        throw new ClassNotFoundException(name);
    }

    private String binaryNameToPath(String binaryName, boolean withLeadingSlash) {
        // 1 for leading '/', 6 for ".class"
        StringBuilder path = new StringBuilder(7 + binaryName.length());
        if (withLeadingSlash) {
            path.append('/');
        }
        path.append(binaryName.replace('.', '/'));
        path.append(CLASS_FILE_SUFFIX);
        return path.toString();
    }

    /**
     * Filter classes.
     *
     * @param name class name
     * @param isClassName <code>true</code> if name is a class name,
     *                <code>false</code> if name is a resource name
     * @return <code>true</code> if the class should be filtered
     */
    protected boolean filter(String name, boolean isClassName) {

        if (name == null)
            return false;

        char ch;
        if (name.startsWith("javax")) {
            /* 5 == length("javax") */
            if (name.length() == 5) {
                return false;
            }
            ch = name.charAt(5);
            if (isClassName && ch == '.') {
                /* 6 == length("javax.") */
                if (name.startsWith("servlet.jsp.jstl.", 6)) {
                    return false;
                }
                if (name.startsWith("el.", 6) ||
                        name.startsWith("servlet.", 6) ||
                        name.startsWith("websocket.", 6) ||
                        name.startsWith("security.auth.message.", 6)) {
                    return true;
                }
            } else if (!isClassName && ch == '/') {
                /* 6 == length("javax/") */
                if (name.startsWith("servlet/jsp/jstl/", 6)) {
                    return false;
                }
                if (name.startsWith("el/", 6) ||
                        name.startsWith("servlet/", 6) ||
                        name.startsWith("websocket/", 6) ||
                        name.startsWith("security/auth/message/", 6)) {
                    return true;
                }
            }
        } else if (name.startsWith("org")) {
            /* 3 == length("org") */
            if (name.length() == 3) {
                return false;
            }
            ch = name.charAt(3);
            if (isClassName && ch == '.') {
                /* 4 == length("org.") */
                if (name.startsWith("apache.", 4)) {
                    /* 11 == length("org.apache.") */
                    if (name.startsWith("tomcat.jdbc.", 11)) {
                        return false;
                    }
                    if (name.startsWith("el.", 11) ||
                            name.startsWith("catalina.", 11) ||
                            name.startsWith("jasper.", 11) ||
                            name.startsWith("juli.", 11) ||
                            name.startsWith("tomcat.", 11) ||
                            name.startsWith("naming.", 11) ||
                            name.startsWith("coyote.", 11)) {
                        return true;
                    }
                }
            } else if (!isClassName && ch == '/') {
                /* 4 == length("org/") */
                if (name.startsWith("apache/", 4)) {
                    /* 11 == length("org/apache/") */
                    if (name.startsWith("tomcat/jdbc/", 11)) {
                        return false;
                    }
                    if (name.startsWith("el/", 11) ||
                            name.startsWith("catalina/", 11) ||
                            name.startsWith("jasper/", 11) ||
                            name.startsWith("juli/", 11) ||
                            name.startsWith("tomcat/", 11) ||
                            name.startsWith("naming/", 11) ||
                            name.startsWith("coyote/", 11)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        // 初始化TestClassLoader，被将加载TestClassLoader类的类加载器设置为TestClassLoader的parent
        // 直接设置为 null；标识 交由 BootstrapClassLoader加载即可；决绝 Object.class 无法加载问题
        TestClassLoader testClassLoader = new TestClassLoader();
        System.out.println("TestClassLoader的父类加载器：" + testClassLoader.getParent() + "\r\n");

        // 加载 自己的
        load(testClassLoader, "ltd.beihu.sample.uitl.ClassHelper", "加载 自己的");

        // 加载 JRE 的
        load(testClassLoader, "java.util.ArrayList", "加载 JRE 的");

        // 加载 Ext 的
        load(testClassLoader, "com.sun.crypto.provider.ARCFOURCipher", "加载 Ext 的");

        // 加载 Tomcat 自己的
        load(testClassLoader, "org.apache.catalina.startup.Tomcat", "加载 Ext 的");

        // 加载 自己重写 JRE 的 | 注意我的本地有一个String的类的
        System.out.println("******************************** start load ***********************************");
        try {
            Class clazz = testClassLoader.loadClass("java.lang.String");
            Object o = clazz.newInstance();
            System.out.println("TestClassLoader 加载 自己重写 JRE 的 - 成功; 对应类加载器："  + clazz.getClassLoader());
            System.out.println("String.toString()："+ o.toString()); // 参考下发我重新的 String.class
        } catch (Exception e) {
            System.err.println("TestClassLoader 加载 自己重写 JRE 的 - 失败");
            Thread.sleep(10);
        }
        System.out.println("******************************** end load ***********************************\r\n");
    }

    /* 测试的自重新String：

        package java.lang;

        public class String {
            public String() {
                System.out.println("我是病毒，我启动拉");
            }

            public int length() {
                return 10;
            }

            public String toString() {
                return "我是 toString";
            }
        }
     */

    public static void load(TestClassLoader testClassLoader, String name, String info) throws InterruptedException {
        System.out.println("******************************** start load ***********************************");
        try {
            Class clazz = testClassLoader.loadClass(name);
            Object o = clazz.newInstance();
            System.out.println("TestClassLoader "+ info +" - 成功; 对应类加载器："  + clazz.getClassLoader());
        } catch (Exception e) {
            System.err.println("TestClassLoader "+ info +" - 失败");
            Thread.sleep(10);
        }
        System.out.println("******************************** end load ***********************************\r\n");
    }

}