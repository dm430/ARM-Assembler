package lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import lexer.Token.TokenType;

/**
 * Created by devin on 11/25/15.
 */
public class Lexer {
    private static final char EOF = (char) -1;
    private static final char EOL = '\n';

    private InputReader input;
    private List<Token> tokens;
    private StringBuilder builder;

    public Lexer(File file) throws FileNotFoundException {
        input = new InputReader(file);
        tokens = new ArrayList<>();
        builder = new StringBuilder();
    }

    public TokenStream tokenize() throws IOException {
        char currentChar;

        while ((currentChar = input.peek()) != EOF) {
            switch (currentChar) {
                case ',':
                    String charValue = String.valueOf(input.read());
                    tokens.add(new Token(charValue, TokenType.COMMA));
                    break;
                case '#':
                    // TODO: Eat Comment
                    break;
                default:
                    if (Character.isLetter(currentChar)) {
                        tokens.add(readWord());
                    } else if (Character.isWhitespace(currentChar)) {
                        readWhiteSpace();
                    }
            }
        }

        return new TokenStream(tokens);
    }

    private void readWhiteSpace() throws IOException {
        while (Character.isWhitespace(input.peek())) {
            input.read();
        }
    }

    private Token readWord() throws IOException {
        builder.setLength(0);

        while (Character.isLetter(input.peek())) {
            builder.append(input.read());
        }
        String lexeme = builder.toString();

        return new Token(lexeme, TokenType.WORD);
    }
}
