package fun.stgoder.psmgr.common.exception;

public class ExecException extends BaseException {
    private static final long serialVersionUID = 5461976300645803393L;

    public ExecException() {
    }

    public ExecException(int code, String message) {
        super.code(code);
        super.message(message);
    }
}
