package fun.stgoder.psmgr.ctrl.hook;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/hook/nginx")
public class Nginx {
    @CrossOrigin("*")
    @GetMapping("/on_connect")
    public String on_connect(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getRequestURI());
        System.out.println(request.getQueryString());
        return "on_connect";
    }

    @CrossOrigin("*")
    @GetMapping("/on_play")
    public String on_play(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getRequestURI());
        System.out.println(request.getQueryString());
        return "on_play";
    }

    @CrossOrigin("*")
    @GetMapping("/on_publish")
    public String on_publish(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getRequestURI());
        System.out.println(request.getQueryString());
        return "on_publish";
    }

    @CrossOrigin("*")
    @GetMapping("/on_done")
    public String on_done(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getRequestURI());
        System.out.println(request.getQueryString());
        return "on_done";
    }

    @CrossOrigin("*")
    @GetMapping("/on_play_done")
    public String on_play_done(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getRequestURI());
        System.out.println(request.getQueryString());
        return "on_play_done";
    }

    @CrossOrigin("*")
    @GetMapping("/on_publish_done")
    public String on_publish_done(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getRequestURI());
        System.out.println(request.getQueryString());
        return "on_publish_done";
    }

    @CrossOrigin("*")
    @GetMapping("/on_record_done")
    public String on_record_done(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getRequestURI());
        System.out.println(request.getQueryString());
        return "on_record_done";
    }

    @CrossOrigin("*")
    @GetMapping("/on_update")
    public String on_update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println(request.getRequestURI());
        System.out.println(request.getQueryString());
        return "on_update";
    }
}
