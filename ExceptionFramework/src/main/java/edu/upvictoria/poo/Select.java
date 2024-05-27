package edu.upvictoria.poo;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Steps to do it
 * - Im gonna divide the query
 * - Im gonna do the where clause
 * - Im gonna take the query, and analyze it, firstly, i will replace the
 * columns with the actual
 * values of the columns, and then, i will start doing recursively the functions
 * -
 */
public class Select {
    public static String select(String query) throws Exception {
        query = query.replace(" DIV ", "#");
        query = query.replace(" div ", "#");
        query = query.replace(" MOD ", "%");
        query = query.replace(" mod ", "%");

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
            if (queryBrk[i].equalsIgnoreCase("FROM")) {
                index = i;
                break;
            }
            argsStr += queryBrk[i] + " ";
        }

        if (index + 1 >= queryBrk.length)
            throw new IllegalArgumentException("Nombre de la tabla incompleto: " + query);
        tableName = queryBrk[++index];

        // ? Here i handle the where clause
        if (index != queryBrk.length - 1) {
            try {
                if (queryBrk[index + 1].equalsIgnoreCase("WHERE"))
                    for (int i = index + 2; i < queryBrk.length; i++)
                        conditionals += queryBrk[i] + " ";
                else 
                    throw new IllegalArgumentException("No se reconoce el comando: " + queryBrk[index + 1]);
                conditionals = conditionals.trim();
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("WHERE vacío");
            }
        }

        if (!FileManagement.searchForTable(tableName))
            throw new FileNotFoundException("No se encontró la tabla: " + tableName);

        // ! All this block is to manage the headers of the table
        String headerOfTable = Utilities.getHeaderOfTable(tableName);
        ArrayList<Header> headers = new ArrayList<>();

        String[] headerParts = headerOfTable.split(",");
        for (int i = 0; i < headerParts.length; i++)
            headers.add(new Header(headerParts[i], i));

        // ? Here i have the sorted headers, to replace them in the query
        headers.sort((h1, h2) -> h2.getName().length() - h1.getName().length());

        // ? Here they are the args for the query
        String[] argsBreak = argsStr.split(",");
        for (int i = 0; i < argsBreak.length; i++)
            argsBreak[i] = argsBreak[i].trim();

        // * Here im going to handle aliases
        HashMap<String, String> aliases = new HashMap<>();
        for (int i = 0; i < argsBreak.length; i++) {
            String[] parts = argsBreak[i].split(" ");
            String formedStr = "";

            for (int j = 0; j < parts.length; j++) {
                if (parts[j].equalsIgnoreCase("as")) {

                    argsBreak[i] = formedStr.trim();
                    formedStr = "";

                    for (int k = j + 1; k < parts.length; k++)
                        formedStr += parts[k] + " ";

                    if (formedStr.trim().isEmpty())
                        throw new IllegalArgumentException("Alias vacío");

                    formedStr = formedStr.trim();
                    if (!formedStr.startsWith("'") || !formedStr.endsWith("'"))
                        throw new IllegalArgumentException("Alias no está entre comillas: " + formedStr);

                    aliases.put(argsBreak[i], formedStr.trim());
                }
                formedStr += parts[j] + " ";
            }
        }
        // * End of handling aliases
        ArrayList<String> resultTable = new ArrayList<>();
        ArrayList<String> table = Utilities.getTable(tableName);

        for (int i = 1; i < table.size(); i++) {
            String[] lineBrk = table.get(i).split(",");
            if (Where.newWhere(conditionals, headers, lineBrk, table, ""))
                resultTable.add(table.get(i));
        }

        if (argsBreak[0].equals("*")) {
            if (argsBreak.length != 1)
                throw new IllegalArgumentException("No se puede colocar más de dos columnas en un select *");

            headers.sort((h1, h2) -> h1.getIndex() - h2.getIndex());

            for (int i = 0; i < headers.size(); i++)
                System.out.print(headers.get(i).getName() + " ");
            System.out.println();

            for (int i = 0; i < resultTable.size(); i++)
                System.out.println(resultTable.get(i));

            return "Select realizado con éxito";
        }

        ArrayList<String> finaltable = new ArrayList<>();

        // * Here i print headers
        String header = "";
        for (int i = 0; i < argsBreak.length; i++) {
            if (aliases.containsKey(argsBreak[i]))
                header += aliases.get(argsBreak[i]) + ",";
            else
                header += argsBreak[i] + ",";
        }
        if (header.endsWith(","))
            header = header.substring(0, header.length() - 1);
        finaltable.add(header);

        // ! First i iterate through the result table
        for (int i = 0; i < resultTable.size(); i++) {
            String[] lineBrk = resultTable.get(i).split(",");
            String line = "";

            // ! Then i iterate through the args
            for (int j = 0; j < argsBreak.length; j++) {
                String arg = argsBreak[j];
                line += Eval.eval(arg, headers, lineBrk, resultTable) + ",";
            }
            if (line.endsWith(","))
                line = line.substring(0, line.length() - 1);
            finaltable.add(line);
        }

        for (int i = 0; i < finaltable.size(); i++)
            System.out.println(finaltable.get(i));

        return "Select realizado con éxito";
    }
}
