package com.example.integration_methods;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class MainActivity extends AppCompatActivity {
    private EditText functionInput;
    private EditText leftLimitInput;
    private EditText rightLimitInput;
    private EditText roundNumberInput;
    private TextView rectangleOutput;

    private double roundToN(double value, int digits) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(digits, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private Double calculateByRectangleMethod(String function, Integer leftLimit, Integer rightLimit, Integer roundNumber) {
        Integer segments = 5;
        Double step = (rightLimit - leftLimit) / (double)segments;

        Double sum = 0.0;

        for (int i = 0; i < segments; ++i) {
            Context context  = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();

            Double x = leftLimit + i * step;
            Double xH2 = x + step / 2;
            String currentExpression = "const x = " + xH2 + "; " + function;
            String result =  context.evaluateString(scriptable, currentExpression,"Javascript",1,null).toString();
            Double resultDbl = Double.parseDouble(result);
            Double roundedDbl = roundToN(resultDbl, roundNumber);
            sum += roundedDbl;
        }

        return sum * step;
    }

    private Boolean isDataValid() {
        return true;
    }

    private String editInitialFunction(String initialFunction) {
        return initialFunction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.functionInput = findViewById(R.id.functionInput);
        this.leftLimitInput = findViewById(R.id.leftLimitInput);
        this.rightLimitInput = findViewById(R.id.rightLimitInput);
        this.roundNumberInput = findViewById(R.id.roundNumberInput);
        this.rectangleOutput = findViewById(R.id.rectangleOutput);
    }

    public void rectangleButtonClick(View view) {
        if (this.isDataValid()) {
            String initialFunction = this.functionInput.getText().toString();
            String function = this.editInitialFunction(initialFunction);
            Integer leftLimit = Integer.parseInt(this.leftLimitInput.getText().toString());
            Integer rightLimit = Integer.parseInt(this.rightLimitInput.getText().toString());
            Integer initialRoundNumber = this.roundNumberInput.getText().toString().split("\\.")[1].length();
            Integer roundNumber = initialRoundNumber + 2;

            Double integral = this.calculateByRectangleMethod(function, leftLimit, rightLimit, roundNumber);
            this.rectangleOutput.setText(integral.toString());
        }
    }
}