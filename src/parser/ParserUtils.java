package parser;

import lexer.Token;
import lexer.Token.TokenType;

import generator.ConcreteCodeGenerator.ConditionCode;
import parser.exceptions.EncodingException;

/**
 * Created by devin on 11/28/15.
 */
public class ParserUtils {

    public static final String REGISTER_PREFIX = "R";
    public static final int NIBBLE_SIZE = 4;

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

        if (endsWith(token, "EQ", "EQi")) {
            conditionCode = conditionCode.EQUAL;
        } else if (endsWith(token, "NE", "NEi")) {
            conditionCode = ConditionCode.NOT_EQUAL;
        } else if (endsWith(token, "LT", "LTi")) {
            conditionCode = ConditionCode.LESS_THAN;
        } else if (endsWith(token, "LE", "LEi")) {
            conditionCode = ConditionCode.LESS_THAN_EQUAL;
        } else if (endsWith(token, "GT", "GTi")) {
            conditionCode = ConditionCode.GRATER_THAN;
        } else if (endsWith(token, "GE", "GEi")) {
            conditionCode = ConditionCode.GREATER_THAN_EQUAL;
        }

        return conditionCode;
    }

    public static String toHexString(int number, int bitWidth) {
        StringBuilder builder = new StringBuilder();

        String result = Integer.toHexString(number);

        // Prevent widening on negative numbers
        if (result.length() >= 8) {
            result = result.substring(2);
        }

        int numberOfHexDigits = bitWidth / NIBBLE_SIZE;
        int digitsToAdd = numberOfHexDigits - result.length();

        for (int i = 0; i < digitsToAdd; i++) {
            builder.append("0");
        }

        builder.append(result);

        return builder.toString();
    }

    public static String reverseEndianness(String hex) {
        StringBuilder hexString = new StringBuilder();

        if (hex.length() % 2 != 0) {
            return hex;
        }

        for (int i = hex.length(); i > 0; i -= 2) {
            hexString.append(hex.substring(i - 2, i));
        }

        return hexString.toString();
    }

    public static int getRegisterNumber(Token register) {
        String lexeme = register.getLexeme().toUpperCase();
        lexeme = lexeme.substring(REGISTER_PREFIX.length());

        return Integer.parseInt(lexeme);
    }

    public static String setFlag(Token flags, String bitFlag) {
        boolean isSet = flags.getLexeme().toUpperCase()
                .contains(bitFlag.toUpperCase());

        return isSet ? "1" : "0";
    }

    public static boolean tokenEquals(Token instruction, String value) {
        return value.equalsIgnoreCase(instruction.getLexeme());
    }

    public static String encodeModifiedImmediate(int immediateValue) throws EncodingException {
        String encodedValue = new String();
        boolean notCalculated = true;

        for (int rotate = 0; rotate < 32 && notCalculated; rotate += 2) {
            if ((immediateValue & ~0xFF) == 0) {
                encodedValue = toHexString(rotate / 2, 4) + toHexString(immediateValue, 8);
                notCalculated = false;
            }

            immediateValue = (immediateValue << 2) | (immediateValue >>> 30);
        }

        if (encodedValue.isEmpty()) {
            throw new EncodingException("The constant is too wide");
        }

        return encodedValue;
    }
}
