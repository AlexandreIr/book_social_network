package tech.afsilva.book.exception;

public class OperationNotAllowedException extends RuntimeException {
    public OperationNotAllowedException(String msg) {
        super(msg);
    }
}
