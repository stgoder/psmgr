package fun.stgoder.psmgr.common.exception;

public class BaseException extends Exception {
    private static final long serialVersionUID = 8557285382037848574L;
    private int code;
    private String message;

    public BaseException() {
    }

    public BaseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public void code(int code) {
        this.code = code;
    }

    public void message(String message) {
        this.message = message;
    }
}
