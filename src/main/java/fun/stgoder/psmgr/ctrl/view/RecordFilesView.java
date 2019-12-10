package fun.stgoder.psmgr.ctrl.view;

import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.model.RecordFile;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/record-files")
public class RecordFilesView {
    @GetMapping({"/", ""})
    public ModelAndView recordFiles(ModelAndView mv) {
        mv.setViewName("record-files");
        mv.addObject("title", "record-files");
        mv.addObject("tab", "record-files");
        List<RecordFile> recordFiles = new ArrayList<>();
        File recordDir = new File(Constants.RECORD_PATH);
        final File[] files = recordDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                recordFiles.add(new RecordFile(file.getName(), file.lastModified()));
            }
        }
        mv.addObject("recordFiles", recordFiles);
        return mv;
    }

    @PostMapping("/delete")
    public ModelAndView deleteRecordFile(@RequestParam("key") String key, ModelAndView mv) throws IOException {
        mv.setViewName("redirect:/record-files/");
        File recordFileDir = new File(Constants.RECORD_PATH + File.separator + key);
        if (recordFileDir.exists())
            FileUtils.deleteDirectory(recordFileDir);
        return mv;
    }
}
