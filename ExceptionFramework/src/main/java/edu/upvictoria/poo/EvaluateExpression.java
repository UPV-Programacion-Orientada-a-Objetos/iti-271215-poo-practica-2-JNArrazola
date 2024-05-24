package edu.upvictoria.poo;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class EvaluateExpression {
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '#';  
    }
    
    private static boolean isNumber(char c) {
        return Character.isDigit(c) || c == '.';
    }

    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static List<String> infixToPostfix(String expression) throws Exception {
        List<String> output = new ArrayList<>();
        Stack<Character> operators = new Stack<>();
        StringBuilder numberBuffer = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (isNumber(c)) {
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

    private static double evaluatePostfix(List<String> postfix) throws RuntimeException {
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
                        if (operand2 == 0) throw new RuntimeException("No se puede dividir entre 0");
                        stack.push(operand1 / operand2);
                        break;
                    case '%':
                        if (operand2 == 0) throw new RuntimeException("No se puede dividir entre 0");
                        stack.push(operand1 % operand2);
                        break;
                    case '#':
                        if (operand2 == 0) throw new RuntimeException("No se puede dividir entre 0");
                        stack.push((double) ((int) operand1 / (int) operand2));
                        break;
                }
            }
        }

        return stack.pop();
    }

    public static String evaluateExpression(String expression) {
        if(expression.toUpperCase().contains("NULL"))
            return "null";
        
        if(expression.isEmpty())
            throw new RuntimeException("La expresión no puede estar vacía");
        
        // ? If it does not contain any number, then it is not an expression, it is a string, so I am going to return it
        boolean flag = false;
        for (int i = 0; i < expression.length(); i++) {
            if(isNumber(expression.charAt(i))||isOperator(expression.charAt(i))){
                flag = true;
                break;
            }
        }
        if(!flag) return expression;

        StringBuilder invalidSequence = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (isNumber(c) || isOperator(c) || c == '(' || c == ')') {
                if (invalidSequence.length() > 0) {
                    throw new IllegalArgumentException("Secuencia inválida al evaluar: " + invalidSequence.toString());
                }
                invalidSequence.setLength(0); 
            } else {
                invalidSequence.append(c);
            }
        }
    
        if (invalidSequence.length() > 0) 
            throw new IllegalArgumentException("Secuencia inválida: " + invalidSequence.toString());
        

        try {
            List<String> postfix = infixToPostfix(expression);
            return Double.toString(evaluatePostfix(postfix));
        } catch (Exception e) {
            return "null";
        }
    }

    // Método para determinar la precedencia de los operadores
    private static int precedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '%':
            case '#':
                return 2;
            default:
                return -1;
        }
    }
}
