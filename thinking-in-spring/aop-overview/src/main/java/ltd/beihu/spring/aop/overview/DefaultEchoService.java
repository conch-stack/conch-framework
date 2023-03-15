package ltd.beihu.spring.aop.overview;

/**
 * @author Adam
 * @since 2023/3/15
 */
public class DefaultEchoService implements EchoService {
    @Override
    public void echo(String info) {
        System.out.println("Default" + info);
    }

    @Override
    public String echo() {
        return "Default Echo";
    }
}
