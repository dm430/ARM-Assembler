package generator;

import lexer.Token;
import parser.exceptions.EncodingException;

import java.util.List;

import generator.ConcreteCodeGenerator.ConditionCode;

/**
 * Created by devin on 12/9/15.
 */
public interface CodeGenerator {
    void generateBranchLinkImmediate(Token instruction, Token branchTo) throws EncodingException;

    void generateBranchLink(Token instruction, int address);

    void generateBranch(Token instruction, int address);

    void generateBranchImmediate(Token instruction, Token branchTo) throws EncodingException;

    void generateRegistersParameters(Token destinationRegister, Token baseRegister, Token offsetRegister) throws EncodingException;

    void generateLdrStrImmediate12BitsParameters(Token destinationRegister, Token baseRegister, Token offset) throws EncodingException;

    void generateStr(ConditionCode conditionCode, Token flags);

    void generateLdr(ConditionCode conditionCode, Token flags);

    void generateAnd(Token instruction, Token flags);

    void generateOrr(Token instruction, Token flags);

    void generateAdd(Token instruction, Token flags);

    void generateSub(Token instruction, Token flags);

    void generateMovt(Token instruction);

    void generateMovw(Token instruction);

    void generateCmp(Token instruction);

    void generateMovImmediateParameters(Token destinationRegister, Token value) throws EncodingException;

    void generateCmpParametersImmediate(Token register, Token value) throws EncodingException;

    void generateLogicImmediate12BitsParameters(Token destinationRegister, Token operandRegister, Token offset) throws EncodingException;

    void generateMovRegistersParameters(Token destinationRegister, Token sourceRegister) throws EncodingException;

    void generatePushPopParameters(List<Token> registerList);

    void generatePush(Token instruction);

    void generatePop(Token instruction);

    void generateMovwR(Token instruction);

    void generateBytes(List<Token> values);

    void generateLdrb(ConditionCode conditionCode, Token flags);

    void generateMovwl(Token destinationRegister, int address);

    int getCurrentAddress();

    List<String> getProgram();

    byte[] generateProgram();
}
