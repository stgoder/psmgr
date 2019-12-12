package fun.stgoder.psmgr.model;

import fun.stgoder.psmgr.ps.live.Hls;

public class Hls1 {
    private String key;
    private String url;
    private boolean keepAlive;
    private long cancelAfterSeconds;
    private long birthTime;
    private long upTime;

    public static Hls1 fromHls(Hls hls) {
        Hls1 hls1 = new Hls1();
        hls1.setKey(hls.key());
        hls1.setUrl(hls.url());
        hls1.setKeepAlive(hls.keepAlive());
        hls1.setCancelAfterSeconds(hls.cancelAfterSeconds());
        hls1.setBirthTime(hls.birthTime());
        hls1.setUpTime(hls.upTime());
        return hls1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
