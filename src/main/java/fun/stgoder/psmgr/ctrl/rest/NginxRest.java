package fun.stgoder.psmgr.ctrl.rest;

import fun.stgoder.psmgr.common.Code;
import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.common.OS;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.common.model.Resp;
import fun.stgoder.psmgr.model.Nginx1;
import fun.stgoder.psmgr.ps.Ps;
import fun.stgoder.psmgr.ps.nginx.Nginx;
import fun.stgoder.psmgr.ps.pusher.Pusher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/nginx")
public class NginxRest {
    @GetMapping
    public Resp nginx() {
        Nginx1 nginx = new Nginx1();
        nginx.setPsFlg(Nginx.NGINX_BIN_FILENAME);
        List<Integer> pids = new ArrayList<>();
        try {
            pids = OS.pids(Nginx.NGINX_BIN_FILENAME);
            if (pids.size() > 0)
                nginx.setAlive(true);
        } catch (ExecException e) {
            e.printStackTrace();
        }
        nginx.setPids(pids);
        return new Resp(Code.REQUEST_OK, nginx);
    }

    @PostMapping("/restart")
    public Resp restart() throws ExecException {
        try {
            Nginx.stop();
        } catch (ExecException e) {
            e.printStackTrace();
        }
        Nginx.start();
        return new Resp(Code.REQUEST_OK);
    }

    @PostMapping("/reload")
    public Resp reload() throws ExecException {
        Nginx.reload();
        List<String> pullFailedPusherKeys = new ArrayList<>();
        if (Constants.WITH_NGINX) {
            pullFailedPusherKeys = Pusher.reloadAllPushers();
        }
        return new Resp(Code.REQUEST_OK, pullFailedPusherKeys);
    }

    @PostMapping("/stop")
    public Resp stop() throws ExecException {
        Nginx.stop();
        return new Resp(Code.REQUEST_OK);
    }

    @PostMapping("/redeploy")
    public Resp redeploy() throws ExecException, IOException {
        Nginx.redeploy();
        return new Resp(Code.REQUEST_OK);
    }
}
