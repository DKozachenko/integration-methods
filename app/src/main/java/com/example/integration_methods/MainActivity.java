package com.example.integration_methods;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class MainActivity extends AppCompatActivity {
    private double roundToN(double value, int digits) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(digits, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private Double calculateByRectangleMethod(String function, Integer leftLimit, Integer rightLimit, Integer initialRoundNumber) {
        Integer segments = 5;
        Double step = (rightLimit - leftLimit) / (double)segments;
        Integer roundNumber = initialRoundNumber + 3;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Double result = this.calculateByRectangleMethod("1 / Math.log(x);", 2, 5, 2);
        System.out.println(result);
    }


}