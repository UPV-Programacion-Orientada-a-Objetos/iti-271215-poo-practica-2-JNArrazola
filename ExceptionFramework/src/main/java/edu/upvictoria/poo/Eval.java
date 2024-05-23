package edu.upvictoria.poo;

import java.util.ArrayList;

/**
 * Fundamental class to do the evaluation
 * This class is crazy, i dont even know how i figured this out and made it work without using a tree lol
 * 
 * @author Joshua Arrazola
  */
public class Eval {
    
    // query = query.replace("DIV", "#");
    // query = query.replace("div", "#");
    // query = query.replace("MOD", "%");
    // query = query.replace("mod", "%");

    /**
     * Function to evaluate the query
     * @param query
     */
    public static String eval(String arg, ArrayList<Header> headers, String[] lineBreak){
        String workedArg = "";

        arg = arg.trim();

        for (int i = 0; i < arg.length(); i++) {
            if(arg.charAt(i) == ' ') continue;
            
            if(isSign(arg.charAt(i))){
                workedArg+=" ";
                workedArg+=arg.charAt(i);
                workedArg+=" ";
            } else 
                workedArg+=arg.charAt(i);
        }

        String expressionToEvaluate = "";
        String[] workedArgBrk = workedArg.split(" ");

        for (int i = 0; i < workedArgBrk.length; i++) {
            // ? ROUND
            if(workedArgBrk[i].toUpperCase().startsWith("ROUND")){
                String round = workedArgBrk[i];

                if(hasValidParenthesis(round)){
                    expressionToEvaluate+=ROUND(round, headers, lineBreak);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(round)){
                            round+=workedArgBrk[j];

                            if(hasValidParenthesis(round)){
                                expressionToEvaluate+=ROUND(round, headers, lineBreak);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("FLOOR")){
                String floor = workedArgBrk[i];

                // ! Si de una es válido entonces a evaluar, si no entonces a seguir con la recursiva
                if(hasValidParenthesis(floor)){
                    expressionToEvaluate+=FLOOR(floor, headers, lineBreak);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(floor)){
                            floor+=workedArgBrk[j];
    
                            if(hasValidParenthesis(floor)){
                                expressionToEvaluate+=FLOOR(floor, headers, lineBreak);
                                i = j;
                                break;
                            }
                        } 
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("CEIL")){
                String ceil = workedArgBrk[i];

                if(hasValidParenthesis(ceil)){
                    expressionToEvaluate+=CEIL(ceil, headers, lineBreak);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(ceil)){
                            ceil+=workedArgBrk[j];

                            if(hasValidParenthesis(ceil)){
                                expressionToEvaluate+=CEIL(ceil, headers, lineBreak);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("UCASE")){
                String ucase = workedArgBrk[i];

                if(hasValidParenthesis(ucase)){
                    expressionToEvaluate+=UCASE(ucase, headers, lineBreak);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(ucase)){
                            ucase+=workedArgBrk[j];

                            if(hasValidParenthesis(ucase)){
                                expressionToEvaluate+=UCASE(ucase, headers, lineBreak);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("LCASE")){
                String lcase = workedArgBrk[i];

                if(hasValidParenthesis(lcase)){
                    expressionToEvaluate+=LCASE(lcase, headers, lineBreak);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(lcase)){
                            lcase+=workedArgBrk[j];

                            if(hasValidParenthesis(lcase)){
                                expressionToEvaluate+=LCASE(lcase, headers, lineBreak);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else
                expressionToEvaluate+=workedArgBrk[i];
        }

        for (int k = 0; k < headers.size(); k++) 
            expressionToEvaluate = expressionToEvaluate.replace(headers.get(k).getName(), lineBreak[headers.get(k).getIndex()]);

        return EvaluateExpression.evaluateExpression(expressionToEvaluate);
    }


    public static boolean hasValidParenthesis(String arg){
        int ctr = 0;

        for (int i = 0; i < arg.length(); i++) {
            if(arg.charAt(i) == '(') ctr++;
            if(arg.charAt(i) == ')') ctr--;

            if(ctr < 0) return false;
        }

        return ctr == 0;
    }

    public static boolean isSign(char c){
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '#';
    }


    // -----------------  FUNCTIONS  -----------------

    public static String ROUND(String arg, ArrayList<Header> headers, String[] lineBreak){
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia ROUND");

        String numberToEvaluate = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        numberToEvaluate = eval(numberToEvaluate, headers, lineBreak);

        try {
            Double.parseDouble(numberToEvaluate);
        } catch (Exception e) {
            return "null";
        }

        return Double.toString(Math.round(Double.parseDouble(numberToEvaluate)));
    }

    public static String FLOOR(String arg, ArrayList<Header> headers, String[] lineBreak){
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia FLOOR");

        String numberToEvaluate = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        numberToEvaluate = eval(numberToEvaluate, headers, lineBreak);
        
        return Double.toString(Math.floor(Double.parseDouble(numberToEvaluate)));
    }

    public static String CEIL(String arg, ArrayList<Header> headers, String[] lineBreak){
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia CEIL");

        String numberToEvaluate = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        numberToEvaluate = eval(numberToEvaluate, headers, lineBreak);
        
        try {
            Double.parseDouble(numberToEvaluate);
        } catch (Exception e) {
            return "null";
        }

        return Double.toString(Math.ceil(Double.parseDouble(numberToEvaluate)));
    }

    // -----------------  STRING FUNCTIONS  -----------------

    public static String UCASE(String arg, ArrayList<Header> headers, String[] lineBreak){
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia UCASE");

        String column = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        for (int i = 0; i < headers.size(); i++) 
            if(headers.get(i).getName().equals(column))
                return lineBreak[headers.get(i).getIndex()].toUpperCase();

        throw new IllegalArgumentException("No se encontró la columna " + column);
    }

    public static String LCASE(String arg, ArrayList<Header> headers, String[] lineBreak){
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia LCASE");

        String column = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        for (int i = 0; i < headers.size(); i++) 
            if(headers.get(i).getName().equals(column))
                return lineBreak[headers.get(i).getIndex()].toLowerCase();

        throw new IllegalArgumentException("No se encontró la columna " + column);
    }
}
