package parser;

import lexer.Token;
import lexer.TokenStream;
import lexer.Token.TokenType;
import parser.exceptions.SyntaxErrorException;
import parser.CodeGenerator.ConditionCode;

import java.util.HashMap;
import java.util.Map;

import static parser.ParserUtils.*;

/**
 * Created by devin on 11/28/15.
 */
public class Parser {
    private CodeGenerator codeGenerator;
    private Map<String, Integer> symbolTable;

    public Parser() {
        this.symbolTable = new HashMap<>();
        this.codeGenerator = new CodeGenerator();
    }

    public void parse(TokenStream tokenStream) throws SyntaxErrorException {
        while (tokenStream.hasNext()) {
            parseInstruction(tokenStream);
        }
    }

    private void parseInstruction(TokenStream tokenStream) throws SyntaxErrorException {
        tryParseLabel(tokenStream);
        parseOperation(tokenStream);
    }

    private void parseOperation(TokenStream tokenStream) throws SyntaxErrorException {
        Token token = tokenStream.peek();

        if (startsWith(token, "B")) {
            parseBranch(tokenStream);
        } else if (startsWith(token, "AND", "ORR", "ADD", "SUB", "CMP", "MOV")) {
            parseDataProcess(tokenStream);
        } else if (startsWith(token, "LDR", "STR")) {
            parseLdrStr(tokenStream);
        } else {
            throw new SyntaxErrorException();
        }
    }

    private void parseDataProcess(TokenStream tokenStream) {

    }

    private void parseLdrStr(TokenStream tokenStream) {

    }

    private void parseBranch(TokenStream tokenStream) {
        Token branch = tokenStream.next();
        Token branchTo = tokenStream.next();

        if (isTokenType(branch, TokenType.WORD)) {
            ConditionCode conditionCode = getConditionCode(branch);

            if (isTokenType(branchTo, TokenType.NUMBER)) {

            } else if (isTokenType(branch, TokenType.WORD)) {
                String word = branchTo.getLexeme();
                int address =  symbolTable.get(word);
                codeGenerator.generateBranch(conditionCode, address);
            }
        }
    }

    private void tryParseLabel(TokenStream tokenStream) {
        Token label = tokenStream.next();
        Token colon = tokenStream.next();

        if (label.getTokenType() == Token.TokenType.WORD
                && colon.getTokenType() == Token.TokenType.COLON) {
            // TODO: think about this
        } else {
            tokenStream.reverseStream(2);
        }
    }
}


