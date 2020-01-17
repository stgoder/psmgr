package fun.stgoder.psmgr.model;

import fun.stgoder.psmgr.ps.pusher.Pusher;

public class Pusher1 {
    private String key;
    private String source;
    private String rtmpUrl;
    private boolean keepAlive;
    private long cancelAfterSeconds;
    private long birthTime;
    private long upTime;
    private boolean alive;

    public static Pusher1 fromPusher(Pusher pusher) {
        Pusher1 pusher1 = new Pusher1();
        pusher1.setKey(pusher.key());
        pusher1.setSource(pusher.source());
        pusher1.setRtmpUrl(pusher.rtmpUrl());
        pusher1.setKeepAlive(pusher.keepAlive());
        pusher1.setCancelAfterSeconds(pusher.cancelAfterSeconds());
        pusher1.setBirthTime(pusher.birthTime());
        pusher1.setUpTime(pusher.upTime());
        pusher1.setAlive(pusher.isAlive());
        return pusher1;
    }

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

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
