package edu.upvictoria.poo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class EvaluateExpression {
    /**
     * This method is used to convert an infix expression to a postfix expression
     * Also called Reverse Polish Notation, it is a mathematical notation in which every operator follows all of its operands
     * @param expression
     * @return
      */
    private static List<String> infixToPostfix(String expression) {
        List<String> output = new ArrayList<>();
        Stack<Character> operators = new Stack<>();
        StringBuilder numberBuffer = new StringBuilder();

        // we are going to iterate over the expression
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
        
            if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c);
            } else {
                if (numberBuffer.length() > 0) {
                    output.add(numberBuffer.toString());
                    numberBuffer = new StringBuilder();
                }
                if (c == '(') {
                    operators.push(c);
                } else if (c == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        output.add(Character.toString(operators.pop()));
                    }
                    operators.pop();
                } else if (c == '/' && i + 1 < expression.length() && expression.charAt(i + 1) == '/') {
                    while (!operators.isEmpty() && precedence('#') <= precedence(operators.peek())) {
                        output.add(Character.toString(operators.pop()));
                    }
                    operators.push('#'); 
                    i++; 
                } else if (isOperator(c)) {
                    while (!operators.isEmpty() && precedence(c) <= precedence(operators.peek())) {
                        output.add(Character.toString(operators.pop()));
                    }
                    operators.push(c);
                }
            }
        }

        if (numberBuffer.length() > 0) {
            output.add(numberBuffer.toString());
        }

        while (!operators.isEmpty()) {
            output.add(Character.toString(operators.pop()));
        }

        return output;
    }

    /**
     * This method is used to determine if a character is an operator
     * @param c
     * @return
      */
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '#';  
    }
    
    /**
     * This method is used to determine the precedence of the operators
     * +- have a lower precedence than / or #(Integer division)
     * @param op
     * @return
      */
    private static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1; // lower precedence
            case '*':
            case '/':
            case '%':
            case '#': // I use this operator to represent integer division
                return 2; // higher precedence
        }
        return -1; // invalid operator
    }

    private static double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                double operand2 = stack.pop();
                double operand1 = stack.pop();
                switch (token.charAt(0)) {
                    case '+':
                        stack.push(operand1 + operand2);
                        break;
                    case '-':
                        stack.push(operand1 - operand2);
                        break;
                    case '*':
                        stack.push(operand1 * operand2);
                        break;
                    case '/':
                        if (operand2 == 0) throw new RuntimeException("No se puyede dividir entre 0");
                        stack.push(operand1 / operand2);
                        break;
                    case '%':
                        stack.push(operand1 % operand2);
                        break; // Modulo
                    case '#':  
                        if (operand2 == 0) throw new RuntimeException("No se puede dividir entre 0");
                        stack.push((double) ((int) operand1 / (int) operand2)); // division entera
                        break;
                }
            }
        }
    
        return stack.pop();
    }

    /**
     * This method is used to determine if a token is a number
     * @param token
     * @return
      */
    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * This method is used to evaluate an arithmetic expression
     * @param expression
      */
    public static Double evaluateExpression(String expression) {
        List<String> postfix = infixToPostfix(expression);
        return evaluatePostfix(postfix);
    }
}
