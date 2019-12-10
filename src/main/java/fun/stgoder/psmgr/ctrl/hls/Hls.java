package fun.stgoder.psmgr.ctrl.hls;

import fun.stgoder.psmgr.common.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
@RequestMapping("/hls")
public class Hls {
    @CrossOrigin("*")
    @GetMapping("/record/{key}/out.m3u8")
    public void m3u8(@PathVariable("key") String key,
                     HttpServletRequest request,
                     HttpServletResponse response) throws IOException {
        File recordM3u8 = new File(Constants.RECORD_PATH + File.separator + key + File.separator + "out.m3u8");
        if (!recordM3u8.exists())
            return;
        try (final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(recordM3u8));
             OutputStream outputStream = response.getOutputStream()) {
            final byte[] buffer = new byte[1024 * 512];
            int len = 0;
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        }
    }

    @CrossOrigin("*")
    @GetMapping("/record/{key}/{filename}")
    public void ts(@PathVariable("key") String key,
                   @PathVariable("filename") String filename,
                   HttpServletRequest request,
                   HttpServletResponse response) throws IOException {
        File tsFile = new File(Constants.RECORD_PATH + File.separator + key + File.separator + filename);
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
    public ModelAndView play(@PathVariable("key") String key, ModelAndView mv) throws Exception {
        mv.setViewName("play-hls");
        String url = "http://" + Constants.localIpv4 + ":" +
                Constants.SERVER_PORT + Constants.SERVER_SERVLET_CONTEXT_PATH +
                "/hls/record/" + key + "/out.m3u8";
        mv.addObject("url", url);
        return mv;
    }
}
