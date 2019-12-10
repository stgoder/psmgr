package fun.stgoder.psmgr.ps;

import fun.stgoder.psmgr.common.exception.ExecException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class Ps {
    private Cmd cmd;
    private java.lang.Process p0;

    public Ps(Cmd cmd) {
        this.cmd = cmd;
    }

    public Out exec() throws ExecException {
        cleanup();
        try {
            ProcessBuilder pb0 = new ProcessBuilder(cmd.cmds());
            p0 = pb0.start();
            StringBuilder output = new StringBuilder();
            try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(p0.getInputStream()))) {
                String line = null;
                while ((line = outputReader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            StringBuilder error = new StringBuilder();
            try (BufferedReader errReader = new BufferedReader(new InputStreamReader(p0.getErrorStream()))) {
                String line = null;
                while ((line = errReader.readLine()) != null) {
                    error.append(line).append("\n");
                }
            }
            p0.waitFor(10, TimeUnit.SECONDS);
            int exitValue = p0.exitValue();
            return new Out(exitValue, output.toString(), error.toString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ExecException(-1, e.getMessage());
        }
    }

    public void execRedirect(File outputFile) throws ExecException {
        cleanup();
        try {
            ProcessBuilder pb0 = new ProcessBuilder(cmd.cmds());
            pb0.redirectErrorStream(true);
            pb0.redirectOutput(outputFile);
            p0 = pb0.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ExecException(-1, e.getMessage());
        }
    }

    public void execIgnore() throws ExecException {
        cleanup();
        try {
            Cmd cmd0 = new Cmd()
                    .add("nohup")
                    .plus(cmd)
                    .add(">/dev/null")
                    .add("2>&1")
                    .add("&");
            ProcessBuilder pb0 = new ProcessBuilder(cmd0.cmds());
            p0 = pb0.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ExecException(-1, e.getMessage());
        }
    }

    public void cleanup() {
        destroy();
    }

    public boolean isAlive() {
        if (p0 != null) {
            return p0.isAlive();
        } else {
            return false;
        }
    }

    public Cmd cmd() {
        return cmd;
    }

    private void destroy() {
        if (p0 != null)
            p0.destroy();
    }

    public static void main(String[] args) throws ExecException {
        Out out = new Ps(
                new Cmd()
                        .add("/bin/bash")
                        .add("-c")
                        .add("ps -ef | grep java")).exec();
        System.out.println(out);
    }
}
