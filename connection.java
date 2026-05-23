package com.example.calculator;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView displayTextView;
    private CalculatorEngine calculator;
    private boolean lastKeyWasOperator = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Verify layout file name matches

        displayTextView = findViewById(R.id.displayTextView);
        calculator = new CalculatorEngine();

        // 1. DIGIT AND DECIMAL INTERCEPTOR
        View.OnClickListener digitListener = v -> {
            Button button = (Button) v;
            handleDigitInput(button.getText().toString());
        };

        // 2. OPERATOR INTERCEPTOR
        View.OnClickListener operatorListener = v -> {
            Button button = (Button) v;
            handleOperatorInput(button.getText().toString());
        };

        // Bind Digits (Map these IDs cleanly to match your XML names)
        findViewById(R.id.btnDot).setOnClickListener(digitListener);
        findViewById(R.id.btnZero).setOnClickListener(digitListener);
        findViewById(R.id.btnOne).setOnClickListener(digitListener);
        findViewById(R.id.btnTwo).setOnClickListener(digitListener);
        findViewById(R.id.btnThree).setOnClickListener(digitListener);
        findViewById(R.id.btnFour).setOnClickListener(digitListener);
        findViewById(R.id.btnFive).setOnClickListener(digitListener);
        findViewById(R.id.btnSix).setOnClickListener(digitListener);
        findViewById(R.id.btnSeven).setOnClickListener(digitListener);
        findViewById(R.id.btnEight).setOnClickListener(digitListener);
        findViewById(R.id.btnNine).setOnClickListener(digitListener);

        // Bind Operational Elements
        findViewById(R.id.btnPlus).setOnClickListener(operatorListener);
        findViewById(R.id.btnMinus).setOnClickListener(operatorListener);
        findViewById(R.id.btnMultiply).setOnClickListener(operatorListener);
        findViewById(R.id.btnDivide).setOnClickListener(operatorListener);

        // Standard Equals Action
        findViewById(R.id.btnEquals).setOnClickListener(v -> handleEquals());

        // 3. CLEAR (C) vs ALL CLEAR (AC)
        findViewById(R.id.btnAC).setOnClickListener(v -> {
            calculator.allClear();
            displayTextView.setText("0");
            lastKeyWasOperator = false;
        });

        findViewById(R.id.btnC).setOnClickListener(v -> {
            // Just clears current working screen buffer view, retains math state
            displayTextView.setText("0");
            lastKeyWasOperator = false;
        });

        // 4. MEMORY HOOKS
        findViewById(R.id.btnMPlus).setOnClickListener(v -> calculator.memoryAdd(getDisplayValue()));
        findViewById(R.id.btnMMinus).setOnClickListener(v -> calculator.memorySubtract(getDisplayValue()));
        findViewById(R.id.btnMR).setOnClickListener(v -> displayTextView.setText(calculator.formatResult(calculator.memoryRecall())));
        findViewById(R.id.btnMC).setOnClickListener(v -> calculator.memoryClear());

        // 5. HISTORY TRIGGER HOOK
        findViewById(R.id.btnHistory).setOnClickListener(v -> {
            if (calculator.getHistory().isEmpty()) {
                Toast.makeText(this, "No history logs yet!", Toast.LENGTH_SHORT).show();
            } else {
                // Toast shows last run line item. You can expand this into a custom dialog if desired!
                String lastRun = calculator.getHistory().get(calculator.getHistory().size() - 1);
                Toast.makeText(this, lastRun, Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- Core Routing Helpers for Shared Inputs ---

    private void handleDigitInput(String key) {
        String currentText = displayTextView.getText().toString();

        if (calculator.shouldClearDisplayOnType() || currentText.equals("0") || currentText.equals("Error")) {
            currentText = "";
        }

        // Rule Validation: Block multiple decimal points (3..5)
        if (key.equals(".")) {
            if (currentText.contains(".")) return;
            if (currentText.isEmpty()) currentText = "0";
        }

        displayTextView.setText(currentText + key);
        lastKeyWasOperator = false;
    }

    private void handleOperatorInput(String operator) {
        String currentText = displayTextView.getText().toString();

        if (currentText.isEmpty() || currentText.equals("Error")) {
            displayTextView.setText("0");
            currentText = "0";
        }

        double val = Double.parseDouble(currentText);

        // Rule Validation: Double operational taps in a row override state
        if (lastKeyWasOperator) {
            calculator.onOperatorClicked(val, operator);
            return;
        }

        String chainedResult = calculator.onOperatorClicked(val, operator);
        if (chainedResult != null) {
            displayTextView.setText(chainedResult);
        }
        lastKeyWasOperator = true;
    }

    private void handleEquals() {
        String currentText = displayTextView.getText().toString();
        if (currentText.isEmpty() || currentText.equals("Error")) {
            displayTextView.setText("0");
            return;
        }

        displayTextView.setText(calculator.onEqualClicked(Double.parseDouble(currentText)));
        lastKeyWasOperator = false;
    }

    private void handleBackspace() {
        String currentText = displayTextView.getText().toString();
        if (currentText.length() > 1 && !currentText.equals("Error")) {
            displayTextView.setText(currentText.substring(0, currentText.length() - 1));
        } else {
            displayTextView.setText("0");
        }
    }

    private double getDisplayValue() {
        try {
            return Double.parseDouble(displayTextView.getText().toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // --- 6. GLOBAL PHYSICAL KEYBOARD MAPPING HOOKS ---
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0: handleDigitInput("0"); return true;
            case KeyEvent.KEYCODE_1: handleDigitInput("1"); return true;
            case KeyEvent.KEYCODE_2: handleDigitInput("2"); return true;
            case KeyEvent.KEYCODE_3: handleDigitInput("3"); return true;
            case KeyEvent.KEYCODE_4: handleDigitInput("4"); return true;
            case KeyEvent.KEYCODE_5: handleDigitInput("5"); return true;
            case KeyEvent.KEYCODE_6: handleDigitInput("6"); return true;
            case KeyEvent.KEYCODE_7: handleDigitInput("7"); return true;
            case KeyEvent.KEYCODE_8: handleDigitInput("8"); return true;
            case KeyEvent.KEYCODE_9: handleDigitInput("9"); return true;
            case KeyEvent.KEYCODE_PERIOD: handleDigitInput("."); return true;
            
            // Math operations mappings
            case KeyEvent.KEYCODE_PLUS: handleOperatorInput("+"); return true;
            case KeyEvent.KEYCODE_MINUS: handleOperatorInput("-"); return true;
            case KeyEvent.KEYCODE_STAR: handleOperatorInput("×"); return true;
            case KeyEvent.KEYCODE_SLASH: handleOperatorInput("÷"); return true;
            
            // Action executions mappings
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                handleEquals();
                return true;
            case KeyEvent.KEYCODE_DEL: // Backspace Key mapping
                handleBackspace();
                return true;
            case KeyEvent.KEYCODE_ESCAPE: // Escape clears system completely (AC)
                calculator.allClear();
                displayTextView.setText("0");
                lastKeyWasOperator = false;
                return true;
                
            default:
                return super.onKeyDown(keyCode, event);
        }
    }
}