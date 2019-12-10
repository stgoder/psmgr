package fun.stgoder.psmgr.ps.live;

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

public class Hls {
    private static final Map<String, Hls> hlss;
    private static final StatusChecker statusChecker;

    static {
        hlss = new HashMap<>();
        statusChecker = new StatusChecker();
        statusChecker.start();
    }

    public static void startAndPut(String key,
                                   String url,
                                   boolean keepAlive,
                                   long cancelAfterSeconds) throws ExecException {
        if (hlss.containsKey(key))
            return;
        Hls hls = new Hls(key, url)
                .keepAlive(keepAlive)
                .cancelAfterSeconds(cancelAfterSeconds)
                .startStreaming()
                .birthTime(System.currentTimeMillis())
                .upTime(System.currentTimeMillis());
        hlss.put(key, hls);
    }

    public static void stopAndRemove(String key) {
        Hls hls = hlss.get(key);
        if (hls == null)
            return;
        hls.cleanup();
        hlss.remove(key);
    }

    public static Collection<Hls> hlss() {
        return hlss.values();
    }

    private String key;
    private String url;
    private Ps ps;
    private boolean keepAlive;
    private long cancelAfterSeconds;
    private long birthTime;
    private long upTime;

    public Hls(String key, String url) {
        this.key = key;
        this.url = url;
        Cmd cmd = new Cmd();
        String hlsTsDirPath = Constants.HLS_PATH + File.separator + key;
        File hlsTsDir = new File(hlsTsDirPath);
        if (!hlsTsDir.exists())
            hlsTsDir.mkdirs();
        if (OS.isLINUX()) {
            cmd.add(Constants.FFMPEG_PATH)
                    .add("-fflags")
                    .add("genpts")
                    //.add("-rtsp_transport")
                    //.add("tcp")
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
                    .add(hlsTsDirPath + File.separator + "out.m3u8");
        }
        if (OS.isWIN()) {
            cmd.add(Constants.FFMPEG_PATH)
                    .add("-fflags")
                    .add("genpts")
                    //.add("-rtsp_transport")
                    //.add("tcp")
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
                    .add(hlsTsDirPath + File.separator + "out.m3u8");
        }
        this.ps = new Ps(cmd);
    }

    public Hls startStreaming() throws ExecException {
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

    public Hls keepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    public long cancelAfterSeconds() {
        return cancelAfterSeconds;
    }

    public Hls cancelAfterSeconds(long cancelAfterSeconds) {
        this.cancelAfterSeconds = cancelAfterSeconds;
        return this;
    }

    public long birthTime() {
        return birthTime;
    }

    public Hls birthTime(long birthTime) {
        this.birthTime = birthTime;
        return this;
    }

    public long upTime() {
        return upTime;
    }

    public Hls upTime(long upTime) {
        this.upTime = upTime;
        return this;
    }

    public static void main(String[] args) throws ExecException, InterruptedException, IOException {
        Hls.startAndPut("cccc", "rtsp://192.168.1.136/86", true, 60);
    }
}

class StatusChecker extends Thread {
    @Override
    public void run() {
        while (true) {
            for (Hls hls : Hls.hlss()) {
                String key = hls.key();
                boolean shouldBeCancelled = hls.cancelAfterSeconds() <= 0 ? false :
                        ((System.currentTimeMillis() - hls.birthTime()
                                >= hls.cancelAfterSeconds() * 1000) ? true : false);
                if (shouldBeCancelled) {
                    System.out.println("hls should be cancelled");
                    Hls.stopAndRemove(key);
                } else {
                    if (hls.keepAlive()) {
                        if (!hls.isAlive()) {
                            System.out.println("ps " + key + " exited, pull up");
                            try {
                                hls.startStreaming()
                                        .upTime(System.currentTimeMillis());
                            } catch (ExecException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            try {
                sleep(1000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
