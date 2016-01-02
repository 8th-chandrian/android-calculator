package com.example.mac.simplecalculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    //TODO: After clicking equals, if number is pressed, previous results are cleared
    //TODO: After clicking equals, if delete is pressed, previous results are cleared
    //TODO: Make ")(" multiply the contents of parentheses
    //TODO: Ensure that results perfectly fill screen without spilling over (terminate decimals at a certain number)

    String calcString;
    TextView calcDisplay;
    HorizontalScrollView calcDisplayScroll;
    HashMap<String, int[]> operators;

    final int IS_LEFT_ASSOCIATIVE = 0;
    final int IS_RIGHT_ASSOCIATIVE = 1;
    final int ADDITION_PRECEDENCE = 0;
    final int MULTIPLICATION_PRECEDENCE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calcString = "";
        calcDisplay = (TextView) findViewById(R.id.calcDisplay);
        calcDisplayScroll = (HorizontalScrollView) findViewById(R.id.calcDisplayScroll);
        operators = new HashMap<>();

        //For operators HashMap, associativity comes first, then precedence
        operators.put("+", new int[]{IS_LEFT_ASSOCIATIVE, ADDITION_PRECEDENCE});
        operators.put("-", new int[]{IS_LEFT_ASSOCIATIVE, ADDITION_PRECEDENCE});
        operators.put("*", new int[]{IS_LEFT_ASSOCIATIVE, MULTIPLICATION_PRECEDENCE});
        operators.put("/", new int[]{IS_LEFT_ASSOCIATIVE, MULTIPLICATION_PRECEDENCE});
    }

    public void oneClick(View view){
        calcString += "1";
        updateDisplay();
    }
    public void twoClick(View view) {
        calcString += "2";
        updateDisplay();
    }
    public void threeClick(View view){
        calcString += "3";
        updateDisplay();
    }
    public void fourClick(View view) {
        calcString += "4";
        updateDisplay();
    }
    public void fiveClick(View view){
        calcString += "5";
        updateDisplay();
    }
    public void sixClick(View view) {
        calcString += "6";
        updateDisplay();
    }
    public void sevenClick(View view){
        calcString += "7";
        updateDisplay();
    }
    public void eightClick(View view) {
        calcString += "8";
        updateDisplay();
    }
    public void nineClick(View view){
        calcString += "9";
        updateDisplay();
    }
    public void zeroClick(View view) {
        calcString += "0";
        updateDisplay();
    }
    public void decimalClick(View view){
        calcString += ".";
        updateDisplay();
    }
    public void openParsClick(View view){
        calcString += " ( ";
        updateDisplay();
    }
    public void closeParsClick(View view){
        calcString += " ) ";
        updateDisplay();
    }
    public void additionClick(View view){
        calcString += " + ";
        updateDisplay();
    }
    public void subtractionClick(View view){
        calcString += " - ";
        updateDisplay();
    }
    public void multiplicationClick(View view){
        calcString += " * ";
        updateDisplay();
    }
    public void divisionClick(View view){
        calcString += " / ";
        updateDisplay();
    }


    /**
     * Removes the last entered digit or operator
     * @param view
     */
    public void deleteClick(View view){
        //If last character of calcString is digit or decimal point, removes last character
        if(calcString.length() != 0 && (Character.isDigit(calcString.charAt(calcString.length()-1)))
                || calcString.charAt(calcString.length() - 1) == '.'){
            calcString = calcString.substring(0, calcString.length()-1);
        }
        //If calcString contains an error message, clear calcString
        else if(calcString.contains("Error"))
            calcString = "";
        //Otherwise, removes last 3 characters to get rid of operator and surrounding spaces
        else if(calcString.length() != 0){
            calcString = calcString.substring(0, calcString.length()-3);
        }
        updateDisplay();
    }

    /**
     * Clears calcString
     * @param view
     */
    public void clearClick(View view){
        calcString = "";
        updateDisplay();
    }

    /**
     * Updates calcDisplay with calcString text after every button push and scrolls calcDisplayScroll all the way right.
     * If calcString contains an error message, ensures that calcString is not updated and calcDisplay contains only the error.
     */
    public void updateDisplay() {
        if(calcString.contains("Error")){
            //If last character of calcString is digit or decimal point, removes last character
            if(calcString.length() != 0 && (Character.isDigit(calcString.charAt(calcString.length()-1)))
                    || calcString.charAt(calcString.length() - 1) == '.'){
                calcString = calcString.substring(0, calcString.length()-1);
            }
            //Otherwise, removes last 3 characters to get rid of operator and surrounding spaces
            else if(calcString.length() != 0){
                calcString = calcString.substring(0, calcString.length()-3);
            }
        }
        calcDisplay.setText(calcString);
        calcDisplayScroll.postDelayed(new Runnable() {
            public void run() {
                calcDisplayScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 50L);
    }

    /**
     * Only used in equalsClick method, scrolls to the left instead of the right
     */
    public void updateDisplayEquals() {
        calcDisplay.setText(calcString);
        calcDisplayScroll.postDelayed(new Runnable() {
            public void run() {
                calcDisplayScroll.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            }
        }, 50L);
    }

    /**
     * Calculates the solution to the expression contained in calcString
     * Note: does nothing if calcString contains an error message
     * @param view
     */
    public void equalsClick(View view){
        if(calcString.contains("Error")){
            return;
        }

        ArrayList<String> postfix = toPostfix();
        Stack<Double> numStack = new Stack<>();

        try {
            for (String token : postfix) {
                if (isOperator(token)) {
                    Double numTwo = numStack.pop();
                    Double numOne = numStack.pop();
                    Double result = calculate(numOne, numTwo, token);
                    numStack.push(result);
                } else if (!token.equals(""))
                    numStack.push(Double.parseDouble(token));
            }

            calcString = String.valueOf(numStack.pop());
            updateDisplayEquals();
        }
        catch(ArithmeticException e1){
            calcString = e1.getMessage();
            updateDisplayEquals();
        }
        catch (EmptyStackException e2){
            calcString = "Error: invalid syntax";
            updateDisplayEquals();
        }
    }

    /**
     * Converts calcString to a postfix-ordered ArrayList which can then be evaluated
     * @return
     *      The postfix-ordered ArrayList
     */
    public ArrayList<String> toPostfix(){
        String[] input = calcString.split(" ");
        ArrayList<String> output = new ArrayList<>();
        Stack<String> opStack = new Stack<>();

        for(String token : input){

            //Handles operators, ensures that operator on top of the stack is always greater than all below it (if operator is left-associative)
            //or greater than or equal to all below it (if operator is right-associative)
            if(isOperator(token)){
                while(!opStack.empty() && isOperator(opStack.peek())){
                    if((operators.get(token)[0] == 0 && operators.get(token)[1] <= operators.get(opStack.peek())[1])
                            || (operators.get(token)[0] == 1 && operators.get(token)[1] < operators.get(opStack.peek())[1])){
                        output.add(opStack.pop());
                        continue;
                    }
                    break;
                }
                opStack.push(token);
            }

            else if(token.equals("("))
                opStack.push(token);

            else if(token.equals(")")){
                while(!opStack.empty() && !opStack.peek().equals("(")){
                    output.add(opStack.pop());
                }
                opStack.pop();
            }

            //Handles digits
            else
                output.add(token);

        }

        while(!opStack.empty()){
            output.add(opStack.pop());
        }

        return output;
    }

    //Helper methods for evaluating

    /**
     * Evaluates whether or not given String is an operator
     * @param s
     *      String to be evaluated
     * @return
     *      True if String is operator, false otherwise
     */
    public boolean isOperator(String s){
        if(s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/"))
            return true;
        return false;
    }

    /**
     * Finds the sum, difference, product, or quotient of the given doubles based on the op parameter
     * @param d1
     *      the first double
     * @param d2
     *      the second double
     * @param op
     *      the operator
     * <dt><b>Preconditions</b></dt>
     *      If the operator is "/", d2 is not zero
     *      The op parameter must be either "+", "-", "*", or "/"
     * @return
     *      The calculated solution based on the op parameter
     * @throws ArithmeticException
     *      Thrown if the operator is not one of those supported, or in the case of division by zero
     */
    public double calculate(double d1, double d2, String op) throws ArithmeticException{
        if(op.equals("+"))
            return d1 + d2;
        else if(op.equals("-"))
            return d1 - d2;
        else if(op.equals("*"))
            return d1 * d2;
        else if(op.equals("/")){
            if(d2 == 0)
                throw new ArithmeticException("Error: division by zero");
            else
                return d1 / d2;
        }
        else
            throw new ArithmeticException("Error: invalid operator");
    }
}