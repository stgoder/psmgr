package fun.stgoder.psmgr.common.db;


import fun.stgoder.psmgr.common.db.annotation.col;
import fun.stgoder.psmgr.common.db.annotation.tbl;

@tbl("pusher")
public class PusherEntity {
    public static final String BCOLS = "key, source, rtmp_url, keep_alive, cancel_after_seconds, birth_time, up_time";
    public static final String COLS = "key, " +
            "source, " +
            "rtmp_url as rtmpUrl, " +
            "keep_alive as keepAlive, " +
            "cancel_after_seconds as cancelAfterSeconds, " +
            "birth_time as birthTime, " +
            "up_time as upTime";
    public static final String VALUES = ":key, :source, :rtmp_url, :keep_alive, " +
            ":cancel_after_seconds, :birth_time, :up_time";
    @col(pk = true, nn = true)
    private String key;
    @col(nn = true, len = 255)
    private String source;
    @col(value = "rtmp_url", nn = true, len = 255)
    private String rtmpUrl;
    @col(value = "keep_alive", nn = true)
    private boolean keepAlive;
    @col(value = "cancel_after_seconds", nn = true)
    private long cancelAfterSeconds;
    @col(value = "birth_time", nn = true)
    private long birthTime;
    @col(value = "up_time", nn = true)
    private long upTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public long getCancelAfterSeconds() {
        return cancelAfterSeconds;
    }

    public void setCancelAfterSeconds(long cancelAfterSeconds) {
        this.cancelAfterSeconds = cancelAfterSeconds;
    }

    public long getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(long birthTime) {
        this.birthTime = birthTime;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }
}
