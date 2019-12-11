package fun.stgoder.psmgr.ps.recorder;

import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.common.OS;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.ps.Cmd;
import fun.stgoder.psmgr.ps.Ps;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Recorder {
    private static final Map<String, Recorder> recorders;
    private static final StatusChecker statusChecker;

    static {
        recorders = new HashMap<>();
        statusChecker = new StatusChecker();
        statusChecker.start();
    }

    public static void startAndPut(String key,
                                   String url,
                                   boolean keepAlive,
                                   long cancelAfterSeconds) throws ExecException {
        if (recorders.containsKey(key))
            return;
        Recorder recorder = new Recorder(key, url)
                .keepAlive(keepAlive)
                .cancelAfterSeconds(cancelAfterSeconds)
                .recordStream()
                .birthTime(System.currentTimeMillis())
                .upTime(System.currentTimeMillis());
        recorders.put(key, recorder);
    }

    public static void stopAndRemove(String key) {
        Recorder recorder = recorders.get(key);
        if (recorder == null)
            return;
        recorder.cleanup();
        recorders.remove(key);
    }

    public static Collection<Recorder> recorders() {
        return recorders.values();
    }

    private String key;
    private String url;
    private Ps ps;
    private boolean keepAlive;
    private long cancelAfterSeconds;
    private long birthTime;
    private long upTime;

    public Recorder(String key, String url) {
        this.key = key;
        this.url = url;
        Cmd cmd = new Cmd();
        String recordTsDirPath = Constants.RECORD_PATH + File.separator + key;
        File recordTsDir = new File(recordTsDirPath);
        if (!recordTsDir.exists())
            recordTsDir.mkdirs();
        if (OS.isLINUX()) {
            cmd.add(Constants.FFMPEG_PATH)
                    .add("-fflags")
                    .add("genpts")
                    .add("-rtsp_transport")
                    .add("tcp")
                    .add("-i")
                    .add(url)
                    .add("-c:v")
                    .add("copy")
                    .add("-c:a")
                    .add("aac")
                    .add("-hls_time")
                    .add(String.valueOf(Constants.HLS_TIME))
                    .add("-hls_list_size")
                    .add("0")
                    .add(recordTsDirPath + File.separator + "out.m3u8");
        }
        if (OS.isWIN()) {
            cmd.add(Constants.FFMPEG_PATH)
                    .add("-fflags")
                    .add("genpts")
                    .add("-rtsp_transport")
                    .add("tcp")
                    .add("-i")
                    .add(url)
                    .add("-c:v")
                    .add("copy")
                    .add("-c:a")
                    .add("aac")
                    .add("-hls_time")
                    .add(String.valueOf(Constants.HLS_TIME))
                    .add("-hls_list_size")
                    .add("0")
                    .add(recordTsDirPath + File.separator + "out.m3u8");
        }
        this.ps = new Ps(cmd);
    }

    public Recorder recordStream() throws ExecException {
        ps.execRedirect(new File(Constants.PSLOG_PATH + File.separator + key + ".log"));
        return this;
    }

    public void cleanup() {
        ps.cleanup();
    }

    public boolean isAlive() {
        return ps.isAlive();
    }

    public String key() {
        return key;
    }

    public String url() {
        return url;
    }

    public Ps ps() {
        return ps;
    }

    public boolean keepAlive() {
        return keepAlive;
    }

    public Recorder keepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    public long cancelAfterSeconds() {
        return cancelAfterSeconds;
    }

    public Recorder cancelAfterSeconds(long cancelAfterSeconds) {
        this.cancelAfterSeconds = cancelAfterSeconds;
        return this;
    }

    public long birthTime() {
        return birthTime;
    }

    public Recorder birthTime(long birthTime) {
        this.birthTime = birthTime;
        return this;
    }

    public long upTime() {
        return upTime;
    }

    public Recorder upTime(long upTime) {
        this.upTime = upTime;
        return this;
    }

    public static void main(String[] args) throws ExecException, InterruptedException, IOException {
        Recorder.startAndPut("cccc", "rtsp://192.168.1.136/86", true, 60);
    }
}

class StatusChecker extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                for (Recorder recorder : Recorder.recorders()) {
                    String key = recorder.key();
                    boolean shouldBeCancelled = recorder.cancelAfterSeconds() <= 0 ? false :
                            ((System.currentTimeMillis() - recorder.birthTime()
                                    >= recorder.cancelAfterSeconds() * 1000) ? true : false);
                    if (shouldBeCancelled) {
                        System.out.println("recorder should be cancelled");
                        Recorder.stopAndRemove(key);
                    } else {
                        if (recorder.keepAlive()) {
                            if (!recorder.isAlive()) {
                                System.out.println("ps " + key + " exited, pull up");
                                try {
                                    recorder.recordStream()
                                            .upTime(System.currentTimeMillis());
                                } catch (ExecException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(1000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
