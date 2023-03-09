package ltd.beihu.sample;

import ltd.beihu.sample.advice.EnableRpcLog;
import ltd.beihu.sample.job.ConfigurationConfigSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ConfigurationConfigSupport
@EnableRpcLog("ltd.beihu.sample.dynamic")
public class BeihuSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeihuSampleApplication.class, args);
    }

}
