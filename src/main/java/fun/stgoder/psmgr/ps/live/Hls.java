package fun.stgoder.psmgr.ps.live;

import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.common.OS;
import fun.stgoder.psmgr.common.db.Ds;
import fun.stgoder.psmgr.common.db.HlsEntity;
import fun.stgoder.psmgr.common.db.Param;
import fun.stgoder.psmgr.common.db.Sql;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.ps.Cmd;
import fun.stgoder.psmgr.ps.Ps;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hls {
    private static final Map<String, Hls> hlss;
    private static final StatusChecker statusChecker;

    static {
        hlss = new HashMap<>();
        statusChecker = new StatusChecker();
        statusChecker.start();
    }

    public static synchronized void startAndPut(String key,
                                                String source,
                                                boolean keepAlive,
                                                long cancelAfterSeconds) throws ExecException {
        if (hlss.containsKey(key))
            return;
        long now = System.currentTimeMillis();
        Hls hls = new Hls(key, source)
                .keepAlive(keepAlive)
                .cancelAfterSeconds(cancelAfterSeconds)
                .startStreaming()
                .birthTime(System.currentTimeMillis())
                .upTime(System.currentTimeMillis());
        hlss.put(key, hls);
        Ds.sqlite0.insert(
                new Sql()
                        .insert(HlsEntity.class)
                        .cols(HlsEntity.BCOLS)
                        .values(HlsEntity.VALUES).sql(),
                new Param()
                        .add("key", key)
                        .add("source", source)
                        .add("keep_alive", keepAlive)
                        .add("cancel_after_seconds", cancelAfterSeconds)
                        .add("birth_time", now)
                        .add("up_time", now));
    }

    public static synchronized void stopAndRemove(String key) {
        Hls hls = hlss.get(key);
        if (hls == null)
            return;
        hls.cleanup();
        hlss.remove(key);
        Ds.sqlite0.delete(
                new Sql()
                        .delete(HlsEntity.class)
                        .where("key = :key").sql(),
                new Param("key", key));
    }

    public static void loadFromDB() {
        new Thread(() -> {
            List<HlsEntity> hlsEntities = Ds.sqlite0.select(
                    new Sql()
                            .select(HlsEntity.COLS)
                            .from(HlsEntity.class).sql(), HlsEntity.class);
            for (HlsEntity hlsEntity : hlsEntities) {
                long now = System.currentTimeMillis();
                String key = hlsEntity.getKey();
                Hls hls = new Hls(key, hlsEntity.getSource())
                        .keepAlive(hlsEntity.isKeepAlive())
                        .cancelAfterSeconds(hlsEntity.getCancelAfterSeconds())
                        .birthTime(hlsEntity.getBirthTime())
                        .upTime(now);
                try {
                    hls.startStreaming();
                    hlss.put(key, hls);
                    Ds.sqlite0.update(
                            new Sql()
                                    .update(HlsEntity.class)
                                    .set("up_time = :up_time")
                                    .where("key = :key").sql(),
                            new Param()
                                    .add("up_time", now)
                                    .add("key", key));
                } catch (ExecException e) {
                    e.printStackTrace();
                    System.err.println("load hls: " + key + " err");
                }
            }
        }).start();
    }

    public static Collection<Hls> hlss() {
        return hlss.values();
    }

    public static Hls get(String key) {
        return hlss.get(key);
    }

    private String key;
    private String source;
    private Ps ps;
    private boolean keepAlive;
    private long cancelAfterSeconds;
    private long birthTime;
    private long upTime;

    public Hls(String key, String source) {
        this.key = key;
        this.source = source;
        Cmd cmd = new Cmd();
        String hlsTsDirPath = Constants.HLS_PATH + File.separator + key;
        File hlsTsDir = new File(hlsTsDirPath);
        if (!hlsTsDir.exists())
            hlsTsDir.mkdirs();
        boolean isFile = false;
        try {
            File file = new File(source);
            if (file.isFile() && file.exists())
                isFile = true;
        } catch (Exception e) {
            System.out.println("source not file");
        }
        if (OS.isLINUX() || OS.isMAC()) {
            cmd.add(Constants.FFMPEG_PATH)
                    .add("-fflags")
                    .add("genpts");
            if (!isFile) {
                if (source.startsWith("rtsp"))
                    cmd.add("-rtsp_transport")
                            .add("tcp");
                cmd.add("-stimeout")
                        .add("5000000"); // keep alive
            } else {
                cmd.add("-re");
            }
            cmd.add("-i")
                    .add(source)
                    .add("-c:v")
                    .add("copy")
                    .add("-c:a")
                    .add("aac")
                    .add("-hls_time")
                    .add(String.valueOf(Constants.HLS_TIME))
                    .add("-hls_list_size")
                    .add("10")
                    .add("-hls_wrap")
                    .add("10")
                    .add(hlsTsDirPath + File.separator + "out.m3u8")
                    .add("-loglevel")
                    .add("error");
        }
        if (OS.isWIN()) {
            cmd.add(Constants.FFMPEG_PATH)
                    .add("-fflags")
                    .add("genpts");
            if (!isFile) {
                if (source.startsWith("rtsp"))
                    cmd.add("-rtsp_transport")
                            .add("tcp");
                cmd.add("-stimeout")
                        .add("5000000"); // keep alive
            } else {
                cmd.add("-re");
            }
            cmd.add("-i")
                    .add(source)
                    .add("-c:v")
                    .add("copy")
                    .add("-c:a")
                    .add("aac")
                    .add("-hls_time")
                    .add(String.valueOf(Constants.HLS_TIME))
                    .add("-hls_list_size")
                    .add("10")
                    .add("-hls_wrap")
                    .add("10")
                    .add(hlsTsDirPath + File.separator + "out.m3u8")
                    .add("-loglevel")
                    .add("error");
        }
        this.ps = new Ps(cmd);
    }

    public synchronized Hls startStreaming() throws ExecException {
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
            try {
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
                                    long now = System.currentTimeMillis();
                                    hls.startStreaming()
                                            .upTime(now);
                                    Ds.sqlite0.update(
                                            new Sql()
                                                    .update(HlsEntity.class)
                                                    .set("up_time = :up_time")
                                                    .where("key = :key").sql(),
                                            new Param()
                                                    .add("up_time", now)
                                                    .add("key", key));
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
