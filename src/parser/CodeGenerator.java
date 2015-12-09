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

    // TODO: ALL 'CODES' need to be updated to not include any flags
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
        EQUAL(""), NOT_EQUAL("1"), LESS_THAN(""),
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

    public void generateBranch(Token instruction, int address) {
        ConditionCode conditionCode = getConditionCode(instruction);

        // TODO: change -1 to value, double pass?
        int calculatedAddress = (currentAddress > address) ? address - (currentAddress + 2) : -1;
        String immediateValue = toHexString(calculatedAddress, 24);

        this.instruction.append(conditionCode);
        this.instruction.append(BRANCH_IMMEDIATE_CODE);
        this.instruction.append(immediateValue);

        writeInstruction();
    }

    public void generateBranchImmediate(Token instruction,  Token branchTo) throws EncodingException {
        ConditionCode conditionCode = getConditionCode(instruction);
        int branchValue = startsWith(branchTo, "0x")
            ? Integer.parseInt(branchTo.getLexeme().substring(2), 16) : Integer.parseInt(branchTo.getLexeme());

        if ((31 - Integer.numberOfLeadingZeros(branchValue)) > 24) {
            throw new EncodingException("The number " + branchTo + " does not fit into 24 bits.");
        }

        String immediateValue = toHexString(branchValue, 24);

        this.instruction.append(conditionCode);
        this.instruction.append(BRANCH_IMMEDIATE_CODE);
        this.instruction.append(immediateValue);

        writeInstruction();
    }

    private void writeInstruction() {
        System.out.println(instruction.toString());
        String littleEndian = reverseEndianness(instruction.toString());
        program.add(littleEndian);
        instruction.setLength(0);
        currentAddress++;
    }

    public void generateRegistersImmediate12BitsParameters(Token destinationRegister, Token baseRegister, Token offset) throws EncodingException {
        int base = getRegisterNumber(baseRegister);
        int destination = getRegisterNumber(destinationRegister);
        int baseOffset = (startsWith(offset, "0x"))
                ? Integer.parseInt(offset.getLexeme().substring(2), 16) : Integer.parseInt(offset.getLexeme());

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

    public void generateStr(ConditionCode conditionCode, Token flags) {
        instruction.append(conditionCode);
        instruction.append(STR_CODE_NO_FLAGS);
    }

    public void generateLdr(ConditionCode conditionCode, Token flags) {
        String instructionHex = LDR_CODE_NO_FLAGS;

        if (flags != null) {
            StringBuilder binary = new StringBuilder();
            String preIndexed = setFlag(flags, "P");
            String addImmediate = setFlag(flags, "U");
            String writeBack = setFlag(flags, "W");

            binary.append("010").append(preIndexed)
                    .append(addImmediate).append(0)
                    .append(writeBack).append(1);

            int decimal = Integer.parseInt(binary.toString(), 2);
            instructionHex = toHexString(decimal, 8);
        }

        instruction.append(conditionCode);
        instruction.append(instructionHex);
    }

    public void generateAnd(Token instruction, Token flags) {
        ConditionCode conditionCode = getConditionCode(instruction);
        String instructionHex = AND_CODE;

        if (flags != null) {
            StringBuilder binary = new StringBuilder();
            String updateProcessorFlags = setFlag(flags, "S");

            binary.append("0010000")
                    .append(updateProcessorFlags);

            int decimal = Integer.parseInt(binary.toString(), 2);
            instructionHex = toHexString(decimal, 8);
        }

        this.instruction.append(conditionCode);
        this.instruction.append(instructionHex);
    }

    public void generateOrr(Token instruction, Token flags) {
        ConditionCode conditionCode = getConditionCode(instruction);
        String instructionHex = ORR_CODE;

        if (flags != null) {
            StringBuilder binary = new StringBuilder();
            String updateProcessorFlags = setFlag(flags, "S");

            binary.append("0011100")
                    .append(updateProcessorFlags);

            int decimal = Integer.parseInt(binary.toString(), 2);
            instructionHex = toHexString(decimal, 8);
        }
        this.instruction.append(conditionCode);
        this.instruction.append(instructionHex);
    }

    public void generateAdd(Token instruction, Token flags) {
        ConditionCode conditionCode = getConditionCode(instruction);
        String instructionHex = ADD_CODE;

        if (flags != null) {
            StringBuilder binary = new StringBuilder();
            String updateProcessorFlags = setFlag(flags, "S");

            binary.append("00010100")
                    .append(updateProcessorFlags);

            int decimal = Integer.parseInt(binary.toString(), 2);
            instructionHex = toHexString(decimal, 8);
        }

        this.instruction.append(conditionCode);
        this.instruction.append(instructionHex);
    }

    public void generateSub(Token instruction, Token flags) {
        ConditionCode conditionCode = getConditionCode(instruction);
        String instructionHex = SUB_CODE;

        if (flags != null) {
            StringBuilder binary = new StringBuilder();
            String updateProcessorFlags = setFlag(flags, "S");

            binary.append("0010010")
                    .append(updateProcessorFlags);

            int decimal = Integer.parseInt(binary.toString(), 2);
            instructionHex = toHexString(decimal, 8);
        }

        this.instruction.append(conditionCode);
        this.instruction.append(instructionHex);
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
        int immediateValue = (startsWith(value, "0x"))
                ? Integer.parseInt(value.getLexeme().substring(2), 16) : Integer.parseInt(value.getLexeme());

        if ((INSTRUCTION_SIZE - Integer.numberOfLeadingZeros(register)) > MAX_REGISTERS) {
            throw new EncodingException(register + " is not a valid register number");
        }

        if ((INSTRUCTION_SIZE - Integer.numberOfLeadingZeros(immediateValue)) > 16) {
            throw new EncodingException("The number " + immediateValue + " does not fit into 12 bits.");
        }

        String hexValue = toHexString(immediateValue, 16);

        instruction.append(hexValue.subSequence(0, 1));
        instruction.append(register);
        instruction.append(hexValue.substring(1));

        writeInstruction();
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

    public void generateLogicImmediate12BitsParameters(Token destinationRegister, Token operandRegister, Token offset) throws EncodingException {
        int destRegister = getRegisterNumber(destinationRegister);
        int opRegister = getRegisterNumber(operandRegister);
        int immediateValue = (startsWith(offset, "0x"))
                ? Integer.parseInt(offset.getLexeme().substring(2), 16) : Integer.parseInt(offset.getLexeme());

        if ((INSTRUCTION_SIZE - Integer.numberOfLeadingZeros(destRegister)) > MAX_REGISTERS) {
            throw new EncodingException(destinationRegister + " is not a valid register number");
        }

        if ((INSTRUCTION_SIZE - Integer.numberOfLeadingZeros(opRegister)) > MAX_REGISTERS) {
            throw new EncodingException(opRegister + " is not a valid register number");
        }

        String encodedValue = encodeModifiedImmediate(immediateValue);

        instruction.append(toHexString(opRegister, 4));
        instruction.append(toHexString(destRegister, 4));
        instruction.append(encodedValue);

        writeInstruction();
    }

    public int getCurrentAddress() {
        return currentAddress;
    }

    public List<String> getProgram() {
        return program;
    }

    public byte[] generateProgram() {
        StringBuilder programBuilder = new StringBuilder();
        this.program.forEach(instruction -> programBuilder.append(instruction));

        byte[] program = new byte[programBuilder.toString().length() / 2];
        for (int i = 0; i < program.length; i++) {
            int index = i * 2;
            int value = Integer.parseInt(programBuilder.toString().substring(index, index + 2), 16);
            program[i] = (byte) value;
        }

        return program;
    }
}
