/**
 * 
 * @author adithepnarula
 * Collaborated with Adisa Narula and Avika Narula. 
 * We designed the structure of our program together but all the code/implementation was done by me solely. 
 */
public class Symbol {
	
	private int absoluteAddress;
	private String symbol;
	private boolean multiplyDefined;
	private boolean relativeExceed;
	private boolean gotUsed;
	private int moduleDefined;
	private boolean multiplyListedUseList;
	
	/**
	 * constructor
	 * @param symbol symbol object
	 * @param absoluteAddress absolute address of the symbol
	 */
	public Symbol(String symbol, int absoluteAddress) {
		this.absoluteAddress = absoluteAddress;
		this.symbol = symbol;
		this.multiplyDefined = false;
		this.relativeExceed = false;
	}
	
	/**
	 * sets whether symbol was already used once, for error checking
	 * @param gotUsed boolean value
	 */
	public void setGotUsed(boolean gotUsed){
		this.gotUsed = gotUsed;
	}
	
	/**
	 * getter which returns gotUsed
	 * @return boolean value gotUsed
	 */
	public boolean getGotUsed(){
		return this.gotUsed;
	}
	
	/**
	 * set boolean value whether relative address of symbol has exceed module size, for error checking
	 * @param relativeExceed
	 */
	public void setRelativeExceed(boolean relativeExceed){
		this.relativeExceed = relativeExceed;
		
	}
	/**
	 * getter method returning boolean value of relativeExceed
	 * @return boolean value relativeExceed
	 */
	public boolean getRelativeExceed(){
		return this.relativeExceed;
	}

	public void setMultiplyDefined(boolean multiplyDefined) {
		this.multiplyDefined = multiplyDefined;
	}
	
	/**
	 * return boolean value whether symbol has been multiply defined
	 * @return boolean multipleDefined
	 */
	public boolean getMultiplyDefined(){
		return this.multiplyDefined;
	}
	
	/**
	 * return absolute address of symbol
	 * @return int absolute address of symbol
	 */
	public int getAbsoluteAddress() {
		return absoluteAddress;
	}

	/**
	 * set absolute address of symbol
	 * @param absoluteAddress int which reprsents absolute address
	 */
	public void setAbsoluteAddress(int absoluteAddress) {
		this.absoluteAddress = absoluteAddress;
	}

	/**
	 * return string representation of the symbol
	 * @return string representation of the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * set string representation of the symbol
	 * @param symbol a string to set symbol
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	/**
	 * return int value whether it has been defined in this module
	 * @return int value whether symbol has been defined in module. 0 means has not been defined in module. 1 means defined in module. 
	 */
	public int getModuleDefined() {
		return moduleDefined;
	}

	/**
	 * set value to 1 if symbol has been defined in module, otherwise 0.
	 * @param moduleDefined integer to set value of moduleDefined.
	 */
	public void setModuleDefined(int moduleDefined) {
		this.moduleDefined = moduleDefined;
	}

	/**
	 * return boolean value whether symbol is multiply listed in used list
	 * @return true if multiply listed in useList and false otherwise
	 */
	public boolean isMultiplyListedUseList() {
		return multiplyListedUseList;
	}

	/**
	 * set whether symbol has been multiply listed in use list
 	 * @param multiplyListedUseList boolean value whether symbol has been multiply listed
	 */
	public void setMultiplyListedUseList(boolean multiplyListedUseList) {
		this.multiplyListedUseList = multiplyListedUseList;
	}

}
