package edu.upvictoria.poo;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Queue;
import java.util.LinkedList;



public class Where {
	/**
	 * Steps to do it
	 * Im gonna store all the operators
	 * Then im gonna store all the conditionals
	 * Then im going to recursively evaluate all the conditionals
	 * as a base case, when the index is greater than the operator array itself im
	 * going to return the boolean
	 */

	/**
	 * This method is going to manage the where clause
	 * 
	 * @param condicionales A string of everything that comes before where clause
	 * @param line          The line that is going to be evaluated
	 * @param ind           A map of (name, index) of the columns
	 * @param type          A map of (name, type) of the columns
	 * @return A boolean that is going to be true if the line is going to be added
	 *         to the result
	 * @throws Exception If the where clause is invalid
	 */
	public static boolean manageWhere(String condicionales, String line, String tableName) throws Exception {	
		if (condicionales.equals(""))
			return true;

		String headerOfTable = Utilities.getHeaderOfTable(tableName);
		ArrayList<TypeBuilder> types = FileManagement.decompressInfo(tableName);

		HashMap<String, Integer> indexMap = new HashMap<>();
		HashMap<String, String> typeMap = new HashMap<>();

		String[] headerOfTableBreak = headerOfTable.split(",");
		for (int i = 0; i < headerOfTableBreak.length; i++) {
			indexMap.put(headerOfTableBreak[i], i);

			for (int j = 0; j < types.size(); j++)
				if (types.get(j).getName().equals(headerOfTableBreak[i]))
					typeMap.put(headerOfTableBreak[i], types.get(j).getDataType());
		}

		// As a parameter i need the conditional sentence, the line i will evaluate, and
		// the map of (name,index) and a hm of types
		ArrayList<String> conditionals = new ArrayList<>();
		ArrayList<String> operators = new ArrayList<>();

		String[] condicionalesBreak = condicionales.split(" ");
		String conditional = "";

		for (int i = 0; i < condicionalesBreak.length; i++) {
			if (Utilities.isLogic(condicionalesBreak[i])) {
				operators.add(condicionalesBreak[i]);
				conditionals.add(conditional);
				conditional = "";
			} else
				conditional += condicionalesBreak[i];
		}
		if (!conditional.equals(""))
			conditionals.add(conditional);

		if (conditionals.size() - 1 != operators.size())
			throw new IllegalArgumentException("Sentencia WHERE inválida");

		return recursiveEvaluation(indexMap, typeMap, conditionals, operators, 0, line);
	}

