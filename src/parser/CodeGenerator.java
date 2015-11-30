package parser;

import lexer.Token;
import parser.exceptions.EncodingException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devin on 11/28/15.
 */
public class CodeGenerator {

    public static final String BRANCH_IMMEDIATE_CODE = "A";

    public enum ConditionCode {
        EQUAL(""), NOT_EQUAL(""), LESS_THAN(""),
        LESS_THAN_EQUAL(""), GRATER_THAN(""),
        GREATER_THAN_EQUAL(""), ALWAYS("");

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

    public void generateLdrStrParametersImmediate(Token destinationRegister, Token baseRegister, Token offset) {
    }

    public void generateStr(ConditionCode conditionCode) {
    }

    public void generateLdr(ConditionCode conditionCode) {
    }

    public int getCurrentAddress() {
        return currentAddress;
    }
}
