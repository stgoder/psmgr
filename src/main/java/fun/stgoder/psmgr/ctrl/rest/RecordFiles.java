package fun.stgoder.psmgr.ctrl.rest;

import fun.stgoder.psmgr.common.Code;
import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.common.model.Resp;
import fun.stgoder.psmgr.model.RecordFile;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/record-files")
public class RecordFiles {
    @GetMapping
    public Resp list() {
        List<RecordFile> recordFiles = new ArrayList<>();
        File recordDir = new File(Constants.RECORD_PATH);
        final File[] files = recordDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                recordFiles.add(new RecordFile(file.getName(), file.lastModified()));
            }
        }
        return new Resp(Code.REQUEST_OK, recordFiles);
    }

    @DeleteMapping("/{key}")
    public Resp delete(@PathVariable("key") String key) throws IOException {
        File recordFileDir = new File(Constants.RECORD_PATH + File.separator + key);
        if (recordFileDir.exists())
            FileUtils.deleteDirectory(recordFileDir);
        return new Resp(Code.REQUEST_OK);
    }
}
