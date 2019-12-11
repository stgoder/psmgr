package fun.stgoder.psmgr.ctrl.view;

import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.model.Nginx1;
import fun.stgoder.psmgr.ps.Ps;
import fun.stgoder.psmgr.ps.nginx.Nginx;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/nginx")
public class NginxView {
    @GetMapping({"", "/"})
    public ModelAndView nginx(ModelAndView mv) {
        mv.setViewName("nginx");
        mv.addObject("title", "nginx");
        mv.addObject("tab", "nginx");
        Nginx1 nginx = new Nginx1();
        nginx.setPsFlg(Nginx.NGINX_BIN_FILENAME);
        List<Integer> pids = new ArrayList<>();
        try {
            pids = Ps.pids(Nginx.NGINX_BIN_FILENAME);
            if (pids.size() > 0)
                nginx.setAlive(true);
        } catch (ExecException e) {
            e.printStackTrace();
        }
        nginx.setPids(pids);
        mv.addObject("nginx", nginx);
        return mv;
    }

    @PostMapping("/restart")
    public ModelAndView restart(ModelAndView mv) throws ExecException {
        mv.setViewName("redirect:/nginx/");
        try {
            Nginx.stop();
        } catch (ExecException e) {
            e.printStackTrace();
        }
        Nginx.start();
        return mv;
    }

    @PostMapping("/reload")
    public ModelAndView reload(ModelAndView mv) throws ExecException {
        mv.setViewName("redirect:/nginx/");
        Nginx.reload();
        return mv;
    }

    @PostMapping("/stop")
    public ModelAndView stop(ModelAndView mv) throws ExecException {
        mv.setViewName("redirect:/nginx/");
        Nginx.stop();
        return mv;
    }
}
