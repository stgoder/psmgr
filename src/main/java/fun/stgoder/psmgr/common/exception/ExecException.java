package fun.stgoder.psmgr.common.exception;

public class ExecException extends BaseException {
    public ExecException() {
    }

    public ExecException(int code, String message) {
        super.code(code);
        super.message(message);
    }
}
