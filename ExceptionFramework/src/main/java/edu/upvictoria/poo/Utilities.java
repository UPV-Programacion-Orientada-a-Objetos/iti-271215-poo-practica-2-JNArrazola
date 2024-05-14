package edu.upvictoria.poo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/*
* Class of Utilities to store common functions that doesn't fit in other classes
* @author Joshua Arrazola
* */
public class Utilities {
    private static final Set<String> reservedWords = new HashSet<String>();
    private static final Set<String> types = new HashSet<String>();
    private static final Set<String> logicOperators = new HashSet<>();
    private static final Set<Character> ArithmeticOperators = new HashSet<>();
    private static final ArrayList<String> numericFunctions = new ArrayList<>();
    private static final Set<Character> validCharactersInOperation = new HashSet<>();
    private static final ArrayList<String> aggregateFunctions = new ArrayList<>();

    private static final BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

    /**
     * General function to read queries
     * */
    public static String readQuery(BufferedReader bf){
        String query = "";

        try  {
            query = bf.readLine();
        } catch (IOException e){
            System.out.println("Error leyendo query");
        }
        return query;
    }

    public static void fillReservedWords(){
        reservedWords.add("SELECT");
        reservedWords.add("INSERT");
        reservedWords.add("DELETE");
        reservedWords.add("FROM");
        reservedWords.add("INT");
        reservedWords.add("VARCHAR");
        reservedWords.add("NOT");
        reservedWords.add("NULL");
        reservedWords.add("UPDATE");
        reservedWords.add("SET");
        reservedWords.add("DATE");
        reservedWords.add("USE");
        reservedWords.add("ORDER");
        reservedWords.add("BY");
        reservedWords.add("WHERE");
        reservedWords.add("SET");
    }

    public static void fillLogicOperators(){
        logicOperators.add("AND");
        logicOperators.add("OR");
    }

    public static void fillTypes(){
        types.add("INT");
        types.add("VARCHAR");
        types.add("DATE");
        types.add("FLOAT");
        types.add("DOUBLE");
    }

    public static void fillValidCharactersInOperation(){
        validCharactersInOperation.add('+');
        validCharactersInOperation.add('-');
        validCharactersInOperation.add('*');
        validCharactersInOperation.add('/');
        validCharactersInOperation.add('(');
        validCharactersInOperation.add(')');
        validCharactersInOperation.add('#');
        validCharactersInOperation.add('%');
        validCharactersInOperation.add('0');
        validCharactersInOperation.add('1');
        validCharactersInOperation.add('2');
        validCharactersInOperation.add('3');
        validCharactersInOperation.add('4');
        validCharactersInOperation.add('5');
        validCharactersInOperation.add('6');
        validCharactersInOperation.add('7');
        validCharactersInOperation.add('8');
        validCharactersInOperation.add('9');
        validCharactersInOperation.add(' ');
    }

    public static void fillArithmeticOperators(){
        ArithmeticOperators.add('+');
        ArithmeticOperators.add('-');
        ArithmeticOperators.add('*');
        ArithmeticOperators.add('/');
        ArithmeticOperators.add('|');
        ArithmeticOperators.add('%');
    }

    public static void fillNumericFunctions(){
        numericFunctions.add("FLOOR");
        numericFunctions.add("ROUND");
        numericFunctions.add("RAND");
        numericFunctions.add("CEIL");
    }

    public static void fillAggregateFunctions(){
        aggregateFunctions.add("SUM");
        aggregateFunctions.add("AVG");
        aggregateFunctions.add("COUNT");
        aggregateFunctions.add("MAX");
        aggregateFunctions.add("MIN");
    }

    public static boolean isReservedWord(String word){
        return reservedWords.contains(word.toUpperCase());
    }

    public static boolean isType(String type){
        return types.contains(type.toUpperCase());
    }

    public static boolean isLogic(String logic){
        return logicOperators.contains(logic.toUpperCase());
    }

    public static boolean isArithmeticOperator(Character operator){
        return ArithmeticOperators.contains(operator); 
    }

    public static boolean isValidInEquation(Character character){
        return validCharactersInOperation.contains(character);
    }
    
    public static boolean isAggregateFunction(String function){
        return aggregateFunctions.contains(function.toUpperCase());
    }

    public static boolean nameValidations(String name){
        if(name.length() <= 1)
            throw new IllegalArgumentException("Nombre demasiado corto");
        else if(Utilities.isReservedWord(name)||Utilities.isType(name)||Utilities.isLogic(name))  
            throw new IllegalArgumentException("El nombre no puede ser una palabra reservada");
        else if(!Utilities.hasValidChars(name))
            throw new IllegalArgumentException("El nombre contiene carácteres no permitidos");
        return true;
    }

    /**
     * Sometimes it is better to have an array instead of a vector
     * specially when you don't want two datatypes
     * @return
      */
    public static ArrayList<String> getVectorOfDatatypes(){
        ArrayList<String> dataTypes = new ArrayList<>();
        
        for(String s : types)
            dataTypes.add(s);
        
        return dataTypes;
    }

    public static ArrayList<String> getVectorOfAggregateFunctions(){
        return aggregateFunctions;
    }

    public static ArrayList<String> getVectorOfNumericFunctions(){
        return numericFunctions;
    }

    public static boolean hasValidChars(String str){
        return !(str.contains(".")|| str.contains("/")||
                str.contains("|")||str.contains("&")||str.contains("=")
                ||str.contains("<")||str.contains(">")||str.contains("!")
                ||str.contains(".csv")||str.contains(".txt")||str.contains("_aux")
                ||str.contains(";")||str.contains(":"));
    }

    public static String readBuffer(){
        String query = "";
        do {
            if(query.isEmpty())
                System.out.println("Ingresa la query");

            String creatingQuery = Utilities.readQuery(bf).trim();

            if (creatingQuery.endsWith(";")){
                query+=creatingQuery;
                return query.substring(0, query.indexOf(";"));
            }
            else {
                if (!query.isEmpty()) {
                    query += " ";
                }
                query += creatingQuery;
            }
        }while (true);
    }

    public static TypeBuilder findType(String name, ArrayList<TypeBuilder> array) throws Exception{
        for(TypeBuilder type : array)
            if(type.getName().equals(name))
                return type;
        

        throw new Exception("No se encontró el tipo");
    }

    public static void deleteFilesFromWhere(){
        new File((new File("")).getAbsolutePath() + "/temporalAuxInfo.txt").delete();
        new File("dataBaseManager/src/main/java/edu/upvictoria/fpoo/TablaTemp.java").delete();
        new File("dataBaseManager/src/main/java/edu/upvictoria/fpoo/TablaTemp.class").delete();
        new File(new File("").getAbsolutePath()+"/temporalAuxInfo.csv").delete();
    }

    public static String getHeaderOfTable(String name) throws Exception {
        String path = FileManagement.getDatabasePath() + name + ".csv";
        
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        } catch (Exception e) {
            throw new Exception("No se encontró el archivo");
        }
    }
}
