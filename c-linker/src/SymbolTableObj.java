import java.util.HashMap;

/**
 * 
 * @author adithepnarula
 * Collaborated with Adisa Narula and Avika Narula. 
 * We designed our program together but all the code/implementation was by me. 
 */

public class SymbolTableObj {
	
	private HashMap<String, Symbol> symbolTable;
	
	/**
	 * Constructor that initializes the HashMap symbol table
	 */
	public SymbolTableObj(){
		symbolTable = new HashMap();
	}
	/**
	 * This method adds symbol to hashmap
	 * @param stringRepSymbol string representation of the symbol
	 * @param symbol symbol object
	 */
	public void addSymbol(String stringRepSymbol, Symbol symbol){
		this.symbolTable.put(stringRepSymbol, symbol);
	}
	
	/**
	 * This method returns symbol object given string representation of the object 
	 * as input
	 * @param symbolString string representation of the symbol
	 * @return corresponding symbol object
	 */
	public Symbol getSymbol(String symbolString) {
		Symbol symbol = symbolTable.get(symbolString);
		return symbol;
	}
	/**
	 * Return Hashmap symbol talbe
	 * @return
	 */
	public HashMap<String,Symbol> getSymbolTable(){
		return this.symbolTable;
	}
	
	/**
	 * Prints symbol table
	 */
	public void printSymTab(){
		System.out.println(symbolTable.entrySet());
	}
	

}
