package fun.stgoder.psmgr;

import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.common.OS;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.ps.nginx.Nginx;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class Start {

    public static void main(String[] args) throws ExecException, IOException {
        if ((OS.isLINUX() || OS.isMAC()) && Constants.WITH_NGINX) {
            Nginx.init();
            if (Nginx.alive())
                Nginx.stop();
            Nginx.start();
        }

        SpringApplication.run(Start.class, args);
    }
}