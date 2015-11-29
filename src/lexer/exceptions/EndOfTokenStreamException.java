package lexer.exceptions;

/**
 * Created by devin on 11/25/15.
 */
public class EndOfTokenStreamException extends RuntimeException {
    private static final String PREFIX = "The end of the token stream was hit: ";

    public EndOfTokenStreamException() {
        super(PREFIX);
    }

    public EndOfTokenStreamException(String  message) {
        super(PREFIX + message);
    }
}

