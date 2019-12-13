package fun.stgoder.psmgr.model;

import fun.stgoder.psmgr.ps.recorder.Recorder;

public class Recorder1 {
    private String key;
    private String url;
    private boolean keepAlive;
    private long cancelAfterSeconds;
    private long birthTime;
    private long upTime;
    private boolean alive;

    public static Recorder1 fromRecorder(Recorder recorder) {
        Recorder1 recorder1 = new Recorder1();
        recorder1.setKey(recorder.key());
        recorder1.setUrl(recorder.url());
        recorder1.setKeepAlive(recorder.keepAlive());
        recorder1.setCancelAfterSeconds(recorder.cancelAfterSeconds());
        recorder1.setBirthTime(recorder.birthTime());
        recorder1.setUpTime(recorder.upTime());
        recorder1.setAlive(recorder.isAlive());
        return recorder1;
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

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
