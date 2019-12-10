package fun.stgoder.psmgr.ctrl.hls;

import fun.stgoder.psmgr.common.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/hls")
public class Hls {
    @CrossOrigin("*")
    @GetMapping("/{type}/{key}/out.m3u8")
    public void m3u8(@PathVariable("type") String type,
                     @PathVariable("key") String key,
                     HttpServletRequest request,
                     HttpServletResponse response) throws IOException {
        String m3u8Path;
        if (StringUtils.equals(type, "record")) {
            m3u8Path = Constants.RECORD_PATH + File.separator + key + File.separator + "out.m3u8";
        } else {
            m3u8Path = Constants.HLS_PATH + File.separator + key + File.separator + "out.m3u8";
        }
        File m3u8File = new File(m3u8Path);
        if (!m3u8File.exists())
            return;
        try (final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(m3u8File));
             OutputStream outputStream = response.getOutputStream()) {
            final byte[] buffer = new byte[1024 * 512];
            int len = 0;
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        }
    }

    @CrossOrigin("*")
    @GetMapping("/{type}/{key}/{filename}")
    public void ts(@PathVariable("type") String type,
                   @PathVariable("key") String key,
                   @PathVariable("filename") String filename,
                   HttpServletRequest request,
                   HttpServletResponse response) throws IOException {
        String tsPath;
        if (StringUtils.equals(type, "record")) {
            tsPath = Constants.RECORD_PATH + File.separator + key + File.separator + filename;
        } else {
            tsPath = Constants.HLS_PATH + File.separator + key + File.separator + filename;
        }
        File tsFile = new File(tsPath);
        if (!tsFile.exists())
            return;
        try (final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(tsFile));
             OutputStream outputStream = response.getOutputStream()) {
            final byte[] buffer = new byte[1024 * 512];
            int len = 0;
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        }
    }

    @GetMapping("/play-record/{key}")
    public ModelAndView playRecord(@PathVariable("key") String key, ModelAndView mv) throws Exception {
        mv.setViewName("play-hls");
        String url = "http://" + Constants.localIpv4 + ":" +
                Constants.SERVER_PORT + Constants.SERVER_SERVLET_CONTEXT_PATH +
                "/hls/record/" + key + "/out.m3u8";
        mv.addObject("url", url);
        return mv;
    }

    @GetMapping("/play-live/{key}")
    public ModelAndView playLive(@PathVariable("key") String key, ModelAndView mv) throws Exception {
        mv.setViewName("play-hls");
        String url = "http://" + Constants.localIpv4 + ":" +
                Constants.SERVER_PORT + Constants.SERVER_SERVLET_CONTEXT_PATH +
                "/hls/live/" + key + "/out.m3u8";
        mv.addObject("url", url);
        return mv;
    }
}
