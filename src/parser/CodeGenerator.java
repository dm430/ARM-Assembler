package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by devin on 11/28/15.
 */
public class CodeGenerator {
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
        // TODO:   

        currentAddress++;
    }

    public int getCurrentAddress() {
        return currentAddress;
    }
}
