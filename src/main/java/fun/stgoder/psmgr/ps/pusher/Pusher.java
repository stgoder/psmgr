package fun.stgoder.psmgr.ps.pusher;

import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.common.OS;
import fun.stgoder.psmgr.common.db.Ds;
import fun.stgoder.psmgr.common.db.Param;
import fun.stgoder.psmgr.common.db.PusherEntity;
import fun.stgoder.psmgr.common.db.Sql;
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
                                                String source,
                                                String rtmpUrl,
                                                boolean keepAlive,
                                                long cancelAfterSeconds) throws ExecException {
        if (pushers.containsKey(key))
            return;
        long now = System.currentTimeMillis();
        Pusher pusher = new Pusher(key, source, rtmpUrl)
                .keepAlive(keepAlive)
                .cancelAfterSeconds(cancelAfterSeconds)
                .pushRtmp()
                .birthTime(now)
                .upTime(now);
        pushers.put(key, pusher);
        Ds.sqlite0.insert(
                new Sql()
                        .insert(PusherEntity.class)
                        .cols(PusherEntity.BCOLS)
                        .values(PusherEntity.VALUES).sql(),
                new Param()
                        .add("key", key)
                        .add("source", source)
                        .add("rtmp_url", rtmpUrl)
                        .add("keep_alive", keepAlive)
                        .add("cancel_after_seconds", cancelAfterSeconds)
                        .add("birth_time", now)
                        .add("up_time", now));
    }

    public static synchronized void stopAndRemove(String key) {
        Pusher pusher = pushers.get(key);
        if (pusher == null)
            return;
        pusher.cleanup();
        pushers.remove(key);
        Ds.sqlite0.delete(
                new Sql()
                        .delete(PusherEntity.class)
                        .where("key = :key").sql(),
                new Param("key", key));
    }

    public static void loadFromDB() {
        new Thread(() -> {
            List<PusherEntity> pusherEntities = Ds.sqlite0.select(
                    new Sql()
                            .select(PusherEntity.COLS)
                            .from(PusherEntity.class).sql(), PusherEntity.class);
            for (PusherEntity pusherEntity : pusherEntities) {
                long now = System.currentTimeMillis();
                String key = pusherEntity.getKey();
                Pusher pusher = new Pusher(key, pusherEntity.getSource(), pusherEntity.getRtmpUrl())
                        .keepAlive(pusherEntity.isKeepAlive())
                        .cancelAfterSeconds(pusherEntity.getCancelAfterSeconds())
                        .birthTime(pusherEntity.getBirthTime())
                        .upTime(now);
                try {
                    pusher.pushRtmp();
                    pushers.put(key, pusher);
                    Ds.sqlite0.update(
                            new Sql()
                                    .update(PusherEntity.class)
                                    .set("up_time = :up_time")
                                    .where("key = :key").sql(),
                            new Param()
                                    .add("up_time", now)
                                    .add("key", key));
                } catch (ExecException e) {
                    e.printStackTrace();
                    System.err.println("load pusher: " + key + " err");
                }
            }
        }).start();
    }

    public static synchronized List<String> reloadAllPushers() {
        List<String> pullFailedPusherKeys = new ArrayList<>();
        for (Pusher pusher : pushers.values()) {
            try {
                long now = System.currentTimeMillis();
                pusher.pushRtmp().upTime(System.currentTimeMillis());
                Ds.sqlite0.update(
                        new Sql()
                                .update(PusherEntity.class)
                                .set("up_time = :up_time")
                                .where("key = :key").sql(),
                        new Param()
                                .add("up_time", now)
                                .add("key", pusher.key()));
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
    private String source;
    private String rtmpUrl;
    private Ps ps;
    private boolean keepAlive;
    private long cancelAfterSeconds;
    private long birthTime;
    private long upTime;

    public Pusher(String key, String source, String rtmpUrl) {
        this.key = key;
        this.source = source;
        this.rtmpUrl = rtmpUrl;
        boolean isFile = false;
        try {
            File file = new File(source);
            if (file.isFile() && file.exists())
                isFile = true;
        } catch (Exception e) {
            System.out.println("source not file");
        }
        Cmd cmd = new Cmd();
        if (OS.isLINUX() || OS.isMAC()) {
            cmd.add(Constants.FFMPEG_PATH);
            if (!isFile) {
                if (source.startsWith("rtsp"))
                    cmd.add("-rtsp_transport")
                            .add("tcp");
                cmd.add("-stimeout")
                        .add("5000000"); // keep alive
            }
            cmd.add("-re")
                    .add("-i")
                    .add(source)
                    .add("-c:v")
                    .add("copy")
                    .add("-c:a")
                    .add("aac")
                    .add("-f")
                    .add("flv");
            if (!isFile) {
                cmd.add("-stimeout")
                        .add("5000000"); // keep alive
            }
            cmd.add(rtmpUrl)
                    .add("-loglevel")
                    .add("error");
        }
        if (OS.isWIN()) {
            cmd.add(Constants.FFMPEG_PATH);
            if (!isFile) {
                if (source.startsWith("rtsp"))
                    cmd.add("-rtsp_transport")
                            .add("tcp");
                cmd.add("-stimeout")
                        .add("5000000"); // keep alive
            }
            cmd.add("-re")
                    .add("-i")
                    .add(source)
                    .add("-c:v")
                    .add("copy")
                    .add("-c:a")
                    .add("aac")
                    .add("-f")
                    .add("flv");
            if (!isFile) {
                cmd.add("-stimeout")
                        .add("5000000"); // keep alive
            }
            cmd.add(rtmpUrl)
                    .add("-loglevel")
                    .add("error");
        }
        this.ps = new Ps(cmd);
    }

    public synchronized Pusher pushRtmp() throws ExecException {
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

    public String source() {
        return source;
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
                                    long now = System.currentTimeMillis();
                                    pusher.pushRtmp()
                                            .upTime(now);
                                    Ds.sqlite0.update(
                                            new Sql()
                                                    .update(PusherEntity.class)
                                                    .set("up_time = :up_time")
                                                    .where("key = :key").sql(),
                                            new Param()
                                                    .add("up_time", now)
                                                    .add("key", pusher.key()));
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
