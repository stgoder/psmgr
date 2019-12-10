package fun.stgoder.psmgr.common.exception;

public class BLException extends BaseException {
    public BLException() {
    }

    public BLException(int code, String message) {
        super.code(code);
        super.message(message);
    }
}
