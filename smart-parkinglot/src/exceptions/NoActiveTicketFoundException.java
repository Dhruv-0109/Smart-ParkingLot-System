package exceptions;

public class NoActiveTicketFoundException extends Exception {
    public NoActiveTicketFoundException(String message) {
        super(message);
    }
}