package parser;

import lexer.Token;
import parser.exceptions.EncodingException;

import java.util.ArrayList;
import java.util.List;

import static parser.ParserUtils.*;

/**
 * Created by devin on 11/28/15.
 */
public class CodeGenerator {

    public static final String BRANCH_IMMEDIATE_CODE = "A";
    public static final String STR_CODE_NO_FLAGS = "40";
    public static final String LDR_CODE_NO_FLAGS = "41";
    public static final int INSTRUCTION_SIZE = 31;
    public static final int MAX_REGISTERS = 15;

    public enum ConditionCode {
        EQUAL(""), NOT_EQUAL(""), LESS_THAN(""),
        LESS_THAN_EQUAL(""), GRATER_THAN(""),
        GREATER_THAN_EQUAL(""), ALWAYS("E");

        private String hexValue;

        ConditionCode(String hexValue) {
            this.hexValue = hexValue;
        }

        @Override
        public String toString() {
            return hexValue;
        }
    }

    private List<String> program;
    private int currentAddress;
    private StringBuilder instruction;

    public CodeGenerator() {
        this.program = new ArrayList<>();
        this.instruction = new StringBuilder();
    }

    public void generateBranch(ConditionCode conditionCode, int address) {

        currentAddress++;
    }

    public void generateBranchImmediate(ConditionCode conditionCode, int numberToJump) throws EncodingException {
        if ((31 - Integer.numberOfLeadingZeros(numberToJump)) > 24) {
            throw new EncodingException("The number " + numberToJump + " does not fit into 24 bits.");
        }

        String immediateValue = Integer.toHexString(numberToJump);

        instruction.append(conditionCode);
        instruction.append(BRANCH_IMMEDIATE_CODE);
        instruction.append(immediateValue);

        writeInstruction();
    }

    private void writeInstruction() {
        program.add(instruction.toString());
        instruction.setLength(0);
        currentAddress++;
    }

    public void generateRegistersImmediate12BitsParameters(Token destinationRegister, Token baseRegister, Token offset) throws EncodingException {
        int base = getRegisterNumber(baseRegister);
        int destination = getRegisterNumber(destinationRegister);
        int baseOffset =  Integer.parseInt(offset.getLexeme());

        if ((INSTRUCTION_SIZE - Integer.numberOfLeadingZeros(base)) > MAX_REGISTERS) {
            throw new EncodingException(base + " is not a valid register number");
        }

        if ((INSTRUCTION_SIZE - Integer.numberOfLeadingZeros(base)) > MAX_REGISTERS) {
            throw new EncodingException(destination + " is not a valid register number");
        }

        if ((INSTRUCTION_SIZE - Integer.numberOfLeadingZeros(baseOffset)) > 12) {
            throw new EncodingException("The number " + baseOffset + " does not fit into 12 bits.");
        }

        instruction.append(Integer.toHexString(base));
        instruction.append(Integer.toHexString(destination));
        instruction.append(Integer.toHexString(baseOffset));

        writeInstruction();
    }

    public void generateStr(ConditionCode conditionCode) {
        instruction.append(conditionCode);
        instruction.append(STR_CODE_NO_FLAGS);
    }

    public void generateLdr(ConditionCode conditionCode) {
        instruction.append(conditionCode);
        instruction.append(LDR_CODE_NO_FLAGS);
    }

    public void generateAnd(Token instruction) {
        // TODO: 11/30/15  
    }

    public void generateOrr(Token instruction) {
        // TODO: 11/30/15  
    }

    public void generateAdd(Token instruction) {
        // TODO: 11/30/15  
    }

    public void generateSub(Token instruction) {
        // TODO: 11/30/15  
    }

    public void generateMovt(Token instruction) {
        // // TODO: 11/30/15  
    }

    public void generateMovw(Token instruction) {
        // // TODO: 11/30/15  
    }

    public void generateCmp(Token instruction) {
        // // TODO: 11/30/15  
    }

    public void generateMovImmediateParameters(Token destinationRegister, Token value) {
        // TODO: 11/30/15  
    }

    public int getCurrentAddress() {
        return currentAddress;
    }
}
