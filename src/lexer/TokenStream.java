package lexer;

import lexer.exceptions.EndOfTokenStreamException;

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

    public Token peek() {
        return tokens.get(index);
    }

    public Token peekNext() {
        if (!hasNext()) {
            throw new EndOfTokenStreamException();
        }

        return tokens.get(index + 1);
    }

    public void reverseStream(int numberToReverseBy) {
        if (index - numberToReverseBy < 0) {
            index = 0;
        } else {
            index -= numberToReverseBy;
        }
    }

    public void reset() {
        index = 0;
    }
}
