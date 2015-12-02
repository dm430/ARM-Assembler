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
    public static final String CMP_CODE = "25";
    public static final String ADD_CODE = "29";
    public static final String AND_CODE = "21";
    public static final String ORR_CODE = "39";
    public static final String SUB_CODE = "25";
    public static final String MOV_CODE = "30";
    public static final String MOVT_CODE = "34";

    public enum ConditionCode {
        EQUAL(""), NOT_EQUAL("tree"), LESS_THAN(""),
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
        String littleEndian = reverseEndianess(instruction.toString());
        program.add(littleEndian);
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

        instruction.append(toHexString(base, 4));
        instruction.append(toHexString(destination, 4));
        instruction.append(toHexString(baseOffset, 12));

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
        ConditionCode conditionCode = getConditionCode(instruction);

        this.instruction.append(conditionCode);
        this.instruction.append(AND_CODE);
    }

    public void generateOrr(Token instruction) {
        ConditionCode conditionCode = getConditionCode(instruction);

        this.instruction.append(conditionCode);
        this.instruction.append(ORR_CODE);
    }

    public void generateAdd(Token instruction) {
        ConditionCode conditionCode = getConditionCode(instruction);

        this.instruction.append(conditionCode);
        this.instruction.append(ADD_CODE);
    }

    public void generateSub(Token instruction) {
        ConditionCode conditionCode = getConditionCode(instruction);

        this.instruction.append(conditionCode);
        this.instruction.append(SUB_CODE);
    }

    public void generateMovt(Token instruction) {
        ConditionCode conditionCode = getConditionCode(instruction);

        this.instruction.append(conditionCode);
        this.instruction.append(MOVT_CODE);
    }

    public void generateMovw(Token instruction) {
        ConditionCode conditionCode = getConditionCode(instruction);

        this.instruction.append(conditionCode);
        this.instruction.append(MOV_CODE);
    }

    public void generateCmp(Token instruction) {
        ConditionCode conditionCode = getConditionCode(instruction);

        this.instruction.append(conditionCode);
        this.instruction.append(CMP_CODE);
    }

    public void generateMovImmediateParameters(Token destinationRegister, Token value) throws EncodingException {
        int register = getRegisterNumber(destinationRegister);
        int immediateValue = Integer.parseInt(value.getLexeme());

        if ((INSTRUCTION_SIZE - Integer.numberOfLeadingZeros(register)) > MAX_REGISTERS) {
            throw new EncodingException(register + " is not a valid register number");
        }

        if ((INSTRUCTION_SIZE - Integer.numberOfLeadingZeros(immediateValue)) > 16) {
            throw new EncodingException("The number " + immediateValue + " does not fit into 12 bits.");
        }

        // TODO: finish this
    }

    public void generateCmpParametersImmediate(Token register, Token value) throws EncodingException {
        int compareRegister = getRegisterNumber(register);
        int immediateValue = Integer.parseInt(value.getLexeme());

        if ((INSTRUCTION_SIZE - Integer.numberOfLeadingZeros(compareRegister)) > MAX_REGISTERS) {
            throw new EncodingException(compareRegister + " is not a valid register number");
        }

        if ((INSTRUCTION_SIZE - Integer.numberOfLeadingZeros(immediateValue)) > 12) {
            throw new EncodingException("The number " + immediateValue + " does not fit into 12 bits.");
        }

        instruction.append(toHexString(compareRegister, 4));
        instruction.append(0);
        instruction.append(toHexString(immediateValue, 12));

        writeInstruction();
    }

    public int getCurrentAddress() {
        return currentAddress;
    }
}
