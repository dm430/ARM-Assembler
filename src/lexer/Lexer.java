package lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import lexer.Token.TokenType;
import lexer.exceptions.UnknownSymbolException;

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

    public TokenStream tokenize() throws IOException, UnknownSymbolException {
        char currentChar;

        while ((currentChar = input.peek()) != EOF) {
            switch (currentChar) {
                case ',':
                    String charValue = String.valueOf(input.read());
                    tokens.add(new Token(charValue, TokenType.COMMA));
                    break;
                case ':':
                    charValue = String.valueOf(input.read());
                    tokens.add(new Token(charValue, TokenType.COLON));
                    break;
                case '#':
                    readInlineComment();
                    break;
                case '-':
                    charValue = String.valueOf(input.read());
                    tokens.add(new Token(charValue, TokenType.FLAG));
                    break;
                case '[':
                    charValue = String.valueOf(input.read());
                    tokens.add(new Token(charValue, TokenType.OPEN_BRACKET));
                    break;
                case ']':
                    charValue = String.valueOf(input.read());
                    tokens.add(new Token(charValue, TokenType.CLOSE_BRACKET));
                    break;
                default:
                    if (Character.isLetter(currentChar)) {
                        tokens.add(readWord());
                    } else if (Character.isDigit(currentChar)) {
                        tokens.add(readNumeric());
                    } else if (Character.isWhitespace(currentChar)) {
                        readWhiteSpace();
                    } else {
                        throw new UnknownSymbolException(currentChar);
                    }
            }
        }

        return new TokenStream(tokens);
    }

    private Token readNumeric() throws IOException {
        Token token;
        char firstSymbol = input.read();

        builder.setLength(0);
        builder.append(firstSymbol);

        if (firstSymbol == '0' && input.peek() == 'x') {
            token = readHexNumber();
        } else {
            while (Character.isDigit(input.peek())) {
                builder.append(input.read());
            }

            token = new Token(builder.toString(), TokenType.NUMBER);
        }

        return token;
    }

    private Token readHexNumber() throws IOException {
        builder.append(input.read());

        char value = input.peek();
        while (Character.isDigit(value) || isHexDigit(value)) {
            builder.append(input.read());
            value = input.peek();
        }
        String lexeme = builder.toString();

        return new Token(lexeme, TokenType.HEX_NUMBER);
    }

    private boolean isHexDigit(char value) {
        char charAsUpper = Character.toUpperCase(value);
        return charAsUpper >= 'A' && charAsUpper <= 'F';
    }

    private void readInlineComment() throws IOException {
        while (input.peek() != EOL) {
            input.read();
        }
    }

    private void readWhiteSpace() throws IOException {
        while (Character.isWhitespace(input.peek())) {
            input.read();
        }
    }

    private Token readWord() throws IOException {
        builder.setLength(0);

        while (Character.isLetter(input.peek()) || Character.isDigit(input.peek())) {
            builder.append(input.read());
        }
        String lexeme = builder.toString();

        return new Token(lexeme, TokenType.WORD);
    }
}
