package ltd.beihu.spring.aop.overview;

/**
 * 类加载
 *
 * @author Adam
 * @since 2023/3/14
 */
public class ClassLoadingDemo {


    /**
     * <p>
     * static class AppClassLoader extends URLClassLoader {
     *      // ...
     *      final String var1 = System.getProperty("java.class.path");
     *      // ...
     * }
     *
     * Java ClassPath 本地文件路径
     * 例如："C:\Program Files\Java\jdk1.8.0_191\bin\java.exe" -javaagent:D:\Users\jz.zheng\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\222.4345.14\lib\idea_rt.jar=55252:D:\Users\jz.zheng\AppData\Local\JetBrains\Toolbox\apps\IDEA-U\ch-0\222.4345.14\bin
     *      -Dfile.encoding=UTF-8
     *      -classpath "C:\Program Files\Java\jdk1.8.0_191\jre\lib\rt.jar;D:\project\beihu-framework\thinking-in-spring\aop-overview\target\classes;D:\maven\aliyun\org\springframework\spring-context\5.2.14.RELEASE\spring-context-5.2.14.RELEASE.jar;" ltd.beihu.spring.aop.overview.ClassLoadingDemo
     *
     * Spring 调整为了 相对路径
     *
     * </p>
     */
    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getContextClassLoader());
        System.out.println(Thread.currentThread().getContextClassLoader().getParent());
    }

}
