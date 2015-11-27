package lexer;

/**
 * Created by devin on 11/25/15.
 */
public class UnknownSymbolException extends Throwable {
    private static final String MESSAGE = "Encountered unrecognized symbol: ";
    public UnknownSymbolException(char symbol) {
        super(MESSAGE + symbol);
    }
}
