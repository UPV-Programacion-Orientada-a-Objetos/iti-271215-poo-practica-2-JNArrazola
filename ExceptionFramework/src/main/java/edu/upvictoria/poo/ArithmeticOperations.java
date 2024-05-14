package edu.upvictoria.poo;

import java.util.ArrayList;

/**
 * Class to manage arithmetic operations from the select query
 * */
public class ArithmeticOperations {
    public static void manageArithmetic(String query) throws Exception {
        query = query.replace("DIV", "#");
        query = query.replace("div", "#");
        query = query.replace("MOD", "%");
        query = query.replace("mod", "%");
        
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
        for(int i = 0; i < numericFunctions.size(); i++){
            if(queryWithoutSelect.toUpperCase().contains(numericFunctions.get(i))){
                if(function.equals(""))
                    function = numericFunctions.get(i);
                else
                    throw new Exception("No se puede tener más de una función numérica en la sentencia");
            }   
        }

        String expressionToEvaluate = "";
        if(!function.equals(""))
            // If it doesnt contain () that means they are no arguments so it is invalid
            if(queryWithoutSelect.contains("(") && queryWithoutSelect.contains(")"))
                expressionToEvaluate = queryWithoutSelect.substring(
                    queryWithoutSelect.indexOf("(") + 1, queryWithoutSelect.indexOf(")"));
            else
                throw new Exception("Error en la sentencia");
        else 
            expressionToEvaluate = queryWithoutSelect;

        // See if they are unkwown functions
        if(function.equals(""))
            for(int i = 0; i < expressionToEvaluate.length(); i++)
                if(!Utilities.isValidInEquation(expressionToEvaluate.charAt(i))){
                    System.out.println(expressionToEvaluate.charAt(i));
                    throw new Exception("Error en los operadores aritméticos");
                }
        
        // Evaluate the result
        double result;
        try{
            result = EvaluateExpression.evaluateExpression(expressionToEvaluate);
        } catch (Exception e){
            throw new Exception("Error en los operadores aritméticos");
        }

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
            }
        }

        if(!alias.equals(""))
            System.out.println(alias);
        else
            System.out.println(queryWithoutSelect);
        System.out.println(result);
    }
}
