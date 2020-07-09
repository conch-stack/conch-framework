package ltd.beihu.spring.lifecycle.beanlifecycle.cycledepend;

import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * @author Adam
 * @date 2020/6/5
 */
public class TestB {

    private String name;

    @Autowired
    private TestA testA;

    @PostConstruct
    public void init() {
        System.out.println("TestB : " + testA);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TestA getTestA() {
        return testA;
    }

    public void setTestA(TestA testA) {
        this.testA = testA;
    }

    @Override
    public String toString() {
        return "TestB{" +
                "name='" + name + '\'' +
                '}';
    }
}
