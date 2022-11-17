package ltd.beihu.sample;

import ltd.beihu.sample.job.ConfigurationConfigSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ConfigurationConfigSupport
public class BeihuSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeihuSampleApplication.class, args);
    }

}
