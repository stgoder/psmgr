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
    public static final String NGINX_BIN_FILENAME = "nginx.bin";

    public static void init() throws IOException, ExecException {
        File nginxDir = new File(Constants.NGINX_PATH);
        if (!nginxDir.exists())
            nginxDir.mkdirs();

        File nginx = new File(Constants.NGINX_PATH + File.separator + NGINX_BIN_FILENAME);
        if (!nginx.exists()) {
            try (final InputStream nginxInputStream = Nginx.class.getResourceAsStream("/" + NGINX_BIN_FILENAME)) {
                if (nginxInputStream == null)
                    throw new IOException("read nginx bin err");
                FileUtils.copyInputStreamToFile(nginxInputStream, nginx);
            }
        }

        File nginxConf = new File(Constants.NGINX_PATH + File.separator + "nginx.conf");
        if (!nginxConf.exists()) {
            try (final InputStream nginxConfInputStream = Nginx.class.getResourceAsStream("/nginx.conf")) {
                if (nginxConfInputStream == null)
                    throw new IOException("read nginx conf err");
                FileUtils.copyInputStreamToFile(nginxConfInputStream, nginxConf);
            }
        }

        final Out out = new Ps(
                new Cmd()
                        .add("/bin/bash")
                        .add("-c")
                        .add("chmod +x " + Constants.NGINX_PATH + File.separator + NGINX_BIN_FILENAME)).exec();
        if (out.getExitValue() != 0) {
            System.out.println(out.getError());
            throw new ExecException(-1, out.getError());
        }
        File nginxLogsDir = new File(Constants.NGINX_PATH + File.separator + "logs");
        if (!nginxLogsDir.exists())
            nginxLogsDir.mkdirs();
    }

    public static void start() throws ExecException {
        final Out out = new Ps(new Cmd()
                .add(Constants.NGINX_PATH + File.separator + NGINX_BIN_FILENAME)
                .add("-c")
                .add(Constants.NGINX_PATH + File.separator + "nginx.conf")
                .add("-p")
                .add(Constants.NGINX_PATH)).exec();
        if (out.getExitValue() != 0) {
            System.out.println(out.getError());
            throw new ExecException(-1, out.getError());
        }
    }

    public static void reload() throws ExecException {
        final Out out = new Ps(new Cmd()
                .add(Constants.NGINX_PATH + File.separator + NGINX_BIN_FILENAME)
                .add("-c")
                .add(Constants.NGINX_PATH + File.separator + "nginx.conf")
                .add("-p")
                .add(Constants.NGINX_PATH).add("-s").add("reload")).exec();
        if (out.getExitValue() != 0) {
            System.out.println(out.getError());
            throw new ExecException(-1, out.getError());
        }
    }

    public static void stop() throws ExecException {
        final Out out = new Ps(new Cmd()
                .add(Constants.NGINX_PATH + File.separator + NGINX_BIN_FILENAME)
                .add("-c")
                .add(Constants.NGINX_PATH + File.separator + "nginx.conf")
                .add("-p")
                .add(Constants.NGINX_PATH).add("-s").add("stop")).exec();
        if (out.getExitValue() != 0) {
            System.out.println(out.getError());
            throw new ExecException(-1, out.getError());
        }
    }

    public static boolean alive() throws ExecException {
        final List<Integer> pids = Ps.pids(NGINX_BIN_FILENAME);
        if (pids.size() > 0)
            return true;
        return false;
    }

    public static void main(String[] args) throws Exception {
        if (OS.isLINUX()) {
            //Nginx.init();
            //Nginx.start();
            //Nginx.stop();
            Nginx.reload();
        }
    }
}
