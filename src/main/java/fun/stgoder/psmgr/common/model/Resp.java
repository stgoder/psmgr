package fun.stgoder.psmgr.common.model;

import fun.stgoder.psmgr.common.Code;

import java.io.Serializable;

public class Resp implements Serializable {
    private static final long serialVersionUID = 5854452812497639129L;
    private int code;
    private String message;
    private Object data;

    public Resp() {
    }

    public Resp(String message) {
        this.message = message;
    }

    public Resp(int code) {
        this.code = code;
        if (code == Code.REQUEST_OK) {
            this.message = "succ";
        } else {
            this.message = "err";
        }
    }

    public Resp(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Resp(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Resp(int code, Object data) {
        this.code = code;
        this.data = data;
        if (code == Code.REQUEST_OK) {
            this.message = "succ";
        } else {
            this.message = "err";
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
