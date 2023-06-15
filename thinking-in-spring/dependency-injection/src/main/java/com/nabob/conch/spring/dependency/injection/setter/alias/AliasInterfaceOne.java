package ltd.beihu.spring.dependency.injection.setter.alias;

import org.springframework.stereotype.Service;

/**
 * @author Adam
 * @date 2020/4/28
 */
@Service
public class AliasInterfaceOne extends AbstractAliasInterface {

    private String name;

    public AliasInterfaceOne(String name) {
        this.name = name;
    }

    @Override
    public void print() {
        System.out.println("AliasInterfaceOne: " + this.name);
    }
}
