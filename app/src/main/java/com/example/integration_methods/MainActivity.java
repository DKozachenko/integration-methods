package com.example.integration_methods;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private EditText functionInput;
    private EditText leftLimitInput;
    private EditText rightLimitInput;
    private EditText roundNumberInput;
    private TextView rectangleOutput;
    private TextView trapezoidOutput;
    private TextView simpsonOutput;

    /** Начальное значение кол-ва отрезков разбиения **/
    private final Integer START_SEGMENT_VALUE = 2;

    /** Округление
     * @param value - значение
     * @param digits - кол-во знаков после запятой
     * @return округленное значение
     **/
    private double roundToN(double value, int digits) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(digits, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /** Инициализация переменных из разметки **/
    private void initializeVariables() {
        this.functionInput = findViewById(R.id.functionInput);
        this.leftLimitInput = findViewById(R.id.leftLimitInput);
        this.rightLimitInput = findViewById(R.id.rightLimitInput);
        this.roundNumberInput = findViewById(R.id.roundNumberInput);
        this.rectangleOutput = findViewById(R.id.rectangleOutput);
        this.trapezoidOutput = findViewById(R.id.trapezoidOuput);
        this.simpsonOutput = findViewById(R.id.simpsonOutput);
    }

    /** Вычисление значения функции
     * @param function - функция
     * @param value - аргумент
     * @return значение функции
     **/
    private Double calculateFunctionValue(String function, Double value) {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        Scriptable scriptable = context.initStandardObjects();

        String currentExpression = String.format(Locale.ROOT, "const x = %f; %s", value, function);
        String result = "0.0";

        try {
            result =  context.evaluateString(scriptable, currentExpression,"Javascript",1,null).toString();
        } catch (Exception e) {
            this.showMessage("Ошибка при вычислении: " + e.getMessage());
        }

        return Double.parseDouble(result);
    }

    /** Одна итерация с помощью метода средних прямоугольников
     * @param parameters - параметры
     * @param segments - кол-во отрезков разбиения
     * @return приближенное значение интеграла
     **/
    private Double makeRectangleMethodIteration(Parameters parameters, Integer segments) {
        Double step = (parameters.getRightLimit() - parameters.getLeftLimit()) / segments;

        Double sum = 0.0;

        for (int i = 0; i < segments; ++i) {
            Double x = parameters.getLeftLimit() + i * step;
            Double xWithStepHalf = x + step / 2;
            Double result = this.calculateFunctionValue(parameters.getFunction(), xWithStepHalf);
            Double roundedResult = roundToN(result, parameters.getRoundNumber());
            sum += roundedResult;
        }

        return this.roundToN(sum * step, 5);
    }

    /** Вычисление интеграла с определенной точностью с помощью метода средних прямоугольников
     * @param parameters - параметры
     * @return максимальное приближение интеграла с указанной точностью
     **/
    private Double calculateByRectangleMethod(Parameters parameters) {
        Double lastApproximation = Double.MAX_VALUE;
        Integer segments = this.START_SEGMENT_VALUE;
        Double currentApproximation = this.makeRectangleMethodIteration(parameters, segments);

        while (Math.abs(lastApproximation - currentApproximation) >= parameters.getAccuracy()) {
            lastApproximation = currentApproximation;
            segments *= 2;
            currentApproximation = this.makeRectangleMethodIteration(parameters, segments);
        }

        return currentApproximation;
    }

    /** Одна итерация с помощью метода средних трапеций
     * @param parameters - параметры
     * @param segments - кол-во отрезков разбиения
     * @return приближенное значение интеграла
     **/
    private Double makeTrapezoidMethodIteration(Parameters parameters, Integer segments) {
        Integer nodes = segments + 1;
        Double step = (parameters.getRightLimit() - parameters.getLeftLimit()) / segments;

        Double sum = 0.0;

        for (int i = 1; i < nodes - 1; ++i) {
            Double x = parameters.getLeftLimit() + i * step;
            Double result = this.calculateFunctionValue(parameters.getFunction(), x);
            Double roundedResult = roundToN(result, parameters.getRoundNumber());
            sum += roundedResult;
        }

        //первое значение функции
        Double x0 = parameters.getLeftLimit() + 0 * step;
        Double resultFirstNodeValue = this.calculateFunctionValue(parameters.getFunction(), x0);
        Double roundedFirstNodeValue = roundToN(resultFirstNodeValue, parameters.getRoundNumber());

        //последнее значение функции
        Double x2n = parameters.getLeftLimit() + (nodes - 1) * step;
        Double resultLastNodeValue = this.calculateFunctionValue(parameters.getFunction(), x2n);
        Double roundedLastNodeValue = roundToN(resultLastNodeValue, parameters.getRoundNumber());

        sum += (roundedFirstNodeValue + roundedLastNodeValue) / 2;

        return this.roundToN(sum * step, 5);
    }

    /** Вычисление интеграла с определенной точностью с помощью метода трапеций
     * @param parameters - параметры
     * @return максимальное приближение интеграла с указанной точностью
     **/
    private Double calculateByTrapezoidMethod(Parameters parameters) {
        Double lastApproximation = Double.MAX_VALUE;
        Integer segments = this.START_SEGMENT_VALUE;
        Double currentApproximation = this.makeTrapezoidMethodIteration(parameters, segments);

        while (Math.abs(lastApproximation - currentApproximation) >= parameters.getAccuracy()) {
            lastApproximation = currentApproximation;
            segments *= 2;
            currentApproximation = this.makeTrapezoidMethodIteration(parameters, segments);
        }

        return currentApproximation;
    }

    /** Одна итерация с помощью метода Симпсона
     * @param parameters - параметры
     * @param segments - кол-во отрезков разбиения
     * @return приближенное значение интеграла
     **/
    private Double makeSimpsonMethodIteration(Parameters parameters, Integer segments) {
        Integer nodes = segments + 1;
        Double step = (parameters.getRightLimit() - parameters.getLeftLimit()) / segments;

        Double sum = 0.0;
        Double sumEven = 0.0;
        Double sumOdd = 0.0;

        for (int i = 1; i < nodes - 1; ++i) {
            Double x = parameters.getLeftLimit() + i * step;
            Double result = this.calculateFunctionValue(parameters.getFunction(), x);
            Double roundedResult = roundToN(result, parameters.getRoundNumber());

            if (i % 2 == 0) {
                sumEven += roundedResult;
            } else {
                sumOdd += roundedResult;
            }
        }

        sum += sumEven * 2 + sumOdd * 4;

        //первое значение функции
        Double x0 = parameters.getLeftLimit() + 0 * step;
        Double resultFirstNodeValue = this.calculateFunctionValue(parameters.getFunction(), x0);
        Double roundedFirstNodeValue = roundToN(resultFirstNodeValue, parameters.getRoundNumber());

        sum += roundedFirstNodeValue;

        //последнее значение функции
        Double x2n = parameters.getLeftLimit() + (nodes - 1) * step;
        Double resultLastNodeValue = this.calculateFunctionValue(parameters.getFunction(), x2n);
        Double roundedLastNodeValue = roundToN(resultLastNodeValue, parameters.getRoundNumber());

        sum += roundedLastNodeValue;

        return this.roundToN(sum * (step / 3), 5);
    }

    /** Вычисление интеграла с определенной точностью с помощью метода Симпсона
     * @param parameters - параметры
     * @return максимальное приближение интеграла с указанной точностью
     **/
    private Double calculateBySimpsonMethod(Parameters parameters) {
        Double lastApproximation = Double.MAX_VALUE;
        Integer segments = this.START_SEGMENT_VALUE;
        Double currentApproximation = this.makeSimpsonMethodIteration(parameters, segments);

        while (Math.abs(lastApproximation - currentApproximation) >= parameters.getAccuracy()) {
            lastApproximation = currentApproximation;
            segments *= 2;
            currentApproximation = this.makeSimpsonMethodIteration(parameters, segments);
        }

        return currentApproximation;
    }

    /** Сбор параметров в экземпляр класса
     * @return экзмепляр класса Parameters
     **/
    private Parameters getParameters() {
        String initialFunction = this.functionInput.getText().toString();
        String function = this.editInitialFunction(initialFunction);
        Double leftLimit = Double.parseDouble(this.leftLimitInput.getText().toString());
        Double rightLimit = Double.parseDouble(this.rightLimitInput.getText().toString());
        Double accuracy = Double.parseDouble(this.roundNumberInput.getText().toString());
        Integer initialRoundNumber = this.roundNumberInput.getText().toString().split("\\.")[1].length();
        Integer roundNumber = initialRoundNumber + 2;

        return new Parameters(function, leftLimit, rightLimit, roundNumber, accuracy);
    }

    /** Вывод сообщения на экран
     * @param message - сообщение
     **/
    private void showMessage(String message) {
        Toast toast = Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /** Подсчет открывающих круглых скобочек
     * @param function - функция
     * @return кол-во открывающих круглых скобочек
     **/
    private Integer countOpenParentheses(String function) {
        Integer count = 0;

        for (int i = 0; i < function.length(); ++i) {
            if (function.toCharArray()[i] != '(') {
                ++count;
            }
        }

        return count;
    }

    /** Подсчет закрывающих круглых скобочек
     * @param function - функция
     * @return кол-во закрывающих круглых скобочек
     **/
    private Integer countCloseParentheses(String function) {
        Integer count = 0;

        for (int i = 0; i < function.length(); ++i) {
            if (function.toCharArray()[i] != ')') {
                ++count;
            }
        }

        return count;
    }

    /** Проверка валидности введенной функции
     * @return валидна / не валидна
     **/
    private Boolean isInitialFunctionDataValid() {
        String initialFunction = this.functionInput.getText().toString();
        if (initialFunction.length() < 1) {
            this.showMessage("Функция не должна быть пустой");
            return false;
        }

        if (!initialFunction.contains("x")) {
            this.showMessage("У функции нет аргумента 'x'");
            return false;
        }

        if (initialFunction.matches("[A-Za-z&&[^x]]")) {
            this.showMessage("В фунции не должно быть латинских букв");
            return false;
        }

        if (initialFunction.matches("[А-Яа-я]")) {
            this.showMessage("В фунции не должно быть русских букв");
            return false;
        }

        if (this.countOpenParentheses(initialFunction) != this.countCloseParentheses(initialFunction)) {
            this.showMessage("Не хватает открывающей или закрывающей скобки");
            return false;
        }

        if (initialFunction.matches("[!@#$%:;&\\-=]")) {
            this.showMessage("Функция содержит недопустимые символы");
            return false;
        }

        return true;
    }

    /** Проверка валидности левой границы
     * @return валидна / не валидна
     **/
    private Boolean isInitialLeftLimitValid(String leftLimit) {
        if (leftLimit.length() < 1) {
            this.showMessage("Левая граница не может быть пустая");
            return false;
        }

        if (leftLimit.contains(",")) {
            this.showMessage("Разделителем должна являться точка");
            return false;
        }

        return true;
    }

    /** Проверка валидности правой границы
     * @return валидна / не валидна
     **/
    private Boolean isInitialRightLimitValid(String rightLimit) {
        if (rightLimit.length() < 1) {
            this.showMessage("Правая граница не может быть пустая");
            return false;
        }

        if (rightLimit.contains(",")) {
            this.showMessage("Разделителем должна являться точка");
            return false;
        }

        return true;
    }

    /** Проверка валидности округления
     * @return валидно / не валидно
     **/
    private Boolean isInitialRoundValid() {
        String initialRound = this.roundNumberInput.getText().toString();

        if (initialRound.length() < 1) {
            this.showMessage("Округление не может быть пустым");
            return false;
        }

        if (initialRound.contains(",")) {
            this.showMessage("Разделителем должна являться точка");
            return false;
        }

        if (!initialRound.contains(".")) {
            this.showMessage("Округление должно быть десятичным числом");
            return false;
        }

        return true;
    }

    /** Проверка валидности промежутка
     * @return валиден / не валиден
     **/
    private Boolean isIntervalValid(Double leftLimit, Double rightLimit) {
        if (leftLimit >= rightLimit) {
            this.showMessage("Левая граница больше или равна правой");
            return false;
        }

        return true;
    }

    /** Проверка валидности введнных значений
     * @return валидны / не валидны
     **/
    private Boolean isDataValid() {
        String initialLeftLimit = this.leftLimitInput.getText().toString();
        String initialRightLimit = this.rightLimitInput.getText().toString();

        return this.isInitialFunctionDataValid()
                && this.isInitialLeftLimitValid(initialLeftLimit)
                && this.isInitialRightLimitValid(initialRightLimit)
                && this.isInitialRoundValid()
                && this.isIntervalValid(Double.parseDouble(initialLeftLimit), Double.parseDouble(initialRightLimit));
    }

    /** Парсинг функции натурального логарифма для движка JS
     * @return обработанная функция
     **/
    private String parseLnFunction(String function) {
        String parsedFunction = function;
        while (parsedFunction.contains("ln")) {
            parsedFunction = parsedFunction.replace("ln", "Math.log");
        }

        return parsedFunction;
    }

    /** Парсинг функции десятичного логарифма для движка JS
     * @return обработанная функция
     **/
    private String parseLgFunction(String function) {
        String parsedFunction = function;
        while (parsedFunction.contains("lg")) {
            parsedFunction = parsedFunction.replace("lg", "Math.log10");
        }

        return parsedFunction;
    }

    /** Парсинг функции квадратного корня для движка JS
     * @return обработанная функция
     **/
    private String parseSqrtFunction(String function) {
        String parsedFunction = function;

        Integer indexSqrt =  parsedFunction.indexOf("sqrt");
        while (indexSqrt != -1 &&
                (indexSqrt == 0 || parsedFunction.toCharArray()[indexSqrt - 1] != '.')) {

            parsedFunction = parsedFunction.substring(0, indexSqrt) + "Math.sqrt"
                    + parsedFunction.substring(indexSqrt + 4, parsedFunction.length());

            indexSqrt =  parsedFunction.indexOf("sqrt", indexSqrt + 9);
        }

        return parsedFunction;
    }

    /** Парсинг функции синуса для движка JS
     * @return обработанная функция
     **/
    private String parseSinFunction(String function) {
        String parsedFunction = function;

        Integer indexSin =  parsedFunction.indexOf("sin");
        while (indexSin != -1 &&
                (indexSin == 0 || parsedFunction.toCharArray()[indexSin - 1] != '.')) {

            parsedFunction = parsedFunction.substring(0, indexSin) + "Math.sin"
                    + parsedFunction.substring(indexSin + 3, parsedFunction.length());

            indexSin =  parsedFunction.indexOf("sin", indexSin + 8);
        }

        return parsedFunction;
    }

    /** Парсинг функции косинуса для движка JS
     * @return обработанная функция
     **/
    private String parseCosFunction(String function) {
        String parsedFunction = function;

        Integer indexCos =  parsedFunction.indexOf("cos");
        while (indexCos != -1 &&
                (indexCos == 0 || parsedFunction.toCharArray()[indexCos - 1] != '.')) {

            parsedFunction = parsedFunction.substring(0, indexCos) + "Math.cos"
                    + parsedFunction.substring(indexCos + 3, parsedFunction.length());

            indexCos =  parsedFunction.indexOf("cos", indexCos + 8);
        }

        return parsedFunction;
    }

    /** Парсинг функции модуля для движка JS
     * @return обработанная функция
     **/
    private String parseAbsFunction(String function) {
        String parsedFunction = function;

        while (parsedFunction.contains("|")) {
            Integer index =  parsedFunction.indexOf("|");
            String argument = "";

            for (int i = index + 1; i < parsedFunction.length()
                    && parsedFunction.toCharArray()[i] != '|'; ++i) {
                argument += parsedFunction.toCharArray()[i];
            }

            String replacement = String.format("Math.abs(%s)", argument);
            String removeString = parsedFunction.substring(index, index + 2 + argument.length());
            parsedFunction = parsedFunction.replace(removeString, replacement);
        }

        return parsedFunction;
    }

    /** Парсинг степенной функции для движка JS
     * @return обработанная функция
     **/
    private String parsePowFunction(String function) {
        String parsedFunction = function;

        while (parsedFunction.contains("^")) {
            Integer index =  parsedFunction.indexOf("^");
            String base = "";
            Boolean isBaseWithParentheses = false;

            if (parsedFunction.toCharArray()[index - 1] == ')') {
                isBaseWithParentheses = true;
                for (int i = index - 2; i >= 0 && parsedFunction.toCharArray()[i] != '('; --i) {
                    base += parsedFunction.toCharArray()[i];
                }
            } else {
                for (int i = index - 1; i >= 0 && (parsedFunction.toCharArray()[i] == 'x'
                        || Character.isDigit(parsedFunction.toCharArray()[i])); --i) {
                    base += parsedFunction.toCharArray()[i];
                }
            }

            String exponent = "";
            Boolean isExponentWithParentheses = false;

            if (parsedFunction.toCharArray()[index + 1] == '(') {
                isExponentWithParentheses = true;
                for (int i = index + 2; i < parsedFunction.length() && parsedFunction.toCharArray()[i] != ')'; ++i) {
                    exponent += parsedFunction.toCharArray()[i];
                }
            } else {
                for (int i = index + 1; i < parsedFunction.length() && (parsedFunction.toCharArray()[i] == 'x'
                        || Character.isDigit(parsedFunction.toCharArray()[i])); ++i) {
                    exponent += parsedFunction.toCharArray()[i];
                }
            }

            String replacement = String.format("Math.pow(%s, %s)", base, exponent);
            String removeString = "";

            if (isBaseWithParentheses && isExponentWithParentheses) {
                removeString = parsedFunction.substring(index - base.length() - 2, index + 1 + exponent.length() + 2);
            } else if (!isBaseWithParentheses && isExponentWithParentheses) {
                removeString = parsedFunction.substring(index - base.length(), index + 1 + exponent.length() + 2);
            } else if (isBaseWithParentheses && !isExponentWithParentheses) {
                removeString = parsedFunction.substring(index - base.length() - 2, index + 1 + exponent.length());
            } else {
                removeString = parsedFunction.substring(index - base.length(), index + 1 + exponent.length());
            }

            parsedFunction = parsedFunction.replace(removeString, replacement);
        }

        return parsedFunction;
    }

    /** Парсинг функции для движка JS
     * @return конечная обработанная функция для JS
     **/
    private String editInitialFunction(String initialFunction) {
        String finalFunction = initialFunction;

        finalFunction = this.parseLnFunction(finalFunction);
        finalFunction = this.parseLgFunction(finalFunction);
        finalFunction = this.parseSqrtFunction(finalFunction);
        finalFunction = this.parseSinFunction(finalFunction);
        finalFunction = this.parseCosFunction(finalFunction);
        finalFunction = this.parseAbsFunction(finalFunction);
        finalFunction = this.parsePowFunction(finalFunction);

        return finalFunction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initializeVariables();
    }

    /** Обработка нажатия на кнопку "Метод прямоугольников" **/
    public void rectangleButtonClick(View view) {
        if (this.isDataValid()) {
            Parameters parameters = this.getParameters();
            Double integral = this.calculateByRectangleMethod(parameters);
            this.rectangleOutput.setText(integral.toString());
        }
    }

    /** Обработка нажатия на кнопку "Метод трапеций" **/
    public void trapezoidButtonClick(View view) {
        if (this.isDataValid()) {
            Parameters parameters = this.getParameters();
            Double integral = this.calculateByTrapezoidMethod(parameters);
            this.trapezoidOutput.setText(integral.toString());
        }
    }

    /** Обработка нажатия на кнопку "Метод Симпсона" **/
    public void simpsonButtonClick(View view) {
        if (this.isDataValid()) {
            Parameters parameters = this.getParameters();
            Double integral = this.calculateBySimpsonMethod(parameters);
            this.simpsonOutput.setText(integral.toString());
        }
    }
}