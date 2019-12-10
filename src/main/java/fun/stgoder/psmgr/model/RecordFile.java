package fun.stgoder.psmgr.model;

public class RecordFile {
    private String key;
    private long time;

    public RecordFile() {
    }

    public RecordFile(String key, long time) {
        this.key = key;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
