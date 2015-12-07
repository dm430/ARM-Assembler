package parser;

import lexer.Token;
import lexer.TokenStream;
import lexer.Token.TokenType;
import parser.exceptions.EncodingException;
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

    public void parse(TokenStream tokenStream) throws SyntaxErrorException, EncodingException {
        while (tokenStream.hasNext()) {
            parseInstruction(tokenStream);
        }

        codeGenerator.getProgram().forEach(x -> System.out.println(x));
    }

    private void parseInstruction(TokenStream tokenStream) throws SyntaxErrorException, EncodingException {
        tryParseLabel(tokenStream);
        parseOperation(tokenStream);
    }

    private void parseOperation(TokenStream tokenStream) throws SyntaxErrorException, EncodingException {
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

    private void parseDataProcess(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
        Token instruction = tokenStream.peek();

        if (startsWith(instruction, "AND", "ORR", "ADD", "SUB")) {
            if (startsWith(instruction, "AND")) {
                codeGenerator.generateAnd(instruction);
            } else if (startsWith(instruction, "ORR")) {
                codeGenerator.generateOrr(instruction);
            } else if (startsWith(instruction, "ADD")) {
                codeGenerator.generateAdd(instruction);
            } else if (startsWith(instruction, "SUB")) {
                codeGenerator.generateSub(instruction);
            }

            parseLogicParameters(tokenStream);
        } else if (startsWith(instruction, "MOVW", "MOVT")) {
            if (startsWith(instruction, "MOVW")) {
                codeGenerator.generateMovw(instruction);
            } else if (startsWith(instruction, "MOVT")) {
                codeGenerator.generateMovt(instruction);
            }

            parseMovParameters(tokenStream);
        } else if (startsWith(instruction, "CMP")) {
            codeGenerator.generateCmp(instruction);
            parseCmpParameters(tokenStream);
        } else {
            throw new SyntaxErrorException();
        }
    }

    private void parseCmpParameters(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
        Token instruction = tokenStream.next();

        if (isTokenType(instruction, TokenType.WORD) && endsWith(instruction, "I")) {
            Token register = tokenStream.next();
            Token comma = tokenStream.next();
            Token value = tokenStream.next();

            if (isTokenType(register, TokenType.WORD)
                    && startsWith(register, "R")
                    && isTokenType(comma, TokenType.COMMA)
                    && (isTokenType(value, TokenType.NUMBER)
                    || isTokenType(value, TokenType.HEX_NUMBER))) {
                codeGenerator.generateCmpParametersImmediate(register, value);
            } else {
                throw new SyntaxErrorException();
            }
        } else {
            throw new SyntaxErrorException();
        }
    }

    private void parseMovParameters(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
        Token token = tokenStream.next();

        if (isTokenType(token, TokenType.WORD) && endsWith(token, "I")) {
            Token destinationRegister = tokenStream.next();
            Token comma = tokenStream.next();
            Token value = tokenStream.next();

            if (isTokenType(destinationRegister, TokenType.WORD)
                    && startsWith(destinationRegister, "R")
                    && isTokenType(comma, TokenType.COMMA)
                    && (isTokenType(value, TokenType.NUMBER)
                    || isTokenType(value, TokenType.HEX_NUMBER))) {
                codeGenerator.generateMovImmediateParameters(destinationRegister, value);
            } else {
                throw new SyntaxErrorException();
            }
        } else {
            throw new SyntaxErrorException();
        }
    }

    private void parseLogicParameters(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
        Token token = tokenStream.next();

        if (isTokenType(token, TokenType.WORD) && endsWith(token, "I")) {
            Token destinationRegister = tokenStream.next();
            Token comma = tokenStream.next();
            Token baseRegister = tokenStream.next();
            Token comma2 = tokenStream.next();
            Token value = tokenStream.next();

            if (isTokenType(destinationRegister, TokenType.WORD)
                    && startsWith(destinationRegister, "R")
                    && isTokenType(comma, TokenType.COMMA)
                    && isTokenType(baseRegister, TokenType.WORD)
                    && startsWith(baseRegister, "R")
                    && isTokenType(comma2, TokenType.COMMA)
                    && (isTokenType(value, TokenType.NUMBER)
                    || isTokenType(value, TokenType.HEX_NUMBER))) {
                codeGenerator.generateLogicImmediate12BitsParameters(destinationRegister, baseRegister, value);
            } else {
                throw new SyntaxErrorException();
            }
        } else {
            throw new SyntaxErrorException();
        }
    }

    private void parseLdrStr(TokenStream tokenStream) throws SyntaxErrorException, EncodingException {
        Token instruction = tokenStream.peek();

        if (startsWith(instruction, "LDR")) {
            ConditionCode conditionCode = getConditionCode(instruction);
            codeGenerator.generateLdr(conditionCode);
        } else if (startsWith(instruction, "STR")) {
            ConditionCode conditionCode = getConditionCode(instruction);
            codeGenerator.generateStr(conditionCode);
        } else {
            throw new SyntaxErrorException();
        }

        // Generates the remaining part of the instruction
        parseLdrStrParameters(tokenStream);
    }

    private void parseLdrStrParameters(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
        Token token = tokenStream.next();

        if (isTokenType(token, TokenType.WORD) && endsWith(token, "I")) {
            Token destinationRegister = tokenStream.next();
            Token comma = tokenStream.next();
            Token baseRegister = tokenStream.next();
            Token comma2 = tokenStream.next();
            Token offset = tokenStream.next();

            if (isTokenType(destinationRegister, TokenType.WORD)
                    && startsWith(destinationRegister, "R")
                    && isTokenType(comma, TokenType.COMMA)
                    && isTokenType(baseRegister, TokenType.WORD)
                    && startsWith(baseRegister, "R")
                    && isTokenType(comma2, TokenType.COMMA)
                    && (isTokenType(offset, TokenType.NUMBER)
                    || isTokenType(offset, TokenType.HEX_NUMBER))) {
                codeGenerator.generateRegistersImmediate12BitsParameters(destinationRegister, baseRegister, offset);
            } else {
                throw new SyntaxErrorException();
            }
        } else {
            throw new SyntaxErrorException();
        }
    }

    private void parseBranch(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
        Token branch = tokenStream.next();
        Token branchTo = tokenStream.next();

        if (isTokenType(branch, TokenType.WORD)) {
            ConditionCode conditionCode = getConditionCode(branch);

            // TODO: This has a null pinter that doesn't make sense...
            if (isTokenType(branchTo, TokenType.NUMBER) || isTokenType(branchTo, TokenType.HEX_NUMBER)) {
                int numberToJump = Integer.getInteger(branchTo.getLexeme());
                codeGenerator.generateBranchImmediate(conditionCode, numberToJump);
            } else if (isTokenType(branchTo, TokenType.WORD)) {
                String word = branchTo.getLexeme();
                int address = symbolTable.get(word);
                codeGenerator.generateBranch(conditionCode, address);
            } else {
                throw new SyntaxErrorException();
            }
        } else  {
            throw new SyntaxErrorException();
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
