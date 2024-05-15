package edu.upvictoria.poo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to manage aggregation functions from the select query
  */
public class AggregationFunctions {
    private static final ArrayList<String> aggregateFunctions = Utilities.getVectorOfAggregateFunctions();

    public static String manageAggregationFunctions(String query) throws Exception {
        String[] queryBrk = query.split(" ");

        // Here i store what the user wants
        String arguments = "";
        
        int index = 0;
        for (int i = 1; i < queryBrk.length; i++) {
            if(queryBrk[i].equalsIgnoreCase("FROM")){
                index = i;
                break;
            } 
            arguments += queryBrk[i] + " ";
        }

        String tableName = "";
        try {
            tableName = queryBrk[++index];
        } catch (Exception e) {
            throw new IllegalArgumentException("No se encontró el nombre de la tabla");
        }

        // here i store the tableName
        // System.out.println(tableName);

        if(!FileManagement.searchForTable(tableName))
            throw new FileNotFoundException("Tabla no encontrada");

        // Here i store the arguments in raw
        String[] argumentsBreak = arguments.split(",");
        for (int i = 0; i < argumentsBreak.length; i++) 
            argumentsBreak[i] = argumentsBreak[i].trim();
        
        // for(String arg : argumentsBreak)
        //     System.out.println(arg);

        // I verify that if the user uses an aggregation function, all the arguments must be aggregation functions too
        for(String arg : argumentsBreak){
            String function = "";

            // TODO: Here i will have to manage distinct

            for(String aggregateFunction : aggregateFunctions){
                if(arg.toUpperCase().contains(aggregateFunction))
                    if(function.equals(""))
                        function = aggregateFunction;
                    else
                        throw new Exception("No se puede tener más de una función de agregación en la sentencia");
            }

            if(function.equals(""))
                throw new Exception("Si se usa una función de agregación, todas deben ser funciones de agregación");
        }

        // All this block is to manage where clause
        String conditionals = "";
        boolean isUpper = false;
        if(query.contains("WHERE"))
            isUpper = true;
        else if(query.contains("where"))
            isUpper = false;

        if(query.toUpperCase().contains("WHERE")){
            String[] queryParts = query.split(isUpper ? "WHERE" : "where");
            System.out.println(queryParts[0]);
            conditionals = queryParts[1].trim();
        }

        // Here i get the table with the where clase if it contains, if not i just get the entire table
        ArrayList<String> resultTable = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader(FileManagement.getDatabasePath() + tableName + ".csv"))) {
            String line;
            resultTable.add(line = br.readLine());
            while ((line = br.readLine()) != null) {
                if (Where.manageWhere(conditionals, line, tableName))
                    resultTable.add(line);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (IOException e) {
            throw new IOException("No se pudo abrir el archivo");
        }


        // Here i identify the function, and send the arguments to the function to evaluate
        String output = "";
        for(String arg : argumentsBreak){
            String function = determineFunction(arg);
            
            if((arg.contains("COUNT")&&arg.contains("DISTINCT"))||(arg.contains("count")&&arg.contains("distinct")))
                function = "COUNT DISTINCT";
            // Contains the function in uppercase letters
            // System.out.println(function);

            // Contains the argument in the original form
            // System.out.println(arg);

            switch (function) {
                case "COUNT":
                    output += count(resultTable, arg, tableName) + " ";
                    break;
                case "SUM":
                    output += sum(resultTable, arg, tableName) + " ";
                    break;
                case "AVG":
                    output += avg(resultTable, arg, tableName) + " ";
                    break;
                case "MIN":
                    output += min(resultTable, arg, tableName) + " ";
                    break;
                case "MAX":
                    output += max(resultTable, arg, tableName) + " ";
                    break;
                case "COUNT DISTINCT":
                    System.out.println("COUNT DISTINCT");
                    break;
                default:
                    break;
            }
        }
        System.out.println(output);

        return "";
    }

    private static String determineFunction(String arg) throws Exception {
        String function = "";
        for(String aggregateFunction : aggregateFunctions)
            if(arg.toUpperCase().contains(aggregateFunction))
                if(function.equals(""))
                    function = aggregateFunction;
                else
                    throw new Exception("No se puede tener más de una función de agregación en la sentencia");
        return function;
    }

    public static int count(ArrayList<String> resultTable, String arg, String tableName) throws Exception {
        String header = Utilities.getHeaderOfTable(tableName);
        int counter = 0;

        if(!arg.contains("(")||!arg.contains(")"))
            throw new Exception("Faltaron argumentos en la función COUNT");

        String column = arg.substring(arg.indexOf("(") + 1, arg.indexOf(")"));

        if(column.equals("*"))
            return resultTable.size() - 1;
        
        String[] headerBreak = header.split(",");

        int index = -1;
        for (int i = 0; i < headerBreak.length; i++) 
            if(headerBreak[i].equals(column)){
                index = i;
                break;
            }
        
        if(index == -1)
            throw new Exception("No se encontró la columna");
        
        for (int i = 1; i < resultTable.size(); i++) {
            String[] row = resultTable.get(i).split(",");
            if(row[index].equals("")||row[index].equals("null"))
                continue;
            counter++;
        }
        return counter;
    }

    private static double sum(ArrayList<String> resultTable, String arg, String tableName) throws Exception {
        double sum = 0;
        
        String header = Utilities.getHeaderOfTable(tableName);
        String[] headerBreak = header.split(",");

        if(!arg.contains("(")||!arg.contains(")"))
            throw new Exception("Faltaron argumentos en la función SUM");

        String column = arg.substring(arg.indexOf("(") + 1, arg.indexOf(")"));

        if(column.equals("*"))
            throw new Exception("No se puede sumar todos los elementos de la tabla");
        
        int index = -1;
        for (int i = 0; i < headerBreak.length; i++) 
            if(headerBreak[i].equals(column)){
                index = i;
                break;
            }

        if(index == -1)
            throw new Exception("No se encontró la columna");

        try {
            for (int i = 1; i < resultTable.size(); i++) {
                String[] row = resultTable.get(i).split(",");
                if(row[index].equals("")||row[index].equals("null"))
                    continue;
                sum += Double.parseDouble(row[index]);
            }
        } catch (Exception e) {
            throw new Exception("No se puede sumar una columna que no sea numérica");
        }
        return sum;
    }

    private static double avg(ArrayList<String> resultTable, String arg, String tableName) throws Exception {
        double sum = 0;
        int counter = 0;
        
        String header = Utilities.getHeaderOfTable(tableName);
        String[] headerBreak = header.split(",");

        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Faltaron argumentos en la función AVG");

        String column = arg.substring(arg.indexOf("(") + 1, arg.indexOf(")"));

        if(column.equals("*"))
            throw new IllegalArgumentException("No se puede sacar el promedio de todos los elementos de la tabla");
        
        int index = -1;
        for (int i = 0; i < headerBreak.length; i++) 
            if(headerBreak[i].equals(column)){
                index = i;
                break;
            }

        if(index == -1)
            throw new IllegalArgumentException("No se encontró la columna");

        try {
            for (int i = 1; i < resultTable.size(); i++) {
                String[] row = resultTable.get(i).split(",");
                if(row[index].equals("")||row[index].equals("null"))
                    continue;
                sum += Double.parseDouble(row[index]);
                counter++;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("No se puede sumar una columna que no sea numérica");
        }

        return (counter != 0) ? (sum/counter) : 0;
    }

    public static String min(ArrayList<String> resultTable, String arg, String tableName) throws Exception{
        String min = "ZZZZZZZZZZZZZZZZZZZZ";

        String header = Utilities.getHeaderOfTable(tableName);
        String[] headerBreak = header.split(",");

        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Faltaron argumentos en la función MIN");

        String column = arg.substring(arg.indexOf("(") + 1, arg.indexOf(")"));

        if(column.equals("*"))
            throw new IllegalArgumentException("No se puede sacar el mínimo de todos los elementos de la tabla");
        
        int index = -1;
        for (int i = 0; i < headerBreak.length; i++) 
            if(headerBreak[i].equals(column)){
                index = i;
                break;
            }
        
        if(index == -1)
            throw new IllegalArgumentException("No se encontró la columna");
        
        for (int i = 1; i < resultTable.size(); i++) {
            String[] row = resultTable.get(i).split(",");
            if(row[index].equals("")||row[index].equals("null"))
                continue;
            if(row[index].compareTo(min) < 0)
                min = row[index];
        }

        if(min.equals("ZZZZZZZZZZZZZZZZZZZZ"))
            throw new IllegalArgumentException("No se encontró un valor mínimo");

        return min;
    }

    public static String max(ArrayList<String> resultTable, String arg, String tableName) throws Exception{
        String max = "AAAAAAAAAAAAAAAAAAAA";

        String header = Utilities.getHeaderOfTable(tableName);
        String[] headerBreak = header.split(",");

        if(!arg.contains("(")||!arg.contains(")"))
            throw new IllegalArgumentException("Faltaron argumentos en la función MAX");

        String column = arg.substring(arg.indexOf("(") + 1, arg.indexOf(")"));

        if(column.equals("*"))
            throw new IllegalArgumentException("No se puede sacar el máximo de todos los elementos de la tabla");
        
        int index = -1;
        for (int i = 0; i < headerBreak.length; i++) 
            if(headerBreak[i].equals(column)){
                index = i;
                break;
            }
        
        if(index == -1)
            throw new IllegalArgumentException("No se encontró la columna");
        
        for (int i = 1; i < resultTable.size(); i++) {
            String[] row = resultTable.get(i).split(",");
            if(row[index].equals("")||row[index].equals("null"))
                continue;
            if(row[index].compareTo(max) > 0)
                max = row[index];
        }

        if(max.equals("AAAAAAAAAAAAAAAAAAAA"))
            throw new IllegalArgumentException("No se encontró un valor máximo");

        return max;
    }
}
