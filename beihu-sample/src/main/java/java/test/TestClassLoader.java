package java.test;

/**
 * JVM根本就不会然你RUN起来
 *
 * 因为识别了 包名前缀： java.
 *
 * @author Adam
 * @since 2023/4/25
 */
public class TestClassLoader {

    public static String test() {
        String s = new String();
        System.out.println("called");
        return s;
    }

}
