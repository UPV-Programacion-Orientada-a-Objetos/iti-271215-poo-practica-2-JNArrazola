package edu.upvictoria.poo;

import java.util.ArrayList;

/**
 * Fundamental class to do the evaluation
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
            } else if(workedArgBrk[i].toUpperCase().startsWith("FLOOR")){
                String floor = workedArgBrk[i];

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

        return Double.toString(Math.round(Double.parseDouble(numberToEvaluate)));
    }

    public static String FLOOR(String arg, ArrayList<Header> headers, String[] lineBreak){
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia FLOOR");

        String numberToEvaluate = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        numberToEvaluate = eval(numberToEvaluate, headers, lineBreak);
        
        return Double.toString(Math.floor(Double.parseDouble(numberToEvaluate)));
    }

}
