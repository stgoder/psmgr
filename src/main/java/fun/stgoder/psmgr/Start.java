package fun.stgoder.psmgr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Start {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Start.class, args);
    }
}