package fun.stgoder.psmgr.ctrl.rest;

import fun.stgoder.psmgr.common.Code;
import fun.stgoder.psmgr.common.exception.BLException;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.common.model.Resp;
import fun.stgoder.psmgr.ps.pusher.Pusher;
import fun.stgoder.psmgr.model.Pusher1;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/pusher")
public class PusherRest {
    @GetMapping
    public Resp list() {
        List<Pusher1> pushers = new ArrayList<>();
        for (Pusher pusher : Pusher.pushers()) {
            pushers.add(Pusher1.fromPusher(pusher));
        }
        return new Resp(Code.REQUEST_OK, "list", pushers);
    }

    @PostMapping("/startAndPut")
    public Resp start(@RequestParam("key") String key,
                      @RequestParam("rtspUrl") String rtspUrl,
                      @RequestParam("rtmpUrl") String rtmpUrl,
                      @RequestParam("keepAlive") boolean keepAlive,
                      @RequestParam(value = "cancelAfterSeconds", required = false, defaultValue = "0")
                              long cancelAfterSeconds) throws ExecException, BLException {
        if (StringUtils.isBlank(key))
            throw new BLException(-1, "key blank");
        if (StringUtils.isBlank(rtspUrl))
            throw new BLException(-1, "rtspUrl blank");
        if (StringUtils.isBlank(rtmpUrl))
            throw new BLException(-1, "rtmpUrl blank");
        Pusher.startAndPut(key, rtspUrl, rtmpUrl, keepAlive, cancelAfterSeconds);
        return new Resp(Code.REQUEST_OK);
    }

    @DeleteMapping("/stopAndRemove/{key}")
    public Resp stopAndRemove(@PathVariable("key") String key) {
        Pusher.stopAndRemove(key);
        return new Resp(Code.REQUEST_OK);
    }
}
