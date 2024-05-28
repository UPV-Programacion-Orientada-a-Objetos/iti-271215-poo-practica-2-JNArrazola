package edu.upvictoria.poo;

import java.util.ArrayList;

import java.util.Queue;
import java.util.LinkedList;



public class Where {

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

		String[] processBrk = condicionales.split(" ");

		String actualStr = "";

		// ? Line to handle unwanted spaces
		for(int i = 0; i < processBrk.length; i++){
			if(processBrk[i].equalsIgnoreCase("OR")||processBrk[i].equalsIgnoreCase("AND")){
				actualStr += " ";
				actualStr += processBrk[i];
				actualStr += " ";
			}
			else{
				if(processBrk[i].equals(" "))
					continue;
				actualStr += processBrk[i];
			}
		}
		condicionales = actualStr.trim();
		// ? End of handling unwanted spaces

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
