package fun.stgoder.psmgr.ps;

import java.util.LinkedList;
import java.util.List;

public class Cmd {
    private LinkedList<String> cmds = new LinkedList<>();

    public Cmd() {
    }

    public Cmd(String cmd) {
        cmds.add(cmd);
    }

    public Cmd add(String cmd) {
        cmds.addLast(cmd);
        return this;
    }

    public Cmd addFirst(String cmd) {
        cmds.addFirst(cmd);
        return this;
    }

    public Cmd plus(Cmd cmd1) {
        cmd1.cmds().forEach(cmd -> {
            cmds.addLast(cmd);
        });
        return this;
    }

    public List<String> cmds() {
        return cmds;
    }
}
