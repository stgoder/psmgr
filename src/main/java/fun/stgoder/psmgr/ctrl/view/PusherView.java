package fun.stgoder.psmgr.ctrl.view;

import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.common.exception.BLException;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.ps.pusher.Pusher;
import fun.stgoder.psmgr.model.Pusher1;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pusher")
public class PusherView {
    @GetMapping({"/", ""})
    public ModelAndView pusher(ModelAndView mv) {
        mv.setViewName("pusher");
        mv.addObject("title", "pusher");
        mv.addObject("tab", "pusher");
        List<Pusher1> pushers = new ArrayList<>();
        for (Pusher pusher : Pusher.pushers()) {
            pushers.add(Pusher1.fromPusher(pusher));
        }
        mv.addObject("pushers", pushers);
        return mv;
    }

    @PostMapping("/startAndPut")
    public ModelAndView startAndPut(@RequestParam("key") String key,
                                    @RequestParam("rtspUrl") String rtspUrl,
                                    @RequestParam("rtmpUrl") String rtmpUrl,
                                    @RequestParam("keepAlive") boolean keepAlive,
                                    @RequestParam(value = "cancelAfterSeconds", required = false, defaultValue = "0")
                                            long cancelAfterSeconds,
                                    ModelAndView mv) throws ExecException, BLException {
        mv.setViewName("redirect:/pusher/");
        if (StringUtils.isBlank(key))
            throw new BLException(-1, "key blank");
        if (StringUtils.isBlank(rtspUrl))
            throw new BLException(-1, "rtspUrl blank");
        if (StringUtils.isBlank(rtmpUrl))
            throw new BLException(-1, "rtmpUrl blank");
        Pusher.startAndPut(key, rtspUrl, rtmpUrl, keepAlive, cancelAfterSeconds);
        return mv;
    }

    @PostMapping("/stopAndRemove")
    public ModelAndView stopAndRemove(@RequestParam("key") String key,
                                      ModelAndView mv) throws ExecException, BLException {
        mv.setViewName("redirect:/pusher/");
        Pusher.stopAndRemove(key);
        return mv;
    }

    @GetMapping("/play-http-flv/{key}")
    public ModelAndView play(@PathVariable("key") String key,
                             @RequestParam("type") String type, ModelAndView mv) throws Exception {
        mv.setViewName("play-http-flv");
        String url = "http://" + Constants.localIpv4 + ":8000/live?app=live&stream=" + key;
        if (StringUtils.equals(type, "nginx"))
            url = "http://" + Constants.localIpv4 + ":8000/live?app=live&stream=" + key;
        if (StringUtils.equals(type, "srs"))
            url = "http://" + Constants.localIpv4 + ":8080/live/" + key + ".flv";
        mv.addObject("url", url);
        return mv;
    }
}
