package fun.stgoder.psmgr.common.exception;

public class BLException extends BaseException {
    private static final long serialVersionUID = 7492816619343859236L;

    public BLException() {
    }

    public BLException(int code, String message) {
        super.code(code);
        super.message(message);
    }
}
