package fun.stgoder.psmgr.ctrl.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping("/")
public class IndexView {

    @GetMapping("/")
    public ModelAndView index(ModelAndView mv) throws IOException {
        mv.setViewName("index");
        mv.addObject("title", "process manager");
        mv.addObject("tab", "index");
        return mv;
    }
}
