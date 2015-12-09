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

    public byte[] parse(TokenStream tokenStream) throws SyntaxErrorException, EncodingException {
        while (tokenStream.hasNext()) {
            parseInstruction(tokenStream);
        }

        return codeGenerator.generateProgram();
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
        Token instruction = tokenStream.next();

        if (startsWith(instruction, "AND", "ORR", "ADD", "SUB")) {
            Token flags = tryParseFlags(tokenStream);

            if (startsWith(instruction, "AND")) {
                codeGenerator.generateAnd(instruction, flags);
            } else if (startsWith(instruction, "ORR")) {
                codeGenerator.generateOrr(instruction, flags);
            } else if (startsWith(instruction, "ADD")) {
                codeGenerator.generateAdd(instruction, flags);
            } else if (startsWith(instruction, "SUB")) {
                codeGenerator.generateSub(instruction, flags);
            }

            parseLogicParameters(tokenStream, instruction);
        } else if (startsWith(instruction, "MOVW", "MOVT")) {
            if (startsWith(instruction, "MOVW")) {
                codeGenerator.generateMovw(instruction);
            } else if (startsWith(instruction, "MOVT")) {
                codeGenerator.generateMovt(instruction);
            }

            parseMovParameters(tokenStream, instruction);
        } else if (startsWith(instruction, "CMP")) {
            codeGenerator.generateCmp(instruction);
            parseCmpParameters(tokenStream, instruction);
        } else {
            throw new SyntaxErrorException();
        }
    }

    private void parseCmpParameters(TokenStream tokenStream, Token instruction) throws EncodingException, SyntaxErrorException {
        //Token instruction = tokenStream.next();

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

    private void parseMovParameters(TokenStream tokenStream, Token token) throws EncodingException, SyntaxErrorException {
        //Token token = tokenStream.next();

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

    private void parseLogicParameters(TokenStream tokenStream, Token token) throws EncodingException, SyntaxErrorException {
        //Token token = tokenStream.next();

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
        Token instruction = tokenStream.next();
        Token flags = tryParseFlags(tokenStream);

        ConditionCode conditionCode = getConditionCode(instruction);

        if (startsWith(instruction, "LDR")) {
            codeGenerator.generateLdr(conditionCode, flags);
        } else if (startsWith(instruction, "STR")) {
            codeGenerator.generateStr(conditionCode, flags);
        } else {
            throw new SyntaxErrorException();
        }

        // Generates the remaining part of the instruction
        parseLdrStrParameters(tokenStream, instruction);
    }

    private Token tryParseFlags(TokenStream tokenStream) {
        Token flags = null;
        Token flag = tokenStream.peek();

        if (isTokenType(flag, TokenType.FLAG)) {
            tokenStream.next();
            flags = tokenStream.next();
        }

        return flags;
    }

    private void parseLdrStrParameters(TokenStream tokenStream, Token token) throws EncodingException, SyntaxErrorException {
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
            if (isTokenType(branchTo, TokenType.NUMBER) || isTokenType(branchTo, TokenType.HEX_NUMBER)) {
                if (startsWith(branch, "Bl")) {
                    // TODO: 12/8/15  
                } else if (startsWith(branch, "B")) {
                    codeGenerator.generateBranchImmediate(branch, branchTo);
                }
            } else if (isTokenType(branchTo, TokenType.WORD)) {
                String word = branchTo.getLexeme();
                int address = symbolTable.get(word);

                if (startsWith(branch, "Bl")) {
                    // TODO: 12/8/15
                } else if (startsWith(branch, "B")) {
                    codeGenerator.generateBranch(branch, address);
                }
            } else {
                throw new SyntaxErrorException();
            }
        } else  {
            throw new SyntaxErrorException();
        }
    }

    private void parseBranchParameters(TokenStream tokenStream) {

    }

    private void tryParseLabel(TokenStream tokenStream) {
        Token label = tokenStream.next();
        Token colon = tokenStream.next();

        if (label.getTokenType() == Token.TokenType.WORD
                && colon.getTokenType() == Token.TokenType.COLON) {
            symbolTable.put(label.getLexeme(), codeGenerator.getCurrentAddress());
        } else {
            tokenStream.reverseStream(2);
        }
    }
}
