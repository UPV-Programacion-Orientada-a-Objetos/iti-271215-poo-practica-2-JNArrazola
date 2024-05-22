package edu.upvictoria.poo;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class to manage arithmetic operations from the select query
 * */
public class ArithmeticOperations {
    private static final Random random = new Random();

    public static void manageArithmetic(String query) throws Exception {
        String[] queryParts = query.split(" ");
        String queryWithoutSelect = "";

        // Get the query without the select
        for(int i = 1; i < queryParts.length; i++)
            queryWithoutSelect += queryParts[i] + " ";

        // Get the alias
        String alias = "";
        if(queryWithoutSelect.contains("AS")||queryWithoutSelect.contains("as")){
            String[] queryWithoutSelectParts = queryWithoutSelect.toUpperCase().split("AS");
            
            queryWithoutSelect = queryWithoutSelectParts[0];
            alias = queryWithoutSelectParts[1].trim();
        }

        ArrayList<String> numericFunctions = Utilities.getVectorOfNumericFunctions();
        String function = "";
        
        // Get the function to evaluate
        for(int i = 0; i < numericFunctions.size(); i++)
            if(queryWithoutSelect.toUpperCase().contains(numericFunctions.get(i)))
                if(function.equals(""))
                    function = numericFunctions.get(i);
                else
                    throw new Exception("No se puede tener más de una función numérica en la sentencia");

        String expressionToEvaluate = "";
        if(!function.equals("")&&!function.toUpperCase().contains("RAND"))
            // If it doesnt contain () that means they are no arguments so it is invalid
            if(queryWithoutSelect.contains("(") && queryWithoutSelect.contains(")")){
                int index = 0;
                for (int i = queryWithoutSelect.length() - 1; i >= 0; i--) 
                    if(queryWithoutSelect.charAt(i) == ')'){
                        index = i;
                        break;
                    }
                expressionToEvaluate = queryWithoutSelect.substring(
                        queryWithoutSelect.indexOf("(") + 1, index);
            }
            else
                throw new Exception("Error en la sentencia");
        else 
            expressionToEvaluate = queryWithoutSelect;

        // See if they are unkwown functions
        if(function.equals(""))
            for(int i = 0; i < expressionToEvaluate.length(); i++)
                if(!Utilities.isValidInEquation(expressionToEvaluate.charAt(i)))
                    throw new Exception("Error en los operadores aritméticos");
        
        // Evaluate the result
        double result;
        if(!function.equalsIgnoreCase("RAND"))
            try{
                result = EvaluateExpression.evaluateExpression(expressionToEvaluate);
            } catch (Exception e){
                throw new Exception(e.getMessage());
            }
        else 
            result = 0;


        if(!function.equals("")){
            switch(function){
                case "FLOOR":
                case "floor":
                    result = Math.floor(result);
                    break;
                case "ROUND":
                case "round":
                    result = Math.round(result);
                    break;
                case "CEIL":
                case "ceil":
                    result = Math.ceil(result);
                    break;
                case "RAND":
                case "rand":
                    result = RAND(expressionToEvaluate);
                    break;
            }
        }

        if(!alias.equals(""))
            System.out.println(alias);
        else
            System.out.println(queryWithoutSelect);
        System.out.println(result);
    }

    public static double RAND(String arg) throws Exception {
        arg = arg.trim();

        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Faltan paréntesis en: " + arg);
        
        if(arg.indexOf(")")!=arg.length()-1)
            throw new IllegalArgumentException("Error en la sentencia RAND");

        String insideParenthesis = "";
        try {
            insideParenthesis = arg.substring(arg.indexOf("(") + 1, arg.indexOf(")"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Faltan argumentos en la función RAND");
        }

        if(insideParenthesis.isEmpty()){
            return random.nextDouble();
        } else {
            double limit;
            
            try{
                limit = Double.parseDouble(insideParenthesis);
            } catch (Exception e){
                throw new IllegalArgumentException("Argumento inválido dentro de la función RAND");
            }

            if(limit<=0)
                throw new IllegalArgumentException("El argumento de la función RAND debe ser mayor a 0");

            return random.nextDouble(0, limit);
        }
        
    }
}