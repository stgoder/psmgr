package fun.stgoder.psmgr.ctrl.rest;

import fun.stgoder.psmgr.common.Code;
import fun.stgoder.psmgr.common.exception.BLException;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.common.model.Resp;
import fun.stgoder.psmgr.model.Recorder1;
import fun.stgoder.psmgr.ps.recorder.Recorder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/recorder")
public class RecorderRest {
    @GetMapping
    public Resp list() {
        List<Recorder1> recorders = new ArrayList<>();
        for (Recorder recorder : Recorder.recorders()) {
            recorders.add(Recorder1.fromRecorder(recorder));
        }
        return new Resp(Code.REQUEST_OK, recorders);
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
        Recorder.startAndPut(key, url, keepAlive, cancelAfterSeconds);
        return new Resp(Code.REQUEST_OK);
    }

    @DeleteMapping("/stopAndRemove/{key}")
    public Resp stopAndRemove(@PathVariable("key") String key) {
        Recorder.stopAndRemove(key);
        return new Resp(Code.REQUEST_OK);
    }
}
