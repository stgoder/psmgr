package fun.stgoder.psmgr.model;

import java.util.List;

public class Nginx1 {
    private String psFlg;
    private boolean alive;
    private List<Integer> pids;

    public String getPsFlg() {
        return psFlg;
    }

    public void setPsFlg(String psFlg) {
        this.psFlg = psFlg;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public List<Integer> getPids() {
        return pids;
    }

    public void setPids(List<Integer> pids) {
        this.pids = pids;
    }
}
