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
    private TextView trapezoidOutput;

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

    private Double calculateByTrapezoidMethod(String function, Integer leftLimit, Integer rightLimit, Integer roundNumber) {
        Integer segments = 5;
        Integer nodes = segments + 1;
        Double step = (rightLimit - leftLimit) / (double)segments;

        Double sum = 0.0;

        for (int i = 1; i < nodes - 1; ++i) {
            Context context  = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();

            Double x = leftLimit + i * step;
            System.out.println(x);
            String currentExpression = "const x = " + x + "; " + function;
            String result =  context.evaluateString(scriptable, currentExpression,"Javascript",1,null).toString();
            Double resultDbl = Double.parseDouble(result);
            Double roundedDbl = roundToN(resultDbl, roundNumber);
            System.out.println(roundedDbl);
            sum += roundedDbl;
        }

        Context context  = Context.enter();
        context.setOptimizationLevel(-1);
        Scriptable scriptable = context.initStandardObjects();

        Double x = leftLimit + 0 * step;
        String currentExpression = "const x = " + x + "; " + function;
        String result =  context.evaluateString(scriptable, currentExpression,"Javascript",1,null).toString();
        Double resultDbl = Double.parseDouble(result);
        Double roundedDbl = roundToN(resultDbl, roundNumber);
        System.out.println("1 " + roundedDbl);

        context  = Context.enter();
        context.setOptimizationLevel(-1);
        scriptable = context.initStandardObjects();

        x = leftLimit + (nodes - 1) * step;
        currentExpression = "const x = " + x + "; " + function;
        result =  context.evaluateString(scriptable, currentExpression,"Javascript",1,null).toString();
        resultDbl = Double.parseDouble(result);
        Double roundedDbl2 = roundToN(resultDbl, roundNumber);
        System.out.println("2 " + roundedDbl2);
        sum += (roundedDbl + roundedDbl2) / 2;

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
        this.trapezoidOutput = findViewById(R.id.trapezoidOuput);
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

    public void trapezoidButtonClick(View view) {
        if (this.isDataValid()) {
            String initialFunction = this.functionInput.getText().toString();
            String function = this.editInitialFunction(initialFunction);
            Integer leftLimit = Integer.parseInt(this.leftLimitInput.getText().toString());
            Integer rightLimit = Integer.parseInt(this.rightLimitInput.getText().toString());
            Integer initialRoundNumber = this.roundNumberInput.getText().toString().split("\\.")[1].length();
            Integer roundNumber = initialRoundNumber + 2;

            Double integral = this.calculateByTrapezoidMethod("1 / Math.log(x)", leftLimit, rightLimit, roundNumber);
            this.trapezoidOutput.setText(integral.toString());
        }
    }
}