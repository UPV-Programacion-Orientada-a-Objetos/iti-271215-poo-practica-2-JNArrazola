package edu.upvictoria.poo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to manage upper, lower, and other column functions
  */
public class ColumnFunctions {
    public static String manageColumnFunctions(String query) throws Exception {
        String[] parts = query.split(" ");

        // !Stores the columns that are going to be selected
        String columnsOnSelect = "";
        int index = 0;
        for (int i = 1; i < parts.length; i++) {
            if(parts[i].equalsIgnoreCase("FROM")){
                index = i;
                break;
            }
            columnsOnSelect += parts[i] + " ";
        }

        // !Stores the tablename
        String tableName = "";
        if(index + 1 < parts.length)
            tableName = parts[++index];
        else 
            throw new IllegalArgumentException("No se encontró el nombre de la tabla en la consulta");
        
        if(!FileManagement.searchForTable(tableName))
            throw new IllegalArgumentException("No se encontró la tabla: " + tableName);
        index++;

        // Stores the header of the table in the database
        String headerOfOriginalTable = Utilities.getHeaderOfTable(tableName);

        
        ArrayList<DataColumn> columns = new ArrayList<DataColumn>();
        String[] columnsOnSelectParts = columnsOnSelect.split(",");

        for(int i = 0; i < columnsOnSelectParts.length; i++)
            columnsOnSelectParts[i] = columnsOnSelectParts[i].trim();

        // for(String s : columnsOnSelectParts)
        //     System.out.println(s);

        for (String columnOnSelect : columnsOnSelectParts) 
            columns.add(new DataColumn(columnOnSelect, headerOfOriginalTable));
        
        // !here i manage where clause
        String conditionals = "";
        boolean flag = false;
        if(query.toUpperCase().contains("WHERE")){
            flag = true;
            for(int i = index; i < parts.length; i++){
                if(parts[i].equalsIgnoreCase("WHERE"))
                    continue;
                conditionals += parts[i] + " ";
            }
        }

        if(flag&&conditionals.isEmpty())
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
        //for(String s : resultTable)
        //    System.out.println(s);

        // !end of where clause management

        // * Print header
        for (int i = 0; i < columns.size(); i++) 
            System.out.print(columns.get(i).getAlias() + " ");
        System.out.println();

        // * Print data
        for (int i = 1; i < resultTable.size(); i++) {
            String[] partsOfLine = resultTable.get(i).split(",");

            for (int j = 0; j < columns.size(); j++) {
                String function = columns.get(j).getFunction();
                int indexColumn = columns.get(j).getIndex();

                if(function.isEmpty())
                    System.out.print(partsOfLine[indexColumn] + " ");
                else {
                    switch (function) {
                        case "UCASE":
                            System.out.print(UCASE(partsOfLine[indexColumn]) + " ");
                            break;
                        case "LCASE":
                            System.out.print(LCASE(partsOfLine[indexColumn]) + " ");
                            break;
                        case "CAPITALIZE":
                            System.out.print(CAPITALIZE(partsOfLine[indexColumn]) + " ");
                            break;
                        default:
                            throw new IllegalArgumentException("Función no reconocida: " + function);
                    }
                }
            }
            System.out.println();
        }
        // * End of printing data

        return "Select exitoso";
    }

    public static String UCASE(String s){
        return s.toUpperCase(); 
    }

    public static String LCASE(String s){
        return s.toLowerCase();
    }

    public static String CAPITALIZE(String s){
        for(int i = 0; i < s.length(); i++){
            if(Character.isLetter(s.charAt(i))){
                s = s.substring(0, i) + Character.toUpperCase(s.charAt(i)) + s.substring(i + 1);
                break;
            }
        }
        return s;
    }
}
