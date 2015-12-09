package lexer;

/**
 * Created by devin on 11/25/15.
 */
public class Token {
    public enum TokenType {
        COMMA, COLON, WORD,
        NUMBER, HEX_NUMBER,
        FLAG
    }

    private String lexeme;
    private TokenType tokenType;

    public Token(String lexeme, TokenType tokenType) {
        this.tokenType = tokenType;
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return lexeme;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return "Token{'" + lexeme + '\'' + ", " + tokenType + '}';
    }
}
