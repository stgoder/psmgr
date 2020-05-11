package fun.stgoder.psmgr.common.model;

import fun.stgoder.psmgr.common.Code;
import fun.stgoder.psmgr.common.util.JsonUtil;

import java.io.Serializable;

public class Resp<T> implements Serializable {
    private static final long serialVersionUID = 5854452812497639129L;
    private int code;
    private String msg;
    private T data;

    public static Resp ok() {
        return new Resp(Code.REQUEST_OK, "ok", null);
    }

    public static Resp ok(Object data) {
        return new Resp(Code.REQUEST_OK, "ok", data);
    }

    public static Resp err(String msg) {
        return new Resp(Code.REQUEST_ERR, msg, null);
    }

    public static Resp err(String msg, Object data) {
        return new Resp(Code.REQUEST_ERR, msg, data);
    }

    public Resp() {
    }

    public Resp(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
