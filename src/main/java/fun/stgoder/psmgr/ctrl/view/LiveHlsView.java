package fun.stgoder.psmgr.ctrl.view;

import fun.stgoder.psmgr.common.exception.BLException;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.model.Hls1;
import fun.stgoder.psmgr.ps.live.Hls;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/live-hls")
public class LiveHlsView {
    @GetMapping({"/", ""})
    public ModelAndView hls(ModelAndView mv) {
        mv.setViewName("live-hls");
        mv.addObject("title", "live-hls");
        mv.addObject("tab", "live-hls");
        List<Hls1> hlss = new ArrayList<>();
        for (Hls hls : Hls.hlss()) {
            hlss.add(Hls1.fromHls(hls));
        }
        mv.addObject("hlss", hlss);
        return mv;
    }

    @PostMapping("/startAndPut")
    public ModelAndView startAndPut(@RequestParam("key") String key,
                                    @RequestParam("url") String url,
                                    @RequestParam("keepAlive") boolean keepAlive,
                                    @RequestParam(value = "cancelAfterSeconds", required = false, defaultValue = "0")
                                            long cancelAfterSeconds,
                                    ModelAndView mv) throws ExecException, BLException {
        mv.setViewName("redirect:/live-hls/");
        if (StringUtils.isBlank(key))
            throw new BLException(-1, "key blank");
        if (StringUtils.isBlank(url))
            throw new BLException(-1, "url blank");
        Hls.startAndPut(key, url, keepAlive, cancelAfterSeconds);
        return mv;
    }

    @PostMapping("/stopAndRemove")
    public ModelAndView stopAndRemove(@RequestParam("key") String key,
                                      ModelAndView mv) throws ExecException, BLException {
        mv.setViewName("redirect:/live-hls/");
        Hls.stopAndRemove(key);
        return mv;
    }
}