	private static boolean recursiveEvaluation(HashMap<String, Integer> ind, HashMap<String, String> type,
			ArrayList<String> conditionals, ArrayList<String> operators, int index, String line) throws Exception {
		String operation = conditionals.get(index);
		String operator = "";
		
		if (operation.contains("<>")) {
			operator = "!=";
			operation = operation.replace("<>", ",");
		} else if (operation.contains("<=")) {
			operator = "<=";
			operation = operation.replace("<=", ",");
		} else if (operation.contains(">=")) {
			operator = ">=";
			operation = operation.replace(">=", ",");
		} else if (operation.contains("<")) {
			operator = "<";
			operation = operation.replace("<", ",");
		} else if (operation.contains(">")) {
			operator = ">";
			operation = operation.replace(">", ",");
		} else if (operation.contains("!=")) {
			operator = "!=";
			operation = operation.replace("!=", ",");
		} else if (operation.contains("=")) {
			operator = "=";
			operation = operation.replace("=", ",");
		}

		if (operator.equals(""))
			throw new IllegalArgumentException("Operador no encontrado en la sentencia WHERE: " + operation);

		String[] operationBreak = operation.split(",");

		if(operationBreak.length != 2)
			throw new IllegalArgumentException("Error en la sentencia WHERE");

		String name = operationBreak[0];
		String value = operationBreak[1];
		String typeValue = type.get(name);

		if (typeValue == null)
			throw new IllegalArgumentException("Columna en el where no encontrada");
		
		if(typeValue.equals("varchar")||typeValue.equals("date"))
			Utilities.isValidString(value);
			

		boolean resultBoolean = false;
		String[] lineBreak = line.split(",");
		
		if(lineBreak[ind.get(name)].equalsIgnoreCase("NULL"))
			return false;
		
		try {
			switch (typeValue) {
				case "int":
					switch (operator) {
						case "=":
							resultBoolean = Integer.parseInt(lineBreak[ind.get(name)]) == Integer.parseInt(value);
							break;
						case "!=":
							resultBoolean = Integer.parseInt(lineBreak[ind.get(name)]) != Integer.parseInt(value);
							break;
						case "<":
							resultBoolean = Integer.parseInt(lineBreak[ind.get(name)]) < Integer.parseInt(value);
							break;
						case ">":
							resultBoolean = Integer.parseInt(lineBreak[ind.get(name)]) > Integer.parseInt(value);
							break;
						case "<=":
							resultBoolean = Integer.parseInt(lineBreak[ind.get(name)]) <= Integer.parseInt(value);
							break;
						case ">=":
							resultBoolean = Integer.parseInt(lineBreak[ind.get(name)]) >= Integer.parseInt(value);
							break;
						default:
							break;
					}
				case "double":
					switch (operator) {
						case "=":
							resultBoolean = Math.round(Double.parseDouble(lineBreak[ind.get(name)]) * 10000000.0)
									/ 10000000.0 == Math.round(Double.parseDouble(value) * 10000000.0) / 10000000.0;
							break;
						case "!=":
							resultBoolean = Math.round(Double.parseDouble(lineBreak[ind.get(name)]) * 10000000.0)
									/ 10000000.0 != Math.round(Double.parseDouble(value) * 10000000.0) / 10000000.0;
							break;
						case "<":
							resultBoolean = Double.parseDouble(lineBreak[ind.get(name)]) < Double.parseDouble(value);
							break;
						case ">":
							resultBoolean = Double.parseDouble(lineBreak[ind.get(name)]) > Double.parseDouble(value);
							break;
						case "<=":
							resultBoolean = Math.round(Double.parseDouble(lineBreak[ind.get(name)]) * 10000000.0)
									/ 10000000.0 <= Math.round(Double.parseDouble(value) * 10000000.0) / 10000000.0;
							break;
						case ">=":
							resultBoolean = Math.round(Double.parseDouble(lineBreak[ind.get(name)]) * 10000000.0)
									/ 10000000.0 >= Math.round(Double.parseDouble(value) * 10000000.0) / 10000000.0;
							break;
						default:
							break;
					}
				case "float":
					switch (operator) {
						case "=":
							resultBoolean = Math.round(Float.parseFloat(lineBreak[ind.get(name)]) * 10000000.0)
									/ 10000000.0 == Math.round(Float.parseFloat(value) * 10000000.0) / 10000000.0;
							break;
						case "!=":
							resultBoolean = Math.round(Float.parseFloat(lineBreak[ind.get(name)]) * 10000000.0)
									/ 10000000.0 != Math.round(Float.parseFloat(value) * 10000000.0) / 10000000.0;
							break;
						case "<":
							resultBoolean = Math.round(Float.parseFloat(lineBreak[ind.get(name)]) * 10000000.0)
									/ 10000000.0 < Math.round(Float.parseFloat(value) * 10000000.0) / 10000000.0;
							break;
						case ">":
							resultBoolean = Math.round(Float.parseFloat(lineBreak[ind.get(name)]) * 10000000.0)
									/ 10000000.0 > Math.round(Float.parseFloat(value) * 10000000.0) / 10000000.0;
							break;
						case "<=":
							resultBoolean = Math.round(Float.parseFloat(lineBreak[ind.get(name)]) * 10000000.0)
									/ 10000000.0 <= Math.round(Float.parseFloat(value) * 10000000.0) / 10000000.0;
							break;
						case ">=":
							resultBoolean = Math.round(Float.parseFloat(lineBreak[ind.get(name)]) * 10000000.0)
									/ 10000000.0 >= Math.round(Float.parseFloat(value) * 10000000.0) / 10000000.0;
							break;
						default:
							break;
					}
				case "varchar":
					switch (operator) {
						case "=":
							resultBoolean = lineBreak[ind.get(name)].equals(value);
							break;
						case "!=":
							resultBoolean = !lineBreak[ind.get(name)].equals(value);
							break;
						case "<=":
							resultBoolean = lineBreak[ind.get(name)].compareTo(value) <= 0;
							break;
						case ">=":
							resultBoolean = lineBreak[ind.get(name)].compareTo(value) >= 0;
							break;
						case "<":
							resultBoolean = lineBreak[ind.get(name)].compareTo(value) < 0;
							break;
						case ">":
							resultBoolean = lineBreak[ind.get(name)].compareTo(value) > 0;
							break;
						default:
							break;
					}
				default:
					break;
				case "date":
					switch (operator) {
						case "=":
							resultBoolean = lineBreak[ind.get(name)].equals(value);
							break;
						case "!=":
							resultBoolean = !lineBreak[ind.get(name)].equals(value);
							break;
						case "<=":
							resultBoolean = lineBreak[ind.get(name)].compareTo(value) <= 0;
							break;
						case ">=":
							resultBoolean = lineBreak[ind.get(name)].compareTo(value) >= 0;
							break;
						case "<":
							resultBoolean = lineBreak[ind.get(name)].compareTo(value) < 0;
							break;
						case ">":
							resultBoolean = lineBreak[ind.get(name)].compareTo(value) > 0;
							break;
						default:
							break;
					}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Error de tipado en la sentencia WHERE");
		}
		
		if (index < operators.size())
			if (operators.get(index).equalsIgnoreCase("AND"))
				return resultBoolean && recursiveEvaluation(ind, type, conditionals, operators, ++index, line);
			else
				return resultBoolean || recursiveEvaluation(ind, type, conditionals, operators, ++index, line);

		return resultBoolean;
	}

	/**
	 * Argumentos
	 * 
	 * @param condicionales trae todos los condicionales de la sentencia where
	 * @param line trae la línea actual que se va a evaluar
	 * @param headers trae los headers de la tabla
	 * @param lineBreak trae la línea actual separada por comas
	 * @param table trae la tabla actual
	 * @return
	 * @throws Exception
	  */
	public static boolean newWhere(String condicionales, ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table, String lastCall) throws Exception {
		if (condicionales.equals(""))
			return true;

		if(condicionales.equalsIgnoreCase(lastCall))
			throw new IllegalArgumentException("Error en la sentencia WHERE: " + condicionales);

		condicionales = condicionales.trim();

		//if(condicionales.startsWith("(")&&condicionales.endsWith(")"))
		//	condicionales = condicionales.substring(1, condicionales.length()-1);

		String stringToProcess = "", actualString = "";
		
		String[] conditionalsBreak = condicionales.split(" ");
		for (int i = 0; i < conditionalsBreak.length; i++) {
			actualString += conditionalsBreak[i] + " ";
			if(hasValidParenthesis(actualString)) {
				if(actualString.equals(conditionalsBreak[i] + " ")){
					stringToProcess += conditionalsBreak[i] + " ";
				} else {
					actualString = actualString.trim();
					if(actualString.startsWith("(")&&actualString.endsWith(")"))
						actualString = actualString.substring(1, actualString.length()-1);
					
					stringToProcess+=Boolean.toString(newWhere(actualString, headers, lineBreak, table, condicionales)) + " ";
				}
				actualString = "";
			} 
		}
		Queue<String> operators = new LinkedList<String>();
		Queue<String> conditionals = new LinkedList<String>();

		String[] stringToProcess2 = stringToProcess.split(" ");
		
		String str = "";
		for (int i = 0; i < stringToProcess2.length; i++) {
			if(Utilities.isLogic(stringToProcess2[i])) {
				operators.add(stringToProcess2[i].trim());
				conditionals.add(str.trim());
				str = "";
			} else 
				str += stringToProcess2[i] + " ";
		}
		if(!str.equals(""))
			conditionals.add(str.trim());

		if(conditionals.size() - 1 != operators.size())
			throw new IllegalArgumentException("Sentencia WHERE inválida: Los operadores no coinciden con los condicionales");

		return evalFunction(headers, lineBreak, table, operators, conditionals);
	}

	@SuppressWarnings("unused")
	public static boolean evalFunction(ArrayList<Header> headers, String[] lineBreak, ArrayList<String> table, Queue<String> operators, Queue<String> conditionals) throws Exception {
		String comparator = "";

		String conditional = conditionals.poll();

		if(conditional.startsWith("(")&&conditional.endsWith(")"))
			conditional = conditional.substring(1, conditional.length()-1);
		
		// * Recursive call when i already solved a parenthesis before in the recursive calls
		if(conditional.equalsIgnoreCase("TRUE")||conditional.equalsIgnoreCase("FALSE")){
			if(operators.isEmpty())
				return Boolean.parseBoolean(conditional);
			
			String operator = operators.poll();
			if(operator.equalsIgnoreCase("AND"))
				return Boolean.parseBoolean(conditional) && evalFunction(headers, lineBreak, table, operators, conditionals);
			else
				return Boolean.parseBoolean(conditional) || evalFunction(headers, lineBreak, table, operators, conditionals);
		}

		// ? Handle the different comparators
		String[] parts;
		if(conditional.contains("<>")) {
			comparator = "<>";
			conditional = conditional.replace("<>", ",");
		} else if(conditional.contains("<=")) {
			comparator = "<=";
			conditional = conditional.replace("<=", ",");
		} else if(conditional.contains(">=")) {
			comparator = ">=";
			conditional = conditional.replace(">=", ",");
		} else if(conditional.contains("<")) {
			comparator = "<";
			conditional = conditional.replace("<", ",");
		} else if(conditional.contains(">")) {
			comparator = ">";
			conditional = conditional.replace(">", ",");
		} else if(conditional.contains("!=")) {
			comparator = "!=";
			conditional = conditional.replace("!=", ",");
		} else if(conditional.contains("=")) {
			comparator = "=";
			conditional = conditional.replace("=", ",");
		} else {
			throw new IllegalArgumentException("Operador no encontrado en la sentencia WHERE: '" + conditional + "'");
		}

		parts = conditional.split(",");

		if(parts.length != 2)
			throw new IllegalArgumentException("Error en la sentencia WHERE: " + conditional + comparator);

		String firstPart = Eval.eval(parts[0].trim(), headers, lineBreak, table);
		String secondPart = Eval.eval(parts[1].trim(), headers, lineBreak, table);

		// ? Handle the case when one of the parts is NULL
		if(firstPart.equalsIgnoreCase("NULL")||secondPart.equalsIgnoreCase("NULL")){
			if(operators.isEmpty())
				return false;
			String operatorStr = operators.poll();
			if(operatorStr.equalsIgnoreCase("OR"))
				return false || evalFunction(headers, lineBreak, table, operators, conditionals);
			else if(operatorStr.equalsIgnoreCase("AND"))
				return false && evalFunction(headers, lineBreak, table, operators, conditionals);
			else 
				throw new IllegalArgumentException("Error en la sentencia WHERE: " + conditional + comparator);
		}

		boolean resultBoolean = false;
		switch (comparator) {
			case "=":
				try {
					resultBoolean = Double.parseDouble(firstPart) == Double.parseDouble(secondPart);
				} catch(NumberFormatException e) {
					resultBoolean = firstPart.equals(secondPart);
				}
				break;
			case "!=":
				try {
					resultBoolean = Double.parseDouble(firstPart) != Double.parseDouble(secondPart);
				} catch(NumberFormatException e) {
					resultBoolean = !firstPart.equals(secondPart);
				}
				break;
			case "<=":
				try {
					resultBoolean = Double.parseDouble(firstPart) <= Double.parseDouble(secondPart);
				} catch(NumberFormatException e) {
					resultBoolean = firstPart.compareTo(secondPart) <= 0;
				}
				break;
			case ">=":
				try {
					resultBoolean = Double.parseDouble(firstPart) >= Double.parseDouble(secondPart);
				} catch(NumberFormatException e) {
					resultBoolean = firstPart.compareTo(secondPart) >= 0;
				}
				break;
			case "<":
				try {
					resultBoolean = Double.parseDouble(firstPart) < Double.parseDouble(secondPart);
				} catch(NumberFormatException e) {
					resultBoolean = firstPart.compareTo(secondPart) < 0;
				}
				break;
			case ">":
				try {
					resultBoolean = Double.parseDouble(firstPart) > Double.parseDouble(secondPart);
				} catch(NumberFormatException e) {
					resultBoolean = firstPart.compareTo(secondPart) > 0;
				}
				break;
			case "<>":
				try {
					resultBoolean = Double.parseDouble(firstPart) != Double.parseDouble(secondPart);
				} catch(NumberFormatException e) {
					resultBoolean = !firstPart.equals(secondPart);
				}
				break;
			default:
				break;
		}

		if(operators.isEmpty())
			return resultBoolean;
		
		String operator = operators.poll();
		if(operator.equalsIgnoreCase("AND"))
			return resultBoolean && evalFunction(headers, lineBreak, table, operators, conditionals);
		else
			return resultBoolean || evalFunction(headers, lineBreak, table, operators, conditionals);
	}

	// Aux Function
	public static boolean hasValidParenthesis(String arg){
        int ctr = 0;

        for (int i = 0; i < arg.length(); i++) {
            if(arg.charAt(i) == '(') ctr++;
            if(arg.charAt(i) == ')') ctr--;

            if(ctr < 0) return false;
        }

        return ctr == 0;
    }
}
