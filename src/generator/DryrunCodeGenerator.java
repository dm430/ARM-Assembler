package generator;

import lexer.Token;
import parser.exceptions.EncodingException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devin on 12/9/15.
 */
public class DryrunCodeGenerator implements CodeGenerator {
    private int programAdress;

    @Override
    public void generateBranchLinkImmediate(Token instruction, Token branchTo) throws EncodingException {
        programAdress++;
    }

    @Override
    public void generateBranchLink(Token instruction, int address) {
        programAdress++;
    }

    @Override
    public void generateBranch(Token instruction, int address) {
        programAdress++;
    }

    @Override
    public void generateBranchImmediate(Token instruction, Token branchTo) throws EncodingException {
        programAdress++;
    }

    @Override
    public void generateRegistersParameters(Token destinationRegister, Token baseRegister, Token offsetRegister) {
        programAdress++;
    }

    @Override
    public void generateLdrStrImmediate12BitsParameters(Token destinationRegister, Token baseRegister, Token offset) throws EncodingException {
        programAdress++;
    }

    @Override
    public void generateStr(ConcreteCodeGenerator.ConditionCode conditionCode, Token flags) {
        // Do nothing
    }

    @Override
    public void generateLdr(ConcreteCodeGenerator.ConditionCode conditionCode, Token flags) {
        // Do nothing
    }

    @Override
    public void generateAnd(Token instruction, Token flags) {
        // Do nothing
    }

    @Override
    public void generateOrr(Token instruction, Token flags) {
        // Do nothing
    }

    @Override
    public void generateAdd(Token instruction, Token flags) {
        // Do nothing
    }

    @Override
    public void generateSub(Token instruction, Token flags) {
        // Do nothing
    }

    @Override
    public void generateMovt(Token instruction) {
        // Do nothing
    }

    @Override
    public void generateMovw(Token instruction) {
        // Do nothing
    }

    @Override
    public void generateCmp(Token instruction) {
        // Do nothing
    }

    @Override
    public void generateMovImmediateParameters(Token destinationRegister, Token value) throws EncodingException {
        programAdress++;
    }

    @Override
    public void generateCmpParametersImmediate(Token register, Token value) throws EncodingException {
        programAdress++;
    }

    @Override
    public void generateLogicImmediate12BitsParameters(Token destinationRegister, Token operandRegister, Token offset) throws EncodingException {
        programAdress++;
    }

    @Override
    public void generateMovRegistersParameters(Token destinationRegister, Token sourceRegister) throws EncodingException {
        programAdress++;
    }

    @Override
    public void generatePushPopParameters(List<Token> registerList) {
        programAdress++;
    }

    @Override
    public void generatePush(Token instruction) {
        // Do nothing
    }

    @Override
    public void generatePop(Token instruction) {
        // Do nothing
    }

    @Override
    public void generateMovwR(Token instruction) {
        // Do nothing
    }

    @Override
    public int getCurrentAddress() {
        return programAdress;
    }

    @Override
    public List<String> getProgram() {
        return new ArrayList<>();
    }

    @Override
    public byte[] generateProgram() {
        return new byte[0];
    }
}
