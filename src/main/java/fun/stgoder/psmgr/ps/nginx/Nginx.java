package fun.stgoder.psmgr.ps.nginx;

import fun.stgoder.psmgr.common.Constants;
import fun.stgoder.psmgr.common.OS;
import fun.stgoder.psmgr.common.exception.ExecException;
import fun.stgoder.psmgr.ps.Cmd;
import fun.stgoder.psmgr.ps.Out;
import fun.stgoder.psmgr.ps.Ps;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Nginx {
    public static final String NGINX_BIN_FILENAME;

    static {
        if (OS.isLINUX()) {
            NGINX_BIN_FILENAME = "nginx.linux";
        } else if (OS.isWIN()) {
            NGINX_BIN_FILENAME = "nginx.win";
        } else if (OS.isMAC()) {
            NGINX_BIN_FILENAME = "nginx.mac";
        } else {
            NGINX_BIN_FILENAME = "nginx.linux";
        }
    }

    public synchronized static void init() throws IOException, ExecException {
        File nginxDir = new File(Constants.NGINX_PATH);
        if (!nginxDir.exists())
            nginxDir.mkdirs();

        File nginx = new File(Constants.NGINX_PATH + File.separator + NGINX_BIN_FILENAME);
        if (!nginx.exists()) {
            try (final InputStream nginxInputStream =
                         Nginx.class.getResourceAsStream("/" + NGINX_BIN_FILENAME)) {
                if (nginxInputStream == null)
                    throw new IOException("read nginx bin err");
                FileUtils.copyInputStreamToFile(nginxInputStream, nginx);
            }
        }

        File nginxConf = new File(Constants.NGINX_PATH + File.separator +
                "conf" + File.separator + "nginx.conf");
        if (!nginxConf.exists()) {
            try (final InputStream nginxConfInputStream = Nginx.class.getResourceAsStream("/nginx.conf")) {
                if (nginxConfInputStream == null)
                    throw new IOException("read nginx conf err");
                FileUtils.copyInputStreamToFile(nginxConfInputStream, nginxConf);
            }
        }

        if (OS.isLINUX() || OS.isMAC()) {
            final Out out = new Ps(
                    new Cmd()
                            .add("/bin/bash")
                            .add("-c")
                            .add("chmod +x " + Constants.NGINX_PATH + File.separator + NGINX_BIN_FILENAME)).exec();
            if (out.getExitValue() != 0) {
                System.out.println(out.toString());
                throw new ExecException(-1, out.toString());
            }
        }

        File nginxLogsDir = new File(Constants.NGINX_PATH + File.separator + "logs");
        if (!nginxLogsDir.exists())
            nginxLogsDir.mkdirs();

        File nginxTempDir = new File(Constants.NGINX_PATH + File.separator + "temp");
        if (!nginxTempDir.exists())
            nginxTempDir.mkdirs();
    }

    public synchronized static void start() throws ExecException {
        if (OS.isWIN()) {
            /*Cmd cmd = new Cmd()
                    .add("cmd")
                    .add("/c")
                    .add(OS.WIN_WORK_DISK)
                    .add("&")
                    .add("cd")
                    .add(OS.convertLinuxPathToWin(Constants.NGINX_PATH))
                    .add("&")
                    .add("start")
                    .add(NGINX_BIN_FILENAME)
                    .add("&")
                    .add("exit");*/
            //.add("\"E: & cd E:\\home\\stgoder\\psmgr\\nginx & start nginx.win\"");
            String cmd = "cmd /c \"" +
                    OS.WIN_WORK_DISK + " & cd " +
                    OS.convertLinuxPathToWin(Constants.NGINX_PATH) +
                    " & start " + NGINX_BIN_FILENAME + "\"";
            try {
                Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                throw new ExecException(-1, "start process err: " + e.getMessage());
            }
        } else {
            Cmd cmd;
            cmd = new Cmd()
                    .add(Constants.NGINX_PATH + File.separator + NGINX_BIN_FILENAME)
                    .add("-c")
                    .add(Constants.NGINX_PATH + File.separator + "conf" + File.separator + "nginx.conf")
                    .add("-p")
                    .add(Constants.NGINX_PATH);
            final Out out = new Ps(cmd).exec();
            if (out.getExitValue() != 0) {
                System.out.println(out.toString());
                throw new ExecException(-1, out.toString());
            }
        }
    }

    public synchronized static void reload() throws ExecException {
        Cmd cmd;
        if (OS.isWIN()) {
            cmd = new Cmd()
                    .add("cmd")
                    .add("/c")
                    .add(OS.WIN_WORK_DISK)
                    .add("&")
                    .add("cd")
                    .add(OS.convertLinuxPathToWin(Constants.NGINX_PATH))
                    .add("&")
                    .add(NGINX_BIN_FILENAME)
                    .add("-s")
                    .add("reload");
        } else {
            cmd = new Cmd()
                    .add(Constants.NGINX_PATH + File.separator + NGINX_BIN_FILENAME)
                    .add("-c")
                    .add(Constants.NGINX_PATH + File.separator + "conf" + File.separator + "nginx.conf")
                    .add("-p")
                    .add(Constants.NGINX_PATH)
                    .add("-s")
                    .add("reload");
        }
        final Out out = new Ps(cmd).exec();
        if (out.getExitValue() != 0) {
            System.out.println(out.toString());
            throw new ExecException(-1, out.toString());
        }
    }

    public synchronized static void stop() throws ExecException {
        Cmd cmd;
        if (OS.isWIN()) {
            cmd = new Cmd()
                    .add("cmd")
                    .add("/c")
                    .add(OS.WIN_WORK_DISK)
                    .add("&")
                    .add("cd")
                    .add(OS.convertLinuxPathToWin(Constants.NGINX_PATH))
                    .add("&")
                    .add(NGINX_BIN_FILENAME)
                    .add("-s")
                    .add("stop");
        } else {
            cmd = new Cmd()
                    .add(Constants.NGINX_PATH + File.separator + NGINX_BIN_FILENAME)
                    .add("-c")
                    .add(Constants.NGINX_PATH + File.separator + "conf" + File.separator + "nginx.conf")
                    .add("-p")
                    .add(Constants.NGINX_PATH)
                    .add("-s")
                    .add("stop");
        }
        final Out out = new Ps(cmd).exec();
        if (out.getExitValue() != 0) {
            System.out.println(out.toString());
            throw new ExecException(-1, out.toString());
        }
    }

    public static boolean alive() throws ExecException {
        final List<Integer> pids = OS.pids(NGINX_BIN_FILENAME);
        if (pids.size() > 0)
            return true;
        return false;
    }

    public synchronized static void redeploy() throws ExecException, IOException {
        if (alive()) {
            stop();
            File nginxDir = new File(Constants.NGINX_PATH);
            FileUtils.deleteDirectory(nginxDir);
            init();
            start();
        }
    }

    public static void main(String[] args) throws Exception {
        start();

    }
}
