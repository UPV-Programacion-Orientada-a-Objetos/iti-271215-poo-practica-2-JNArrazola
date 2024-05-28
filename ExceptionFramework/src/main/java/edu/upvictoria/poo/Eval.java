package edu.upvictoria.poo;

import java.util.ArrayList;
import java.util.Random;

/**
 * Fundamental class to do the evaluation
 * This class is crazy, i dont even know how i figured this out and made it work without using a tree lol
 * 
 * @author Joshua Arrazola
  */
public class Eval {
    private static final Random random = new Random();
    // query = query.replace("DIV", "#");
    // query = query.replace("div", "#");
    // query = query.replace("MOD", "%");
    // query = query.replace("mod", "%");

    /**
     * Function to evaluate the query
     * @param query
     */
    public static String eval(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table) throws Exception {
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
                    expressionToEvaluate+=ROUND(round, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(round)){
                            round+=workedArgBrk[j];

                            if(hasValidParenthesis(round)){
                                expressionToEvaluate+=ROUND(round, headers, lineBreak, table);
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
                    expressionToEvaluate+=FLOOR(floor, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(floor)){
                            floor+=workedArgBrk[j];
    
                            if(hasValidParenthesis(floor)){
                                expressionToEvaluate+=FLOOR(floor, headers, lineBreak, table);
                                i = j;
                                break;
                            }
                        } 
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("CEIL")){
                String ceil = workedArgBrk[i];

                if(hasValidParenthesis(ceil)){
                    expressionToEvaluate+=CEIL(ceil, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(ceil)){
                            ceil+=workedArgBrk[j];

                            if(hasValidParenthesis(ceil)){
                                expressionToEvaluate+=CEIL(ceil, headers, lineBreak, table);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("UCASE")){
                String ucase = workedArgBrk[i];

                if(hasValidParenthesis(ucase)){
                    expressionToEvaluate+=UCASE(ucase, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(ucase)){
                            ucase+=workedArgBrk[j];

                            if(hasValidParenthesis(ucase)){
                                expressionToEvaluate+=UCASE(ucase, headers, lineBreak, table);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("LCASE")){
                String lcase = workedArgBrk[i];

                if(hasValidParenthesis(lcase)){
                    expressionToEvaluate+=LCASE(lcase, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(lcase)){
                            lcase+=workedArgBrk[j];

                            if(hasValidParenthesis(lcase)){
                                expressionToEvaluate+=LCASE(lcase, headers, lineBreak, table);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("CAPITALIZE")){
                String capitalize = workedArgBrk[i];

                if(hasValidParenthesis(capitalize)){
                    expressionToEvaluate+=CAPITALIZE(capitalize, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(capitalize)){
                            capitalize+=workedArgBrk[j];

                            if(hasValidParenthesis(capitalize)){
                                expressionToEvaluate+=CAPITALIZE(capitalize, headers, lineBreak, table);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("RAND")){
                String rand = workedArgBrk[i];

                if(hasValidParenthesis(rand)){
                    expressionToEvaluate+=RAND(rand, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(rand)){
                            rand+=workedArgBrk[j];

                            if(hasValidParenthesis(rand)){
                                expressionToEvaluate+=RAND(rand, headers, lineBreak, table);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("COUNT")){
                String count = workedArgBrk[i];

                if(hasValidParenthesis(count)){
                    expressionToEvaluate+=COUNT(count, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(count)){
                            count+=workedArgBrk[j];

                            if(hasValidParenthesis(count)){
                                expressionToEvaluate+=COUNT(count, headers, lineBreak, table);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            }  else if(workedArgBrk[i].toUpperCase().startsWith("AVG")){
                String avg = workedArgBrk[i];

                if(hasValidParenthesis(avg)){
                    expressionToEvaluate+=AVG(avg, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(avg)){
                            avg+=workedArgBrk[j];

                            if(hasValidParenthesis(avg)){
                                expressionToEvaluate+=AVG(avg, headers, lineBreak, table);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("MIN")){
                String min = workedArgBrk[i];

                if(hasValidParenthesis(min)){
                    expressionToEvaluate+=MIN(min, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(min)){
                            min+=workedArgBrk[j];

                            if(hasValidParenthesis(min)){
                                expressionToEvaluate+=MIN(min, headers, lineBreak, table);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("MAX")){
                String max = workedArgBrk[i];

                if(hasValidParenthesis(max)){
                    expressionToEvaluate+=MAX(max, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(max)){
                            max+=workedArgBrk[j];

                            if(hasValidParenthesis(max)){
                                expressionToEvaluate+=MAX(max, headers, lineBreak, table);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else if(workedArgBrk[i].toUpperCase().startsWith("SUM")){
                String sum = workedArgBrk[i];

                if(hasValidParenthesis(sum)){
                    expressionToEvaluate+=SUM(sum, headers, lineBreak, table);
                } else {
                    for (int j = i + 1; j < workedArgBrk.length; j++) {
                        if(!hasValidParenthesis(sum)){
                            sum+=workedArgBrk[j];

                            if(hasValidParenthesis(sum)){
                                expressionToEvaluate+=SUM(sum, headers, lineBreak, table);
                                i = j;
                                break;
                            }
                        }
                    }
                }
            } else {
                expressionToEvaluate += workedArgBrk[i];
            }
        }

        for (int k = 0; k < headers.size(); k++) 
            expressionToEvaluate = expressionToEvaluate.replace(headers.get(k).getName(), lineBreak[headers.get(k).getIndex()]);

        if(!verifySentence(expressionToEvaluate))
            throw new IllegalArgumentException("Error en la sentencia: " + expressionToEvaluate);

        return EvaluateExpression.evaluateExpression(expressionToEvaluate);
    }


    // -----------------  UTILS  -----------------

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

    public static boolean isSign(String c){
        return c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") || c.equals("%") || c.equals("#");
    }

    public static boolean verifySentence(String arg){
        String workedString = "";

        for (int i = 0; i < arg.length(); i++) {
            if(arg.charAt(i) == '(' || arg.charAt(i) == ')')
                continue;

            if(isSign(arg.charAt(i))) {
                workedString+=" ";
                workedString+=arg.charAt(i);
                workedString+=" ";
                continue;
            } 
            
            workedString+=arg.charAt(i);
        }

        String[] workedStringBreak = workedString.split(" ");

        for(int i = 0; i < workedStringBreak.length; i++){
            if(isSign(workedStringBreak[i]))
                continue;
            
            if(workedStringBreak[i].startsWith("'") && workedStringBreak[i].endsWith("'"))
                continue;
            
            if(workedStringBreak[i].equalsIgnoreCase("NULL"))
                continue;

            try {
                Double.parseDouble(workedStringBreak[i]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Error en la evaluación: No se reconoció '" + workedStringBreak[i]+"'");
            }
        }
        
        return true;
    }

    // -----------------  FUNCTIONS  -----------------

    public static String ROUND(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table) throws Exception{
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia ROUND");

        String numberToEvaluate = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        numberToEvaluate = eval(numberToEvaluate, headers, lineBreak, table);

        if(numberToEvaluate.isEmpty()||numberToEvaluate.equalsIgnoreCase("null"))
            return "null";

        try {
            Double.parseDouble(numberToEvaluate);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error en la sentencia ROUND: " + numberToEvaluate + " no es un número");
        }

        return Double.toString(Math.round(Double.parseDouble(numberToEvaluate)));
    }

    public static String FLOOR(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table) throws Exception{
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia FLOOR");

        String numberToEvaluate = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        numberToEvaluate = eval(numberToEvaluate, headers, lineBreak, table);
        
        if(numberToEvaluate.isEmpty()||numberToEvaluate.equalsIgnoreCase("null"))
            return "null";

        return Double.toString(Math.floor(Double.parseDouble(numberToEvaluate)));
    }

    public static String CEIL(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table) throws Exception{
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia CEIL");

        String numberToEvaluate = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        numberToEvaluate = eval(numberToEvaluate, headers, lineBreak, table);
        
        if(numberToEvaluate.isEmpty()||numberToEvaluate.equalsIgnoreCase("null"))
            return "null";

        try {
            Double.parseDouble(numberToEvaluate);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error en la sentencia CEIL: " + numberToEvaluate + " no es un número");
        }

        return Double.toString(Math.ceil(Double.parseDouble(numberToEvaluate)));
    }

    public static String RAND(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table) throws Exception{
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia RAND");

        String numberToEvaluate = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);

        if(numberToEvaluate.isEmpty())
            return Double.toString((int)random.nextDouble(0, 10000));

        numberToEvaluate = eval(numberToEvaluate, headers, lineBreak, table);

        try {
            Double.parseDouble(numberToEvaluate);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error en la sentencia RAND: " + numberToEvaluate + " no es un número");
        }

        if(Double.parseDouble(numberToEvaluate) < 0)
            throw new IllegalArgumentException("Error en la sentencia RAND: " + numberToEvaluate + " no puede ser negativo");
        
        int upperLimit = (int) Double.parseDouble(numberToEvaluate);

        if(upperLimit <= 0)
            upperLimit = 1;
        
        return Double.toString((int)random.nextDouble(0, upperLimit));
    }

    // -----------------  STRING FUNCTIONS  -----------------

    public static String UCASE(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table){
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia UCASE");

        String column = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        if(column.isEmpty())
            throw new IllegalArgumentException("Error en la función UCASE: No se especificó la columna");

        if(column.startsWith("'")&&column.endsWith("'"))
            return column.toUpperCase();

        for (int i = 0; i < headers.size(); i++) 
            if(headers.get(i).getName().equals(column))
                return lineBreak[headers.get(i).getIndex()].toUpperCase();

        throw new IllegalArgumentException("No se encontró la columna " + column);
    }

    public static String LCASE(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table){
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia LCASE");

        String column = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        if(column.isEmpty())
            throw new IllegalArgumentException("Error en la función LCASE: No se especificó la columna");

        if(column.startsWith("'")&&column.endsWith("'"))
            return column.toLowerCase();

        for (int i = 0; i < headers.size(); i++) 
            if(headers.get(i).getName().equals(column))
                return lineBreak[headers.get(i).getIndex()].toLowerCase();
            
            

        throw new IllegalArgumentException("No se encontró la columna " + column);
    }

    public static String CAPITALIZE(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table){
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia CAPITALIZE");

        String column = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        if(column.isEmpty())
            throw new IllegalArgumentException("Error en la función CAPITALIZE: No se especificó la columna");

        if(column.startsWith("'")&&column.endsWith("'")){
            String value = column;
            for(int i = 0; i < value.length(); i++)
            if(Character.isLetter(value.charAt(i))){
                value = value.substring(0, i) + Character.toUpperCase(value.charAt(i)) + value.substring(i + 1);
                break;
            }
            return value;
        }


        String value = "";
        for (int i = 0; i < headers.size(); i++) 
            if(headers.get(i).getName().equals(column))
                value = lineBreak[headers.get(i).getIndex()];
        
        if(value.isEmpty())
            throw new IllegalArgumentException("No se encontró la columna " + column);

        for(int i = 0; i < value.length(); i++)
            if(Character.isLetter(value.charAt(i))){
                value = value.substring(0, i) + Character.toUpperCase(value.charAt(i)) + value.substring(i + 1);
                break;
            }

        return value;
    }

    // -----------------  Group Functions  -----------------
    public static String COUNT(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table){
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia COUNT");

        String column = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        if(column.isEmpty())
            throw new IllegalArgumentException("Error en la función COUNT: No se especificó la columna");

        if(column.equals("*"))
            return Integer.toString(table.size()); 

        int indexOfColumn = -1;
        for (int i = 0; i < headers.size(); i++) 
            if(headers.get(i).getName().equals(column))
                indexOfColumn = headers.get(i).getIndex();
        
        if(indexOfColumn == -1)
            throw new IllegalArgumentException("Error en la función COUNT: No se encontró la columna " + column);
        
        int count = 0;
        for (int i = 1; i < table.size(); i++) 
            if(!table.get(i).split(",")[indexOfColumn].equals("null"))
                count++;

        return Integer.toString(count);
    }

    public static String AVG(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table){
        if(table.size() == 1)
            return "0";
        
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia AVG");

        String column = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        if(column.isEmpty())
            throw new IllegalArgumentException("Error en la función AVG: No se especificó la columna");

        if(column.equals("*"))
            throw new IllegalArgumentException("Error en la función AVG: No se puede usar *");

        int indexOfColumn = -1;
        for (int i = 0; i < headers.size(); i++) 
            if(headers.get(i).getName().equals(column))
                indexOfColumn = headers.get(i).getIndex();
        
        if(indexOfColumn == -1)
            throw new IllegalArgumentException("Error en la función AVG: No se encontró la columna " + column);
        
        double sum = 0;
        int count = 0;
        for (int i = 1; i < table.size(); i++) 
            if(!table.get(i).split(",")[indexOfColumn].equals("null")){

                try {
                    Double.parseDouble(table.get(i).split(",")[indexOfColumn]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Error en la función AVG: " + table.get(i).split(",")[indexOfColumn] + " no es un número");
                }

                sum+=Double.parseDouble(table.get(i).split(",")[indexOfColumn]);
                count++;
            }

        if(count == 0)
            return "0";

        return Double.toString(sum / count);
    }

    public static String MIN(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table){
        if(table.size() == 1)
            return "0";
        
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia MIN");

        String column = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        if(column.isEmpty())
            throw new IllegalArgumentException("Error en la función MIN: No se especificó la columna");

        if(column.equals("*"))
            throw new IllegalArgumentException("Error en la función MIN: No se puede usar *");

        int indexOfColumn = -1;
        for (int i = 0; i < headers.size(); i++) 
            if(headers.get(i).getName().equals(column))
                indexOfColumn = headers.get(i).getIndex();
        
        if(indexOfColumn == -1)
            throw new IllegalArgumentException("Error en la función MIN: No se encontró la columna " + column);
        
        String min = "null";
        for (int i = 1; i < table.size(); i++) 
            if(!table.get(i).split(",")[indexOfColumn].equals("null"))
                if(min.equals("null") || table.get(i).split(",")[indexOfColumn].compareTo(min) < 0)
                    min = table.get(i).split(",")[indexOfColumn];

        return min;
    } 

    public static String MAX(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table){
        if(table.size() == 1)
            return "0";
        
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia MAX");

        String column = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        if(column.isEmpty())
            throw new IllegalArgumentException("Error en la función MAX: No se especificó la columna");

        if(column.equals("*"))
            throw new IllegalArgumentException("Error en la función MAX: No se puede usar *");

        int indexOfColumn = -1;
        for (int i = 0; i < headers.size(); i++) 
            if(headers.get(i).getName().equals(column))
                indexOfColumn = headers.get(i).getIndex();
        
        if(indexOfColumn == -1)
            throw new IllegalArgumentException("Error en la función MAX: No se encontró la columna " + column);
        
        String max = "null";
        for (int i = 1; i < table.size(); i++) 
            if(!table.get(i).split(",")[indexOfColumn].equals("null"))
                if(max.equals("null") || table.get(i).split(",")[indexOfColumn].compareTo(max) > 0)
                    max = table.get(i).split(",")[indexOfColumn];

        return max;
    }

    public static String SUM(String arg, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table){
        if(table.size() == 1)
            return "0";
        
        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Error en los paréntesis en la sentencia SUM");

        String column = arg.substring(arg.indexOf("(") + 1, arg.length() - 1);
        
        if(column.isEmpty())
            throw new IllegalArgumentException("Error en la función SUM: No se especificó la columna");

        if(column.equals("*"))
            throw new IllegalArgumentException("Error en la función SUM: No se puede usar *");

        int indexOfColumn = -1;
        for (int i = 0; i < headers.size(); i++) 
            if(headers.get(i).getName().equals(column))
                indexOfColumn = headers.get(i).getIndex();
        
        if(indexOfColumn == -1)
            throw new IllegalArgumentException("Error en la función SUM: No se encontró la columna " + column);
        
        double sum = 0;
        for (int i = 1; i < table.size(); i++) 
            if(!table.get(i).split(",")[indexOfColumn].equals("null")){

                try {
                    Double.parseDouble(table.get(i).split(",")[indexOfColumn]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Error en la función SUM: " + table.get(i).split(",")[indexOfColumn] + " no es un número");
                }

                sum+=Double.parseDouble(table.get(i).split(",")[indexOfColumn]);
            }

        return Double.toString(sum);
    }
}
