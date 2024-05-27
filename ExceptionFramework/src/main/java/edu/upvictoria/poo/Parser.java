package edu.upvictoria.poo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Function that parses the query to be able to distinguish between different
 * types of them
 *
 * @author Joshua Arrazola
 */
public class Parser {
    private static final BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    public static boolean flag = false;
    /**
     * Function to parse the query, and start dividing it to see what type it is and
     * filter if there's a typo
     */
    public static String parseQuery(String query) throws Exception {
        query = query.trim();

        if(!flag) {
            FileManagement.initialValidations();
            flag = true;
        }
        String brokeStr[] = query.split(" ");

        if(!parenthesisCheck(query))
            throw new IllegalArgumentException("Parentesis mal colocados");

        try {
            if (brokeStr.length <= 1)
                throw new RuntimeException("Query incompleta");
            if (brokeStr[0].equalsIgnoreCase("USE")) {
                return FileManagement.useDatabase(query, brokeStr);
            } else if (brokeStr[0].equalsIgnoreCase("UPDATE")) {
                return update(query);
            } else if (brokeStr[0].equalsIgnoreCase("DELETE") && brokeStr[1].equalsIgnoreCase("FROM")) {
                return deleteFrom(query);
            } else if (brokeStr[0].equalsIgnoreCase("SELECT")) {
                return Select.select(query);
            } else if (brokeStr[0].equalsIgnoreCase("INSERT") && brokeStr[1].equalsIgnoreCase("INTO")) {
                return (insertInto(query));
            } else if (brokeStr[0].equalsIgnoreCase("CREATE") && brokeStr[1].equalsIgnoreCase("TABLE")) {
                return createTable(query);
            } else if (brokeStr[0].equalsIgnoreCase("SHOW") && brokeStr[1].equalsIgnoreCase("TABLES")) {
                return showTables(query, brokeStr);
            } else if (brokeStr[0].equalsIgnoreCase("DROP") && brokeStr[1].equalsIgnoreCase("TABLE")) {
                return dropTable(query, brokeStr);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return "No se reconoció la sentencia";
    }

    private static File[] obtainTableList() {
        File files = new File(FileManagement.getDatabasePath());
        return files.listFiles();
    }

    /**
     * Function to manage show tables query
     */
    private static String showTables(String query, String[] brokeStr) throws Exception {
        if (FileManagement.getDatabasePath() == null)
            throw new NullPointerException("No hay path designado");

        if (brokeStr.length > 2)
            throw new Exception("Sintaxis incorrecta");

        File[] files = obtainTableList();

        try {
            if (files.length == 0)
                return "No hay archivos para listar";
        } catch (NullPointerException e) {
            throw new NullPointerException("El directorio no se encontró");
        }

        for (File file : files) {
            String name = file.getName();
            if (name.contains("aux"))
                continue;
            if (file.getName().contains(".csv") && !file.getName().contains("~"))
                System.out.println("* " + name.substring(0, name.indexOf(".csv")));
        }
        return "Listado éxitoso";
    }

    /**
     * Function to manage and also parse CREATE TABLE query
     */
    private static String createTable(String query) throws Exception {
        if (FileManagement.getDatabasePath() == null)
            throw new NullPointerException("No hay path asignado");

        // Analizar primera parte del string (CREATE TABLE name)
        if (query.indexOf("(") == -1)
            throw new IllegalArgumentException("Sintaxis incorrecta");

        String firstPart = query.substring(0, query.indexOf("("));
        String[] firstPartBreak = firstPart.split(" ");

        // esto quiere decir que no se entiende donde esta el create table|
        if (firstPartBreak.length < 3)
            throw new Exception("Error de sintaxis");

        // Verificar las palabras
        String tableName = firstPartBreak[2];

        // Verificar que no exista el archivo, además verificar ciertas propiedades del
        // nombre
        if (FileManagement.searchForTable(tableName))
            throw new FileAlreadyExistsException("Nombre de tabla repetido");

        // Boolean function que valida el nombre
        Utilities.nameValidations(tableName);

        // Extraer los argumentos y separarlos
        String arguments = query.substring(query.indexOf("(") + 1, query.length() - 1);
        String[] argumentsBreak = arguments.split(",");

        for (int i = 0; i < argumentsBreak.length; i++)
            argumentsBreak[i] = argumentsBreak[i].trim();

        // Recorrer cada uno de los argumentos, se busca que al menos exista:
        // * Una y sólo una primary key
        // * Por lo menos un tipo de dato
        // * Que tenga un nombre la variable
        // * Si no se especifica not null, se asume que puede ser null
        // * Si es primary key no puede ser null
        // * No se le puede especificar longitud a una variable int o date

        HashSet<String> assignedColumnNames = new HashSet<>();
        ArrayList<TypeBuilder> typesOfTable = new ArrayList<>();

        boolean hasPK = false;
        try {
            for (String s : argumentsBreak) {
                String type = "", length = "";
                boolean canBeNull = true, isPk = false;

                String[] individualArgumentBreak = s.split(" ");
                for(int i = 0; i < individualArgumentBreak.length; i++)
                    individualArgumentBreak[i] = individualArgumentBreak[i].trim();
                
                for(int i = 1; i < individualArgumentBreak.length; i++)
                    if(!Utilities.isValidReservedWordCreateTable(individualArgumentBreak[i]))
                        throw new IllegalArgumentException(individualArgumentBreak[i] + " no es una palabra reservada válida");

                // Check for name
                String columnName = individualArgumentBreak[0];
                Utilities.nameValidations(columnName);
                if (assignedColumnNames.contains(columnName))
                    throw new IllegalArgumentException("Nombre de columna repetido");

                // Check for type
                for (String substr : individualArgumentBreak) {
                    ArrayList<String> types = Utilities.getVectorOfDatatypes();

                    for (String t : types)
                        if (substr.toUpperCase().contains(t))
                            if (type.equals(""))
                                type = substr;
                            else
                                throw new IllegalArgumentException("No se pueden tener dos tipos de datos");

                }
                if (type.equals(""))
                    throw new IllegalArgumentException("No se encontró el tipo de dato");

                // Check for length
                // also check if the length is invalid
                if (type.contains("("))
                    length = type.substring(type.indexOf("(") + 1, type.indexOf(")"));
                if (!(length.equalsIgnoreCase(""))
                        && (type.toLowerCase().contains("int") || type.toLowerCase().contains("date")))
                    throw new IllegalArgumentException("INT y DATE no pueden tener precisión");

                // try to parse length
                if (!length.equals(""))
                    try {
                        int integer = Integer.parseInt(length);
                        if (integer != -1 && integer <= 0)
                            throw new NumberFormatException("Longitud inválida");
                    } catch (Exception e) {
                        throw new NumberFormatException("Longitud no válida");
                    }

                if (s.toUpperCase().contains("NOT NULL"))
                    canBeNull = false;
                else if (s.toUpperCase().contains("NULL"))
                    canBeNull = true;

                if (s.toUpperCase().contains("PRIMARY KEY"))
                    if (s.toUpperCase().contains("NULL") && !s.toUpperCase().contains("NOT NULL"))
                        throw new IllegalArgumentException("No puede ser PK y a la vez nula");
                    else if (hasPK)
                        throw new IllegalArgumentException("No se pueden tener dos PK");
                    else {
                        hasPK = true;
                        isPk = true;
                    }
                

                assignedColumnNames.add(columnName.trim());
                typesOfTable.add(new TypeBuilder(columnName.trim(), canBeNull,
                        ((length.equals("") ? type : type.substring(0, type.indexOf("(")))),
                        ((!length.equals("") ? Integer.parseInt(length) : -1)), isPk));
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("Argumentos dentro de la creación de columnas inválidos");
        }

        if (!hasPK)
            throw new IllegalArgumentException("No se puede crear una tabla sin PK");
        FileManagement.createFileTable(tableName, typesOfTable);

        return "Tabla creada correctamente";
    }

    /**
     * Function to manage drop table function
     */
    private static String dropTable(String query, String[] brokeStr) throws Exception {
        if (FileManagement.getDatabasePath() == null)
            throw new NullPointerException("No hay path asignado");

        if (brokeStr.length > 3)
            throw new IllegalArgumentException("Sintaxis incorrecta");

        String name;
        name = brokeStr[2];

        if (name == null || name.isEmpty() || name.contains(".csv") || name.contains(".txt"))
            throw new FileNotFoundException("No se encontró el archivo");

        String auxFile = name + "_aux.txt";
        name += ".csv";
        File[] files = obtainTableList();

        for (File file : files)
            if (file.getName().equalsIgnoreCase(name)) {
                System.out.println("¿Seguro que deseas borrar la tabla(y/n)?");

                String answ;
                try {
                    answ = bf.readLine();

                    if (answ.equalsIgnoreCase("y")) {
                        file.delete();
                        new File(FileManagement.getDatabasePath() + auxFile).delete();
                        return "Tabla borrada éxitosamente";
                    } else {
                        return "Tabla no eliminada";
                    }

                } catch (IOException e) {
                    throw new IOException("No se pudo borrar la tabla");
                }

            }

        return "No se encontró la tabla";
    }

    public static String insertInto(String query) throws Exception {
        if (FileManagement.getDatabasePath() == null)
            throw new IOException("No hay ninguna base de datos seleccionada");

        String[] parts = query.split("\\s+(?=\\([^)]*\\))|\\s+(?![^(]*\\))");

        for (int i = 0; i < parts.length; i++)
            parts[i] = parts[i].replaceAll("\\(", "").replaceAll("\\)", "");

        if (parts.length > 6)
            throw new RuntimeException("Query inválida");

        String name = parts[2];

        if (!FileManagement.searchForTable(name))
            throw new FileNotFoundException("No se encontró el archivo");

        String columns = parts[3];

        if (columns.equalsIgnoreCase("VALUES"))
            throw new IllegalArgumentException("Falta especificar columnas");

        if (!parts[4].equalsIgnoreCase("VALUES"))
            throw new IllegalArgumentException("Sintaxis no válida");

        String values = parts[5];

        String[] colBrk = columns.split(",");
        String[] valBrk = values.split(",");

        if (colBrk.length != valBrk.length)
            throw new RuntimeException("Los valores a insertar y las columnas no coinciden");

        for (int i = 0; i < colBrk.length; i++) {
            colBrk[i] = colBrk[i].trim();
            valBrk[i] = valBrk[i].trim();
        }

        ArrayList<TypeBuilder> types = FileManagement.decompressInfo(name);

        String header;
        try (BufferedReader br = new BufferedReader(new FileReader(FileManagement.getDatabasePath() + name + ".csv"))) {
            header = br.readLine();
        } catch (IOException e) {
            throw new IOException("No se pudo abrir el archivo");
        }

        String[] headerBrk = header.split(",");

        for (int i = 0; i < colBrk.length; i++)
            colBrk[i] = colBrk[i].trim();

        for (int i = 0; i < colBrk.length; i++) {
            boolean flag = false;
            for (int j = 0; j < types.size(); j++) {
                if (colBrk[i].equals(types.get(j).getName())) {
                    flag = true;
                    break;
                }
            }

            if (!flag)
                throw new IllegalArgumentException("Valores de insert into inválidos");

        }

        if (header.isEmpty())
            throw new RuntimeException("Ocurrió un error al leer la base de datos");

        String headerBuilder = "";

        for (int i = 0; i < headerBrk.length; i++) {
            boolean flag = false;
            for (int j = 0; j < colBrk.length; j++) {
                if (colBrk[j].equalsIgnoreCase(headerBrk[i])) {
                    headerBuilder += valBrk[j] + ",";
                    flag = true;
                    break;
                }
            }
            if (!flag)
                headerBuilder += "null,";
        }
        headerBuilder = headerBuilder.substring(0, headerBuilder.length() - 1);

        String[] headerBrkValues = headerBuilder.split(",");

        // types es el tipo de cada uno de los valores de la columna
        // headerBrkValues son los valores de las columnas
        // headerBrk son los nombres de la columna

        for (int i = 0; i < headerBrkValues.length; i++)
            for (TypeBuilder type : types)
                if (type.getName().equalsIgnoreCase(headerBrk[i])) {
                    if (type.isPrimaryKey())
                        verifyUniqueness(type.getName(), name, headerBrkValues[i]);
                    checkType(type, headerBrkValues[i]);
                }

        String stringToWrite = "";
        for (int i = 0; i < headerBrkValues.length; i++)
            stringToWrite += headerBrkValues[i] + ",";
        stringToWrite = stringToWrite.substring(0, stringToWrite.length() - 1);

        try (BufferedWriter bf = new BufferedWriter(
                new FileWriter(FileManagement.getDatabasePath() + name + ".csv", true))) {
            bf.write(stringToWrite);
            bf.newLine();
        } catch (IOException e) {
            throw new IOException("No se pudo abrir el archivo");
        }

        return "Registro éxitoso";
    }

    public static boolean verifyUniqueness(String colName, String tableName, String value) throws Exception {
        try (BufferedReader br = new BufferedReader(
                new FileReader(FileManagement.getDatabasePath() + tableName + ".csv"))) {
            String line = br.readLine();

            int index = 0;

            String[] lnBreak = line.split(",");
            for (int i = 0; i < lnBreak.length; i++) {
                if (lnBreak[i].equalsIgnoreCase(colName)) {
                    index = i;
                    break;
                }
            }

            while ((line = br.readLine()) != null) {
                String[] brkLine = line.split(",");

                if (brkLine[index].equalsIgnoreCase(value))
                    throw new IllegalArgumentException("La primary key se repetiría, lo que compromete la integridad de la base de datos");

            }

        } catch (NumberFormatException e) {
            throw new NumberFormatException("La pk se repite");
        }
        return true;
    }

    public static boolean checkType(TypeBuilder T, String value) throws Exception {
        if (value.equalsIgnoreCase("null") && T.getCanBeNull())
            return true;
        else if (value.equalsIgnoreCase("null") && !T.getCanBeNull())
            throw new RuntimeException("Hay valores nulos que no deberían de serlo");

        switch (T.getDataType()) {
            case "int":
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Tipo de dato de " + value + " incorrecto");
                }
                return true;
            case "float":
                try {
                    Float.parseFloat(value);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Tipo de dato de " + value + " incorrecto");
                }
                if (T.getLength() != -1 && value.length() - 1 - value.indexOf(".") > T.getLength())
                    throw new NumberFormatException("Precisión equivocada");
                return true;

            case "double":
                try {
                    Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Tipo de dato de " + value + " incorrecto");
                }
                if (T.getLength() != -1 && value.contains(".")
                        && value.length() - 1 - value.indexOf(".") > T.getLength())
                    throw new NumberFormatException("Precisión equivocada");
                return true;
            case "varchar":
                if (value.charAt(0) != '\'' || value.charAt(value.length() - 1) != '\'')
                    throw new NumberFormatException("Faltan comillas simples en " + value);
                if (T.getLength() != -1 && value.length() > T.getLength())
                    throw new Exception("Mayor longitud en " + value);
                return true;
            case "date":
                if(value.charAt(0)!='\'' || value.charAt(value.length()-1)!='\'')
                    throw new NumberFormatException("Faltan comillas simples en "+ value);
                return true;
        }

        return false;
    }

    

    public static String deleteFrom(String query) throws Exception {
        if (FileManagement.getDatabasePath() == null)
            throw new FileNotFoundException("No se ha accedido a ninguna base de datos");

        String[] words = query.split(" ");

        if (words.length < 3)
            throw new Exception("Sintaxis incorrecta");

        String tableName = words[2];

        if (!FileManagement.searchForTable(tableName))
            throw new Exception("No se ha encontrado el archivo;");

        String condicionales = "";
        if (query.contains("WHERE") || query.contains("where")) {
            for (int i = 3; i < words.length; i++) {
                if (Utilities.isReservedWord(words[i]))
                    continue;
                condicionales += words[i] + " ";
            }
        }

        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new FileReader(FileManagement.getDatabasePath() + tableName + ".csv"))) {
            String line;
            lines.add(line = br.readLine());
            while ((line = br.readLine()) != null) {
                if (!Where.manageWhere(condicionales, line, tableName))
                    lines.add(line);
            }
        } catch (IOException e) {
            throw new IOException("No se pudo abrir el archivo");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new Exception("Error de sintaxis");
        }

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FileManagement.getDatabasePath() + tableName + ".csv"))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new IOException("No se pudo abrir el archivo");
        }

        return "Delete realizado con éxito";
    }

    public static String update(String query) throws Exception {
        if(FileManagement.getDatabasePath() == null)
            throw new FileNotFoundException("No se ha accedido a ninguna base de datos");

        query = query.replace(" DIV ", "#");
        query = query.replace(" div ", "#");
        query = query.replace(" MOD ", "%");
        query = query.replace(" mod ", "%");

        String[] words = query.split(" ");

        String tableName = "";

        try{
            tableName = words[1];
        } catch (Exception e) {
            throw new Exception("No se encontró el nombre de la tabla");
        }

        if(!FileManagement.searchForTable(tableName))
            throw new FileNotFoundException("No se encontró la tabla: " + tableName);

        String set = "", conditionals = "";

        try {
            if (!words[2].equalsIgnoreCase("SET"))
                throw new Exception("Sintaxis incorrecta: Se esperaba SET");
        } catch (Exception e) {
            throw new Exception("Sintaxis incompleta");
        }

        int index = -1;
        for (int i = 3; i < words.length; i++) {
            if (words[i].equalsIgnoreCase("WHERE")) {
                index = i;
                break;
            }
            set += words[i] + " ";
        }
        
        if(set.isEmpty())
            throw new IllegalArgumentException("No se encontraron valores en el set");
        
        if(index != -1){
            for (int i = index + 1; i < words.length; i++)
                conditionals += words[i] + " ";
            if(conditionals.isEmpty())
                throw new IllegalArgumentException("Error: WHERE vacío");
        }
        
        
        
        ArrayList<String> table = Utilities.getTable(tableName);

        // ! Headers
        String headerOfTable = Utilities.getHeaderOfTable(tableName);
        ArrayList<Header> headers = new ArrayList<>();

        String[] headerParts = headerOfTable.split(",");
        for (int i = 0; i < headerParts.length; i++)
            headers.add(new Header(headerParts[i], i));
        
        // ? Here i have the sorted headers, to replace them in the query
        headers.sort((h1, h2) -> h2.getName().length() - h1.getName().length());
        // ! End of headers

        // ? Work the set values
        HashMap<String, String> setValues = new HashMap<>();

        String[] workedSet = set.split(",");
        for (int i = 0; i < workedSet.length; i++)
            workedSet[i] = workedSet[i].trim();

        for (int i = 0; i < workedSet.length; i++) {
            String[] parts = workedSet[i].split("=");

            if(parts.length != 2)
                throw new IllegalArgumentException("Error en el set: " + workedSet[i]);
            
            parts[0] = parts[0].trim();
            parts[1] = parts[1].trim();

            if(parts[1].isEmpty()||parts[0].isEmpty())
                throw new IllegalArgumentException("Error en el set: " + workedSet[i]);

            boolean flag = false;
            for (int j = 0; j < headers.size(); j++) {
                if (parts[0].equalsIgnoreCase(headers.get(j).getName())) {
                    
                    parts[0] = Integer.toString(headers.get(j).getIndex());
                    
                    if(setValues.containsKey(parts[0]))
                        throw new IllegalArgumentException("Columna repetida en el set: " + parts[0]);

                    setValues.put(parts[0], parts[1]);
                    
                    flag = true;
                    break;
                }
            }

            if (!flag)
                throw new IllegalArgumentException("Columna no encontrada en la tabla: " + parts[0]);
        }

        ArrayList<String> finalTable = new ArrayList<>();
        finalTable.add(table.get(0));

        for (int i = 1; i < table.size(); i++) {
            String[] lineBrk = table.get(i).split(",");
            String formedString = "";
            
            try {
                if(Where.newWhere(conditionals, headers, lineBrk, table, "")){
                    for(int j = 0; j < lineBrk.length; j++){
                        if(setValues.containsKey(Integer.toString(j))){
                            String result = Eval.eval(setValues.get(Integer.toString(j)), headers, lineBrk, finalTable);
                            result = result.trim();
    
                            String columnName = "";
                            for(Header h : headers)
                                if(h.getIndex() == j)
                                    columnName = h.getName();
                            
                            String type = Utilities.getType(columnName, tableName);
                            
                            if(result.equalsIgnoreCase("null")){
                                formedString += "null,";
                                continue;
                            }
    
                            switch (type) {
                                case "int":
                                    try {
                                        int number;
                                        try {
                                            number = Integer.parseInt(result);
                                        } catch (NumberFormatException e1) {
                                            double temp = Double.parseDouble(result);
                                            if (temp == (int) temp) {
                                                number = (int) temp;
                                            } else {
                                                throw new NumberFormatException("Tipo de dato incorrecto en " + result);
                                            }
                                        }
                                        formedString += number + ",";
                                    } catch (NumberFormatException e) {
                                        throw new NumberFormatException("Tipo de dato incorrecto en " + result);
                                    }
                                    break;
                                case "float":
                                    try {
                                        formedString += Float.parseFloat(result) + ",";
                                    } catch (NumberFormatException e) {
                                        throw new NumberFormatException("Tipo de dato incorrecto en " + result);
                                    }
                                    break;
                                case "double":
                                    try {
                                        formedString += Double.parseDouble(result) + ",";
                                    } catch (NumberFormatException e) {
                                        throw new NumberFormatException("Tipo de dato incorrecto en " + result);
                                    }
                                    break;
                                case "varchar":
                                    if(result.charAt(0) != '\'' || result.charAt(result.length() - 1) != '\'')
                                        throw new NumberFormatException("Faltan comillas simples en " + result);
                                    formedString += result + ",";
                                    break;
                                case "date":
                                    if(result.charAt(0) != '\'' || result.charAt(result.length() - 1) != '\'')
                                        throw new NumberFormatException("Faltan comillas simples en " + result);
                                    formedString += result + ",";
                                    break;
                                default:
                                    break;
                            }
                            
                        }
                        else
                            formedString += lineBrk[j] + ",";
                    }
                    formedString = formedString.substring(0, formedString.length() - 1);
                    finalTable.add(formedString);
                } else 
                    finalTable.add(table.get(i));
            } catch (Exception e) {
                return "Error en la consulta WHERE";
            } 
        }

        ArrayList<TypeBuilder> types = FileManagement.decompressInfo(tableName);

        int indexPK = -1;

        for(int i = 0; i < types.size(); i++)
            if(types.get(i).isPrimaryKey())
                indexPK = i;
        

        if(indexPK == -1)
            throw new IllegalArgumentException("No se encontró la PK");

        HashSet<String> pkValues = new HashSet<>();

        for(int i = 1; i < finalTable.size(); i++){
            String[] lineBrk = finalTable.get(i).split(",");
            if(pkValues.contains(lineBrk[indexPK]))
                throw new IllegalArgumentException("La PK se repetiría, lo que compromete la integridad de la base de datos");
            pkValues.add(lineBrk[indexPK]);
        }


        System.out.println("Este será el resultado: ");
        System.out.println();

        for(int i = 0; i < finalTable.size(); i++){
            String[] lineBrk = finalTable.get(i).split(",");
            for(int j = 0; j < lineBrk.length; j++)
                System.out.print(lineBrk[j] + " ");
            System.out.println();
        }

        System.out.println("¿Desea guardar los cambios? (y/n)");
        String answ = bf.readLine();

        if(answ.equalsIgnoreCase("y")){
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FileManagement.getDatabasePath() + tableName + ".csv"))) {
                for (String line : finalTable) {
                    bw.write(line);
                    bw.newLine();
                }
            } catch (IOException e) {
                throw new IOException("No se pudo abrir el archivo");
            }
        } else {
            return "Update cancelado";
        }

        return "Update realizado con éxito";
    }

    /**
     * Function to validate parenthesis
     *
     * @return boolean
     */
    public static boolean parenthesisCheck(String query) {
        int parenthesis = 0;
        for (int i = 0; i < query.length(); i++) {
            switch (query.charAt(i)) {
                case '(':
                    parenthesis++;
                    break;
                case ')':
                    parenthesis--;
                    if (parenthesis < 0)
                        return false;
                    break;
            }
        }

        return parenthesis == 0;
    }
}