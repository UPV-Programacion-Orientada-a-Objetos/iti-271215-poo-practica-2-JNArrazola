package edu.upvictoria.poo;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import org.junit.Test;
import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class AppTest

        extends TestCase {
    public AppTest(String testName) {
        super(testName);
    }

    public void testApp() {
        assertTrue(true);
    }

    /**
     * Test to validate if a word is reserved
     */
    @Test
    public void testIsReservedWord_True() {
        FileManagement.initialValidations();

        assertTrue(Utilities.isReservedWord("SELECT"));
        assertTrue(Utilities.isReservedWord("INSERT"));
        assertTrue(Utilities.isReservedWord("UPDATE"));
        assertTrue(Utilities.isReservedWord("FROM"));
        assertTrue(Utilities.isReservedWord("INT"));
        assertTrue(Utilities.isReservedWord("VARCHAR"));
    }

    /**
     * Test to validate if some given word
     */
    @Test
    public void testHasValidChars_AllValid() {
        // Todas las letras son válidas
        assertTrue(Utilities.hasValidChars("abcABC123"));
        assertTrue(Utilities.hasValidChars("SELECT"));
        assertTrue(Utilities.hasValidChars("INSERT"));
        assertTrue(Utilities.hasValidChars("EMPLOYEES"));
    }

    /**
     *
     */
    @Test
    public void tesHasValidChars_NotValid() {
        assertFalse(Utilities.hasValidChars("/home/path"));
        assertFalse(Utilities.hasValidChars("||name||"));
        assertFalse(Utilities.hasValidChars(".exec"));
    }

    /**
     * Not valid queries
     */
    @Test
    public void testParse() throws Exception {
        String[] query = { "masmcamd mwd cmqmd", "esto no es una sentencia", "esto tampoco",
                "tengo hambre", "sexto cuatrimestr"};

        for (String s : query)
            assertEquals("No se reconoció la sentencia", Parser.parseQuery(s));
    }

    /**
     * Use database with a totally new and not existing directory
     */

    /**
     * Invalid path test
     */
    @Test
    public void testUseDatabase_invalid() throws Exception {
        FileManagement.initialValidations();
        String query = "USE /W";

        Exception generatedException = assertThrows(Exception.class, () -> {
            Parser.parseQuery(query);
        });

        assertEquals("El directorio no existe", generatedException.getMessage());
    }

    /**
     * Permission problem
     */
    @Test
    public void testUseDatabase_invalid2() throws Exception {
        FileManagement.initialValidations();
        String query = "USE /";

        Exception generatedException = assertThrows(java.lang.Exception.class, () -> {
            Parser.parseQuery(query);
        });

        assertEquals("No tengo permisos para acceder a este directorio", generatedException.getMessage());
    }

    /**
     * Crear tabla
     * Sin path para guardar el archivo previamente configurado
     */
    @Test
    public void testCreateTable() throws Exception {
        FileManagement.initialValidations();
        FileManagement.setDatabasePath(null);
        String query = "CREATE TABLE EMP (ID int not null);";

        Exception generatedException = assertThrows(java.lang.Exception.class, () -> {
            Parser.parseQuery(query);
        });

        assertEquals("No hay path asignado", generatedException.getMessage());
    }

    /*
     * Checar que los paréntesis estén correctos
     */
    @Test
    public void testParenthesis() {
        FileManagement.initialValidations();
        FileManagement.setDatabasePath(new File("").getAbsolutePath() + "/");
        String[] strs = { "SELECT * FROM (Locations, CREATE DATABASE(", "((())", "()()()(" };

        for (String s : strs)
            assertFalse(Parser.parenthesisCheck(s));
    }

    /**
     * Without primary key
     * @throws Exception
     */
    @Test
    public void testCreateTable_notValidPrimaryKey() throws Exception {
        FileManagement.initialValidations();
        String path = new File("").getAbsolutePath() + "/";
        FileManagement.setDatabasePath(path);
        String query = "CREATE TABLE JOSHUA (ID int not null, NAME varchar(50) not null, MONEY int not null, HEIGHT float not null)";


        Exception generatedException = assertThrows(Exception.class, () -> {
            Parser.parseQuery(query);
        });

        assertEquals("No se puede crear una tabla sin PK", generatedException.getMessage());
    }

    /**
     * Here we are passing some words that nothing have to do with the query
     * @throws Exception
      */
    @Test
    public void testCreateTableInvalidArguments() throws Exception {
        FileManagement.initialValidations();
        String path = new File("").getAbsolutePath() + "/";
        FileManagement.setDatabasePath(path);
        String query = "create table add(id int not null primary key pedro elizondo);";

        Exception generatedException = assertThrows(Exception.class, () -> {
            Parser.parseQuery(query);
        });

        assertEquals("El nombre no puede ser una palabra reservada", generatedException.getMessage());
        new File(new File("").getAbsolutePath() + "/JOSHUA.csv").delete();
        new File(new File("").getAbsolutePath() + "/JOSHUA_aux.txt").delete();
    }


    // ---------------------------------------------- SELECT TEST ----------------------------------------------
    /**
     * Select with an invalid alias
      */
    @Test
    public void testAlias(){
        Exception e = assertThrows(Exception.class, () -> {
            Parser.parseQuery("use /home/jarrazola/Documents/iti-271215-poo-practica-2-JNArrazola/test/");
            Parser.parseQuery("select name as 'nombre', id as apellido from testTables;");
        });
        assertEquals("No se encontró la tabla: testTables;", e.getMessage());
    }

    /**
     * Select with invalid arguments
      */
    @Test
    public void testArguments(){
        Exception e = assertThrows(Exception.class, () -> {
            Parser.parseQuery("use /home/jarrazola/Documents/iti-271215-poo-practica-2-JNArrazola/test/");
            Parser.parseQuery("select id, a from test");
        });
        assertEquals("Error en la evaluación: No se reconoció 'a'", e.getMessage());
    }

    @Test 
    public void testTableDoesntExist(){
        Exception e = assertThrows(Exception.class, () -> {
            Parser.parseQuery("use /home/jarrazola/Documents/iti-271215-poo-practica-2-JNArrazola/test/");
            Parser.parseQuery("select * from a");
        });
        assertEquals("No se encontró la tabla: a", e.getMessage());
    }

    /**
     * Path is null
     */
    @Test
    public void testSelect_nullPath() throws Exception {
        FileManagement.initialValidations();
        FileManagement.setDatabasePath(null);
        String query = "SELECT * FROM JOSHUA";

        Exception generatedException = assertThrows(Exception.class, () -> {
            Parser.parseQuery(query);
        });

        assertEquals("No se ha accedido a ninguna base de datos", generatedException.getMessage());
    }

    /**
     * Table does not exist
     */
    @Test
    public void testSelect_tableDoesNotExist() throws Exception {
        FileManagement.initialValidations();
        String path = new File("").getAbsolutePath() + "/";
        FileManagement.setDatabasePath(path);
        String query = "SELECT * FROM JOSHUA";

        Exception generatedException = assertThrows(Exception.class, () -> {
            Parser.parseQuery(query);
        });

        assertEquals("No se encontró la tabla: JOSHUA", generatedException.getMessage());
    }

    // ---------------------------------------------- DELETE TEST ----------------------------------------------
    /**
     * File does not exist case
      */
    @Test
    public void testDeleteTableDoesNotExist() throws Exception {
        Exception generatedException = assertThrows(Exception.class, () -> {
            Parser.parseQuery("use /home/jarrazola/Documents/iti-271215-poo-practica-1-JNArrazola/test");
            Parser.parseQuery("DELETE FROM tesamd WHERE id = 1;");
        });

        assertEquals("El directorio no existe", generatedException.getMessage());
    }

    //---------------------------------------------- CREATE TEST ----------------------------------------------
    /**
     * Create test
      */
    @Test
    public void testCreateTableWithDuplicateKey(){
        Exception e = assertThrows(Exception.class, () -> {
            Parser.parseQuery("use /home/jarrazola/Documents/iti-271215-poo-practica-2-JNArrazola/test/");
            Parser.parseQuery("CREATE TABLE PRUEBA(ID INT NOT NULL PRIMARY KEY, ID INT NOT NULL PRIMARY KEY)");
        });
        assertEquals("Nombre de columna repetido", e.getMessage());
    }

    

    // ---------------------------------------------- UPDATE TEST ----------------------------------------------
    /**
     * Update with invalid arguments
      */
    @Test
    public void testUpdateInvalid(){
        Exception e = assertThrows(Exception.class, () -> {
            Parser.parseQuery("use /home/jarrazola/Documents/iti-271215-poo-practica-2-JNArrazola/test/");
            Parser.parseQuery("update test set a=2, b=3 where i = 1");
        });

        assertEquals(e.getMessage(), "Columna no encontrada en la tabla: a");
    }
    
    // ---------------------------------------------- INSERT TEST ----------------------------------------------
    /**
     * Insert with columns not matching the values
      */
    @Test
    public void testInsertInvalid(){
        Exception e = assertThrows(Exception.class, () -> {
            Parser.parseQuery("use /home/jarrazola/Documents/iti-271215-poo-practica-2-JNArrazola/test/");
            Parser.parseQuery("insert into test (id, name) values (1, 'joshua', 2)");
        });

        assertEquals("Los valores a insertar y las columnas no coinciden", e.getMessage());
    }

    /**
     * Repeated primary key
      */
    @Test
    public void testInsertRepeatedPrimaryKey(){
        Exception e = assertThrows(Exception.class, () -> {
            Parser.parseQuery("use /home/jarrazola/Documents/iti-271215-poo-practica-2-JNArrazola/test/");
            Parser.parseQuery("insert into test (id, name) values (1,'jose')");
        });

        assertEquals("La primary key se repetiría, lo que compromete la integridad de la base de datos", e.getMessage());
    }

    /**
     * Insert with invalid types of arguments
      */
    @Test
    public void testInsertInvalidTypes(){
        Exception e = assertThrows(Exception.class, () -> {
            Parser.parseQuery("use /home/jarrazola/Documents/iti-271215-poo-practica-2-JNArrazola/test/");
            Parser.parseQuery("INSERT INTO test (id,name) values (15,34)");
            Parser.parseQuery("DELETE FROM test WHERE values id = 15");
        });

        assertEquals("Faltan comillas simples en 34", e.getMessage());
    }

    /**
     * Null values that should be null
      */
    @Test
    public void testNullVerification(){
        Exception e = assertThrows(Exception.class, () -> {
            Parser.parseQuery("use /home/jarrazola/Documents/iti-271215-poo-practica-2-JNArrazola/test/");
            Parser.parseQuery("INSERT INTO test (id,height) values (15,1.20)");
        });

        assertEquals("Hay valores nulos que no deberían de serlo", e.getMessage());
    }

    /**
     * Precision test
     */
    @Test
    public void testPrecision(){
        Exception e = assertThrows(Exception.class, () -> {
            Parser.parseQuery("use /home/jarrazola/Documents/iti-271215-poo-practica-2-JNArrazola/test/");
            Parser.parseQuery("INSERT INTO test (id,name,height) values (15,'pedro',1.2314)");
        });

        assertEquals("Precisión equivocada", e.getMessage());
    }
}

