package lexer;

import java.util.List;

/**
 * Created by devin on 11/25/15.
 */
public class TokenStream {
    private List<Token> tokens;
    private int index;

    public TokenStream(List<Token> tokens) {
        this.tokens = tokens;
    }

    public boolean hasNext() {
        return index < tokens.size();
    }

    public Token next() {
        if (!hasNext()) {
            throw new EndOfTokenStreamException();
        }

        return tokens.get(index++);
    }

    public Token Previous() {
        if (index <= 0) {
            throw new EndOfTokenStreamException("Attempted to get token at index " + (index - 1));
        }

        return tokens.get(--index);
    }
}
