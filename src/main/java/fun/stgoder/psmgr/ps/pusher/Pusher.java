package fun.stgoder.psmgr.ps.pusher;

import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.common.OS;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.ps.Cmd;
import fun.stgoder.psmgr.ps.Ps;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Pusher {
    private static final Map<String, Pusher> pushers;
    private static final StatusChecker statusChecker;

    static {
        pushers = new HashMap<>();
        statusChecker = new StatusChecker();
        statusChecker.start();
    }

    public static synchronized void startAndPut(String key,
                                                String rtspUrl,
                                                String rtmpUrl,
                                                boolean keepAlive,
                                                long cancelAfterSeconds) throws ExecException {
        if (pushers.containsKey(key))
            return;
        Pusher pusher = new Pusher(key, rtspUrl, rtmpUrl)
                .keepAlive(keepAlive)
                .cancelAfterSeconds(cancelAfterSeconds)
                .pullRtspPushRtmp()
                .birthTime(System.currentTimeMillis())
                .upTime(System.currentTimeMillis());
        pushers.put(key, pusher);
    }

    public static synchronized void stopAndRemove(String key) {
        Pusher pusher = pushers.get(key);
        if (pusher == null)
            return;
        pusher.cleanup();
        pushers.remove(key);
    }

    public static synchronized List<String> reloadAllPushers() {
        List<String> pullFailedPusherKeys = new ArrayList<>();
        for (Pusher pusher : pushers.values()) {
            try {
                pusher.pullRtspPushRtmp().upTime(System.currentTimeMillis());
                System.out.println("reload pusher succ key:" + pusher.key());
            } catch (Exception e) {
                e.printStackTrace();
                pullFailedPusherKeys.add(pusher.key());
            }
        }
        return pullFailedPusherKeys;
    }

    public static Collection<Pusher> pushers() {
        return pushers.values();
    }

    public static Pusher get(String key) {
        return pushers.get(key);
    }

    private String key;
    private String rtspUrl;
    private String rtmpUrl;
    private Ps ps;
    private boolean keepAlive;
    private long cancelAfterSeconds;
    private long birthTime;
    private long upTime;

    public Pusher(String key, String rtspUrl, String rtmpUrl) {
        this.key = key;
        this.rtspUrl = rtspUrl;
        this.rtmpUrl = rtmpUrl;

        Cmd cmd = new Cmd();
        if (OS.isLINUX() || OS.isMAC()) {
            cmd.add(Constants.FFMPEG_PATH)
                    .add("-rtsp_transport")
                    .add("tcp")
                    .add("-re")
                    .add("-i")
                    .add(rtspUrl)
                    .add("-c:v")
                    .add("copy")
                    .add("-c:a")
                    .add("aac")
                    .add("-rtsp_transport")
                    .add("tcp")
                    .add("-f")
                    .add("flv")
                    .add(rtmpUrl)
                    .add("-loglevel")
                    .add("error");
        }
        if (OS.isWIN()) {
            cmd.add(Constants.FFMPEG_PATH)
                    .add("-rtsp_transport")
                    .add("tcp")
                    .add("-re")
                    .add("-i")
                    .add(rtspUrl)
                    .add("-c:v")
                    .add("copy")
                    .add("-c:a")
                    .add("aac")
                    .add("-rtsp_transport")
                    .add("tcp")
                    .add("-f")
                    .add("flv")
                    .add(rtmpUrl)
                    .add("-loglevel")
                    .add("error");
        }
        this.ps = new Ps(cmd);
    }

    public synchronized Pusher pullRtspPushRtmp() throws ExecException {
        ps.execRedirect(new File(Constants.PSLOG_PATH + File.separator + key + ".log"));
        return this;
    }

    public synchronized void cleanup() {
        ps.cleanup();
    }

    public boolean isAlive() {
        return ps.isAlive();
    }

    public String key() {
        return key;
    }

    public String rtspUrl() {
        return rtspUrl;
    }

    public String rtmpUrl() {
        return rtmpUrl;
    }

    public Ps ps() {
        return ps;
    }

    public boolean keepAlive() {
        return keepAlive;
    }

    public Pusher keepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    public long cancelAfterSeconds() {
        return cancelAfterSeconds;
    }

    public Pusher cancelAfterSeconds(long cancelAfterSeconds) {
        this.cancelAfterSeconds = cancelAfterSeconds;
        return this;
    }

    public long birthTime() {
        return birthTime;
    }

    public Pusher birthTime(long birthTime) {
        this.birthTime = birthTime;
        return this;
    }

    public long upTime() {
        return upTime;
    }

    public Pusher upTime(long upTime) {
        this.upTime = upTime;
        return this;
    }

    public static void main(String[] args) throws ExecException, InterruptedException, IOException {
        Pusher.startAndPut("831",
                "rtsp://192.168.1.136/831",
                "rtmp://192.168.1.136/live/831", true, 60);
    }
}

class StatusChecker extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                for (Pusher pusher : Pusher.pushers()) {
                    String key = pusher.key();
                    boolean shouldBeCancelled = pusher.cancelAfterSeconds() <= 0 ? false :
                            ((System.currentTimeMillis() - pusher.birthTime()
                                    >= pusher.cancelAfterSeconds() * 1000) ? true : false);
                    if (shouldBeCancelled) {
                        System.out.println("pusher should be cancelled");
                        Pusher.stopAndRemove(key);
                    } else {
                        if (pusher.keepAlive()) {
                            if (!pusher.isAlive()) {
                                System.out.println("ps " + key + " exited, pull up");
                                try {
                                    pusher.pullRtspPushRtmp()
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
                sleep(1000 * 30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
