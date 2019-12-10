package fun.stgoder.psmgr.ctrl.view;

import fun.stgoder.psmgr.common.exception.BLException;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.ps.recorder.Recorder;
import fun.stgoder.psmgr.model.Recorder1;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/recorder")
public class RecorderView {
    @GetMapping({"/", ""})
    public ModelAndView recorder(ModelAndView mv) {
        mv.setViewName("recorder");
        mv.addObject("title", "recorder");
        mv.addObject("tab", "recorder");
        List<Recorder1> recorders = new ArrayList<>();
        for (Recorder recorder : Recorder.recorders()) {
            recorders.add(Recorder1.fromRecorder(recorder));
        }
        mv.addObject("recorders", recorders);
        return mv;
    }

    @PostMapping("/startAndPut")
    public ModelAndView startAndPut(@RequestParam("key") String key,
                                    @RequestParam("url") String url,
                                    @RequestParam("keepAlive") boolean keepAlive,
                                    @RequestParam(value = "cancelAfterSeconds", required = false, defaultValue = "0")
                                            long cancelAfterSeconds,
                                    ModelAndView mv) throws ExecException, BLException {
        mv.setViewName("redirect:/recorder/");
        if (StringUtils.isBlank(key))
            throw new BLException(-1, "key blank");
        if (StringUtils.isBlank(url))
            throw new BLException(-1, "url blank");
        Recorder.startAndPut(key, url, keepAlive, cancelAfterSeconds);
        return mv;
    }

    @PostMapping("/stopAndRemove")
    public ModelAndView stopAndRemove(@RequestParam("key") String key,
                                      ModelAndView mv) throws ExecException, BLException {
        mv.setViewName("redirect:/recorder/");
        Recorder.stopAndRemove(key);
        return mv;
    }


}
