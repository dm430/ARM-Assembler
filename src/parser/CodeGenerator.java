package parser;

import parser.exceptions.EncodingException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devin on 11/28/15.
 */
public class CodeGenerator {
    private static final String BRANCH_OPCODE = "A";

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

    public CodeGenerator() {
        this.program = new ArrayList<>();
    }

    public void generateBranch(ConditionCode conditionCode, int address) {

        currentAddress++;
    }

    public void generateBranchImmediate(ConditionCode conditionCode, int numberToJump) throws EncodingException {
        if ((31 - Integer.numberOfLeadingZeros(numberToJump)) > 24) {
            throw new EncodingException("The number " + numberToJump + " does not fit into 24 bits.");
        }

        StringBuilder instruction = new StringBuilder();
        String immediateValue = Integer.toHexString(numberToJump);

        instruction.append(conditionCode);
        instruction.append(BRANCH_OPCODE);
        instruction.append(immediateValue);

        program.add(instruction.toString());
        currentAddress++;
    }

    public int getCurrentAddress() {
        return currentAddress;
    }
}
