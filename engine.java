package com.example.calculator; // Change this to match your package line

import java.util.ArrayList;
import java.util.List;

public class CalculatorEngine {
    private Double firstOperand = null;
    private String pendingOperator = null;
    private boolean isResettingInput = false;
    
    // Memory and History States
    private double memoryRegister = 0.0;
    private final List<String> calculationHistory = new ArrayList<>();

    public String onOperatorClicked(double currentDisplayValue, String nextOperator) {
        String displayResult = null;

        if (firstOperand == null) {
            firstOperand = currentDisplayValue;
        } else if (pendingOperator != null && !isResettingInput) {
            double result = calculate(firstOperand, currentDisplayValue, pendingOperator);
            
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                allClear();
                isResettingInput = true;
                return "Error";
            }
            
            // Add to history log
            calculationHistory.add(formatResult(firstOperand) + " " + pendingOperator + " " + formatResult(currentDisplayValue) + " = " + formatResult(result));
            
            firstOperand = result;
            displayResult = formatResult(result);
        } else {
            // User swapped operator mid-way
            firstOperand = currentDisplayValue;
        }

        pendingOperator = nextOperator;
        isResettingInput = true; 
        return displayResult;
    }

    public String onEqualClicked(double currentDisplayValue) {
        if (pendingOperator != null && firstOperand != null) {
            double result = calculate(firstOperand, currentDisplayValue, pendingOperator);
            
            if (Double.isInfinite(result) || Double.isNaN(result)) {
                allClear();
                isResettingInput = true;
                return "Error";
            }

            calculationHistory.add(formatResult(firstOperand) + " " + pendingOperator + " " + formatResult(currentDisplayValue) + " = " + formatResult(result));
            
            allClear();
            firstOperand = result; // Result can be chained seamlessly as next input
            isResettingInput = true; 
            
            return formatResult(result);
        }
        return formatResult(currentDisplayValue);
    }

    public boolean shouldClearDisplayOnType() {
        if (isResettingInput) {
            isResettingInput = false;
            return true;
        }
        return false;
    }

    // AC: Resets everything back to defaults
    public void allClear() {
        firstOperand = null;
        pendingOperator = null;
        isResettingInput = false;
    }

    private double calculate(double op1, double op2, String operator) {
        switch (operator) {
            case "+": return op1 + op2;
            case "-": return op1 - op2;
            case "×":
            case "*": return op1 * op2;
            case "÷":
            case "/": return op2 == 0 ? Double.NaN : op1 / op2;
            default: return op2;
        }
    }

    public String formatResult(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return "Error";
        if (value % 1 == 0.0) {
            return String.valueOf((long) value);
        } else {
            return String.valueOf(value);
        }
    }

    // --- Memory Operations ---
    public void memoryAdd(double value) { memoryRegister += value; }
    public void memorySubtract(double value) { memoryRegister -= value; }
    public double memoryRecall() { return memoryRegister; }
    public void memoryClear() { memoryRegister = 0.0; }

    // --- History Accessor ---
    public List<String> getHistory() { return calculationHistory; }
}