package edu.upvictoria.poo;

import java.util.ArrayList;

/**
 * Class that represents a column entered in the select
 * This because some columns can have aggregate functions or some other functions
 * attached to them, so an object is a better way of handling this
  */
public class DataColumn {
    private String column;
    private String function;
    private String alias = "";
    private int index = -1;

    public DataColumn(String columnOnSelect, String header){
        String columnOnSelectParts[] = columnOnSelect.split(" ");
        
        // Handle alias
        for (int i = 0; i < columnOnSelectParts.length; i++) 
            columnOnSelectParts[i] = columnOnSelectParts[i].trim();

        try {
            if(columnOnSelectParts[1].equalsIgnoreCase("as")){
                column = columnOnSelectParts[0];

                for(int i = 2; i < columnOnSelectParts.length; i++)
                    alias += columnOnSelectParts[i] + " ";
                
                alias = alias.trim();

                if(alias.isEmpty())
                    throw new Exception("No se encontró alias en la columna");
                else if(!alias.startsWith("'")|| !alias.endsWith("'"))
                    throw new Exception("Alias debe ir entre comillas simples: " + alias);
            } else 
                throw new Exception("No se encontró alias en la columna");
        } catch (Exception e) {
            column = columnOnSelectParts[0];
            alias = columnOnSelectParts[0];
        }
        // Finished handling aliases
        
        ArrayList<String> functions = Utilities.getVectorOfColumnFunctions();
        String fn = "";

        // Handle functions
        for (String function : functions) 
            if(column.toUpperCase().contains(function))
                if(fn.isEmpty())
                    fn = function;
                else 
                    throw new IllegalArgumentException("No se pueden tener dos funciones en una columna");
        function = fn;
        
        if(!function.isEmpty())
            if(!column.contains("(") || !column.contains(")"))
                throw new IllegalArgumentException("Faltan paréntesis a la función: " + function);
            else 
                column = column.substring(column.indexOf("(") + 1, column.indexOf(")"));
        // Finished handling functions

        // Handle index
        String headerParts[] = header.split(",");
        for (int i = 0; i < headerParts.length; i++) 
            headerParts[i] = headerParts[i].trim();
        
        for (int i = 0; i < headerParts.length; i++)
            if(headerParts[i].equalsIgnoreCase(column)){
                index = i;
                break;
            }
        
        if(index == -1)
            throw new IllegalArgumentException("No se encontró la columna: " + column);
    }

    public String getAlias() {
        return alias;
    }

    public String getColumn() {
        return column;
    }

    public String getFunction() {
        return function;
    }
    public int getIndex() {
        return index;
    }
}
