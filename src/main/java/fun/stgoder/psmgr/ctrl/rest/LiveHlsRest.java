package fun.stgoder.psmgr.ctrl.rest;


import fun.stgoder.psmgr.common.Code;
import fun.stgoder.psmgr.common.exception.BLException;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.common.model.Resp;
import fun.stgoder.psmgr.model.Hls1;
import fun.stgoder.psmgr.ps.live.Hls;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/live-hls")
public class LiveHlsRest {
    @GetMapping
    public Resp list() {
        List<Hls1> hlss = new ArrayList<>();
        for (Hls hls : Hls.hlss()) {
            hlss.add(Hls1.fromHls(hls));
        }
        return new Resp(Code.REQUEST_OK, hlss);
    }

    @GetMapping("/{key}")
    public Resp liveHls(@PathVariable("key") String key) throws BLException {
        Hls hls = Hls.get(key);
        if (hls == null)
            throw new BLException(Code.REQUEST_ERR, "live hls not exist");
        return new Resp(Code.REQUEST_OK, Hls1.fromHls(hls));
    }

    @PostMapping("/startAndPut")
    public Resp startAndPut(@RequestParam("key") String key,
                            @RequestParam("url") String url,
                            @RequestParam("keepAlive") boolean keepAlive,
                            @RequestParam(value = "cancelAfterSeconds", required = false, defaultValue = "0")
                                    long cancelAfterSeconds) throws BLException, ExecException {
        if (StringUtils.isBlank(key))
            throw new BLException(-1, "key blank");
        if (StringUtils.isBlank(url))
            throw new BLException(-1, "url blank");
        Hls.startAndPut(key, url, keepAlive, cancelAfterSeconds);
        return new Resp(Code.REQUEST_OK);
    }

    @DeleteMapping("/stopAndRemove/{key}")
    public Resp stopAndRemove(@PathVariable("key") String key) {
        Hls.stopAndRemove(key);
        return new Resp(Code.REQUEST_OK);
    }
}
