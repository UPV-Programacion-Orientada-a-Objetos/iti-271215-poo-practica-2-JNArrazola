package edu.upvictoria.poo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Steps to do it
 * - Im gonna divide the query
 * - Im gonna do the where clause
 * - Im gonna take the query, and analyze it, firstly, i will replace the columns with the actual
 * values of the columns, and then, i will start doing recursively the functions 
 * - 
 */
public class Select {
    public static String select(String query) throws Exception {
        query = query.replace("DIV", "#");
        query = query.replace("div", "#");
        query = query.replace("MOD", "%");
        query = query.replace("mod", "%");
        
        if (FileManagement.getDatabasePath() == null)
            throw new FileNotFoundException("No se ha accedido a ninguna base de datos");

        if (!query.toUpperCase().contains("FROM")) {
            ArithmeticOperations.manageArithmetic(query);
            return "Operación aritmética realizada con éxito";
        }

        String[] queryBrk = query.split(" ");

        int index = 0;
        String argsStr = "", tableName = "", conditionals = "";
        for (int i = 1; i < queryBrk.length; i++) {
            if(queryBrk[i].equalsIgnoreCase("FROM")){
                index = i;
                break;
            }
            argsStr+=queryBrk[i];
        }
        
        if(index + 1 >= queryBrk.length)
            throw new IllegalArgumentException("Nombre de la tabla incompleto");
        tableName = queryBrk[++index];

        if(!FileManagement.searchForTable(tableName))
            throw new FileNotFoundException("No se encontró la tabla");

        if(index + 2 < queryBrk.length && queryBrk[index + 1].equalsIgnoreCase("WHERE"))
            for(int i = index + 2; i < queryBrk.length; i++)
                conditionals += queryBrk[i] + " ";
        conditionals = conditionals.trim();
        
        if(query.toUpperCase().contains("WHERE")&&conditionals.isEmpty())
            throw new IllegalArgumentException("WHERE vacío");

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

        // ! All this block is to manage the headers of the table
        String headerOfTable = Utilities.getHeaderOfTable(tableName);
        ArrayList<Header> headers = new ArrayList<>();

        String[] headerParts = headerOfTable.split(",");
        for (int i = 0; i < headerParts.length; i++)
            headers.add(new Header(headerParts[i], i));
        
        //but sort them in the other way
        // i mean, sort them in non increasing order

        // ? Here i have the sorted headers, to replace them in the query
        headers.sort((h1, h2) -> h2.getIndex() - h1.getIndex());

        // ? Here they are the args for the query
        String[] argsBreak = argsStr.split(",");
        for (int i = 0; i < argsBreak.length; i++) 
            argsBreak[i] = argsBreak[i].trim();

        // Firstly i have to replace all the header columns with the actual values

        if(argsBreak[0].equals("*")){  
            if(argsBreak.length!=1)
                throw new IllegalArgumentException("No se puede colocar más de dos columnas en un select *");

            for (int i = 0; i < resultTable.size(); i++) 
                System.out.println(resultTable.get(i));
            
            return "Select realizado con éxito";
        }

        // ! First i iterate through the result table
        for (int i = 1; i < resultTable.size(); i++) {
            String[] lineBrk = resultTable.get(i).split(",");

            // ! Then i iterate through the args
            for (int j = 0; j < argsBreak.length; j++) {
                String arg = argsBreak[j];
                
                System.out.print(Eval.eval(arg, headers, lineBrk, resultTable) + " ");
            }
            System.out.println();
        }

        return "Select realizado con éxito";
    }
}
