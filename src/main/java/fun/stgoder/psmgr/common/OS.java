package fun.stgoder.psmgr.common;

import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.ps.Cmd;
import fun.stgoder.psmgr.ps.Out;
import fun.stgoder.psmgr.ps.Ps;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public enum OS {
    LINUX, WIN, MAC;
    private static OS os;
    public static String WIN_WORK_DISK;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.indexOf("linux") >= 0)
            os = OS.LINUX;
        if (osName.indexOf("windows") >= 0) {
            os = OS.WIN;
            WIN_WORK_DISK = System.getProperty("user.dir").substring(0, 2);
        }
        if (osName.indexOf("mac") >= 0)
            os = OS.MAC;
    }

    public static List<Integer> pids(String name) throws ExecException {
        long s = System.currentTimeMillis();
        List<Integer> pids = new ArrayList<>();
        if (OS.isLINUX() || OS.isMAC()) {
            Out out = new Ps(
                    new Cmd()
                            .add("/bin/bash")
                            .add("-c")
                            .add("pgrep -f " + name))
                    .exec();
            int exitValue = out.getExitValue();
            if (exitValue != 0 && StringUtils.isBlank(out.getOutput())) {
                return pids;
            }
            String outputStr = StringUtils.trim(out.getOutput());
            outputStr = StringUtils.strip(outputStr);
            String[] pidStrs = outputStr.split("\n");
            for (String pidStr : pidStrs) {
                if (StringUtils.isNotBlank(pidStr)) {
                    try {
                        int pid = Integer.valueOf(pidStr);
                        pids.add(pid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (OS.isWIN()) {
            Out out = new Ps(
                    new Cmd()
                            .add("cmd")
                            .add("/c")
                            .add("tasklist")
                            .add("/fi")
                            .add("\"imagename eq " + name + "\"")
                            .add("/nh"))
                    .exec();
            int exitValue = out.getExitValue();
            if (exitValue != 0 && StringUtils.isBlank(out.getOutput())) {
                return pids;
            }
            String outputStr = StringUtils.trim(out.getOutput());
            outputStr = StringUtils.strip(outputStr);
            String[] lines = outputStr.split("\n");
            for (String line : lines) {
                if (StringUtils.isNotBlank(line)) {
                    String[] strs = line.split(" ");
                    List<String> tmps = new ArrayList<>();
                    for (String str : strs) {
                        if (StringUtils.isNotBlank(str))
                            tmps.add(str);
                    }
                    String pidStr = tmps.get(1);
                    try {
                        int pid = Integer.valueOf(pidStr);
                        pids.add(pid);
                    } catch (Exception e) {
                        System.out.println("parse pid err: " + e.getMessage());
                    }
                }
            }
        }
        long e = System.currentTimeMillis();
        System.out.println("pids(" + name + "): " + (e - s));
        return pids;
    }

    public static boolean isLINUX() {
        return os == OS.LINUX ? true : false;
    }

    public static boolean isWIN() {
        return os == OS.WIN ? true : false;
    }

    public static boolean isMAC() {
        return os == OS.MAC ? true : false;
    }

    public static String convertLinuxPathToWin(String path) {
        String[] pathStrs = path.split("/");
        String winPath = WIN_WORK_DISK;
        for (String pathStr : pathStrs) {
            if (StringUtils.isNotBlank(pathStr))
                winPath += "\\" + pathStr;
        }
        return winPath;
    }

    public static void main(String[] args) throws Exception {
        Out out = new Ps(
                new Cmd()
                        .add("cmd.exe")
                        .add("/c")
                        .add(Constants.FFMPEG_PATH)).exec();
        System.out.println(out.getExitValue());
        System.out.println(out.getOutput());
        System.out.println(out.getError());
    }
}
