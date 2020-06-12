package fun.stgoder.psmgr;

import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.common.db.Ds;
import fun.stgoder.psmgr.common.exception.BaseException;
import fun.stgoder.psmgr.ps.live.Hls;
import fun.stgoder.psmgr.ps.nginx.Nginx;
import fun.stgoder.psmgr.ps.pusher.Pusher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class Start {

    public static void main(String[] args) throws BaseException, IOException {
        SpringApplication.run(Start.class, args);

        if (Constants.WITH_NGINX) {
            Nginx.init();
            if (!Nginx.alive())
                Nginx.start();
        }
        Ds.initSqlite0();
        Pusher.loadFromDB();
        Hls.loadFromDB();
    }
}