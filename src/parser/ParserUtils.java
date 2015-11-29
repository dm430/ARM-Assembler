package parser;

import lexer.Token;
import lexer.Token.TokenType;

import parser.CodeGenerator.ConditionCode;

/**
 * Created by devin on 11/28/15.
 */
public class ParserUtils {
    public static boolean startsWith(Token token, String... prefixes) {
        boolean startsWith = false;
        String lexeme = token.getLexeme().toUpperCase();

        for (int i = 0; i < prefixes.length && !startsWith; i++) {
            startsWith = lexeme.startsWith(prefixes[i].toUpperCase());
        }

        return startsWith;
    }

    public static boolean endsWith(Token token, String... postfixes) {
        boolean endsWith = false;
        String lexeme = token.getLexeme().toUpperCase();

        for (int i = 0; i < postfixes.length && !endsWith; i++) {
            endsWith = lexeme.endsWith(postfixes[i].toUpperCase());
        }

        return endsWith;
    }

    public static boolean isTokenType(Token token, TokenType type) {
        return token.getTokenType() == type;
    }

    public static ConditionCode getConditionCode(Token token) {
        ConditionCode conditionCode = ConditionCode.ALWAYS;

        if (endsWith(token, "EQ")) {
            conditionCode = conditionCode.EQUAL;
        } else if (endsWith(token, "NE")) {
            conditionCode = ConditionCode.NOT_EQUAL;
        } else if (endsWith(token, "LT")) {
            conditionCode = ConditionCode.LESS_THAN;
        } else if (endsWith(token, "LE")) {
            conditionCode = ConditionCode.LESS_THAN_EQUAL;
        } else if (endsWith(token, "GT")) {
            conditionCode = ConditionCode.GRATER_THAN;
        } else if (endsWith(token, "GE")) {
            conditionCode = ConditionCode.GREATER_THAN_EQUAL;
        }

        return conditionCode;
    }
}
