package parser;

import generator.CodeGenerator;
import generator.ConcreteCodeGenerator;
import generator.DryrunCodeGenerator;
import lexer.Token;
import lexer.TokenStream;
import lexer.Token.TokenType;
import parser.exceptions.EncodingException;
import parser.exceptions.SyntaxErrorException;
import generator.ConcreteCodeGenerator.ConditionCode;

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
    }

    public void buildSymbolTable(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
        this.codeGenerator = new DryrunCodeGenerator();

        while (tokenStream.hasNext()) {
            parseInstruction(tokenStream);
        }

        tokenStream.reset();
    }

    public byte[] parse(TokenStream tokenStream) throws SyntaxErrorException, EncodingException {
        this.codeGenerator = new ConcreteCodeGenerator();

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
        } else if (startsWith(token, "PUSH", "POP")) {
            parsePushPop(tokenStream);
        } else {
            throw new SyntaxErrorException();
        }
    }

    private void parsePushPop(TokenStream tokenStream) throws SyntaxErrorException {
        Token instruction = tokenStream.next();

        // Accept no variations
        if (tokenEquals(instruction, "PUSH")) {

        } else if (tokenEquals(instruction, "POP")) {

        } else {
            throw new SyntaxErrorException();
        }


        parsePushPopListParameters(tokenStream);
    }

    private void parsePushPopListParameters(TokenStream tokenStream) throws SyntaxErrorException {
        Token token = tokenStream.next();
        Token register = tokenStream.next();

        if (isTokenType(token, TokenType.OPEN_BRACKET)
                && isTokenType(register, TokenType.WORD) && startsWith(register, "R")) {
            Token commaOrBracket = tokenStream.next();

            while (isTokenType(commaOrBracket, TokenType.COMMA)) {
                register = tokenStream.next();

                if (isTokenType(register, TokenType.WORD) && startsWith(register, "R")) {

                } else {
                    throw new SyntaxErrorException();
                }

                commaOrBracket = tokenStream.next();
            }

            if (isTokenType(commaOrBracket, TokenType.CLOSE_BRACKET)) {
                // TODO: Generate
            } else {
                throw new SyntaxErrorException();
            }
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

            if (endsWith(instruction, "i")) {
                parseLogicImmediateParameters(tokenStream);
            } else {
                throw new SyntaxErrorException();
            }
        } else if (startsWith(instruction, "MOVW", "MOVT")) {
            if (startsWith(instruction, "MOVW")) {
                codeGenerator.generateMovw(instruction);
            } else if (startsWith(instruction, "MOVT")) {
                codeGenerator.generateMovt(instruction);
            }

            if (endsWith(instruction, "i")) {
                parseMovImmediateParameters(tokenStream);
            } else {
                throw new SyntaxErrorException();
            }
        } else if (startsWith(instruction, "CMP")) {
            codeGenerator.generateCmp(instruction);

            if (endsWith(instruction, "i")) {
                parseCmpImmediateParameters(tokenStream);
            } else {
                throw new SyntaxErrorException();
            }
        } else {
            throw new SyntaxErrorException();
        }
    }

    private void parseCmpImmediateParameters(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
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
    }

    private void parseMovImmediateParameters(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
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
    }

    private void parseLogicRegisterParameters(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
        // TODO: 12/9/15 Also refactor to extract common code from parse params
    }

    private void parseLogicImmediateParameters(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
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
        if (endsWith(instruction, "i")) {
            parseLdrStrImmediateParameters(tokenStream);
        } else {
            parseLdrStrRegisterParameters(tokenStream);
        }
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

    private void parseLdrStrRegisterParameters(TokenStream tokenStream) throws SyntaxErrorException, EncodingException {
        Token destinationRegister = tokenStream.next();
        Token comma = tokenStream.next();
        Token baseRegister = tokenStream.next();
        Token comma2 = tokenStream.next();
        Token offsetRegister = tokenStream.next();

        if (isTokenType(destinationRegister, TokenType.WORD)
                && startsWith(destinationRegister, "R")
                && isTokenType(comma, TokenType.COMMA)
                && isTokenType(baseRegister, TokenType.WORD)
                && startsWith(baseRegister, "R")
                && isTokenType(comma2, TokenType.COMMA)
                && isTokenType(offsetRegister, TokenType.WORD)
                && startsWith(offsetRegister, "R")) {
            codeGenerator.generateRegistersParameters(destinationRegister, baseRegister, offsetRegister);
        } else {
            throw new SyntaxErrorException();
        }
    }

    private void parseLdrStrImmediateParameters(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
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
    }

    private void parseBranch(TokenStream tokenStream) throws EncodingException, SyntaxErrorException {
        Token branch = tokenStream.next();
        Token branchTo = tokenStream.next();

        if (isTokenType(branch, TokenType.WORD)) {
            if (isTokenType(branchTo, TokenType.NUMBER) || isTokenType(branchTo, TokenType.HEX_NUMBER)) {
                if (startsWith(branch, "BL")) {
                    codeGenerator.generateBranchLinkImmediate(branch, branchTo);
                } else if (startsWith(branch, "B")) {
                    codeGenerator.generateBranchImmediate(branch, branchTo);
                }
            } else if (isTokenType(branchTo, TokenType.WORD)) {
                int address = 0;
                String word = branchTo.getLexeme();

                if (symbolTable.containsKey(word)) {
                    address = symbolTable.get(word);
                }

                if (startsWith(branch, "BL")) {
                    codeGenerator.generateBranchLink(branch, address);
                } else if (startsWith(branch, "B")) {
                    codeGenerator.generateBranch(branch, address);
                }
            } else {
                throw new SyntaxErrorException();
            }
        } else {
            throw new SyntaxErrorException();
        }
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
