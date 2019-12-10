package fun.stgoder.psmgr.ps;

import fun.stgoder.psmgr.common.util.JsonUtil;

public class Out {
    private int exitValue;
    private String output;
    private String error;

    public Out() {
    }

    public Out(int exitValue, String output, String error) {
        this.exitValue = exitValue;
        this.output = output;
        this.error = error;
    }

    public int getExitValue() {
        return exitValue;
    }

    public void setExitValue(int exitValue) {
        this.exitValue = exitValue;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
