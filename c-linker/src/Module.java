import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author adithepnarula
 * Collaborated with Adisa Narula and Avika Narula. 
 * We designed our program together but all the code/implementation was by me. 
 */
public class Module {
	
	private ArrayList<Symbol> symbols;
	public HashMap<Integer, String> useList;
	private int[] programText;
	private int baseAddress;
	
	private int countND;
	private int countNU;
	private int prgTextCount;
	private ArrayList<Integer> memoryMap;
	private HashMap<String, Integer> multipleSymbolsList;
	
	/**
	 * no-arg constructor
	 */
	public Module(){
		this.symbols = new ArrayList<Symbol>();
		this.useList = new HashMap();
		this.memoryMap = new ArrayList<Integer>();
		this.multipleSymbolsList = new HashMap<String, Integer>();
		
	}
	
	
	/**
	 * returns hashmap representation of use list
	 * @return 
	 */
	public HashMap<Integer, String> getUseList(){
		return this.useList;
	}
	/**
	 * return the memory map, which is absolute address of program instruction
	 * @return
	 */
	public ArrayList<Integer> getMemoryMap(){
		return this.memoryMap;
	}

	/**
	 * Return string representation of symbol given its relative postiion
	 * @param relativePos relative position of symbol
	 * @return string symbol representation
	 */
	public String getExSymbolAd(int relativePos) {
		String symbol = this.useList.get(relativePos);
		return symbol;
	}
	/**
	 * adds an address to the memory map 
	 * @param address integer of absolute address
	 */
	public void addMemoryMap(int address){
		this.memoryMap.add(address);
	}
	
	/**
	 * return program text as an integer array
	 * @return program text as an integer array
	 */
	public int[] getProgramText(){
		return this.programText;
	}
	
	/**
	 * return array list of symbols 
	 * @return array list of symbols
	 */
	public ArrayList<Symbol> getSymbols(){
		return this.symbols;
	}
	
	/**
	 * Set program text
	 * @param prgText int array of program text
	 */
	public void setPrgText(int[] prgText){
		this.programText = prgText;
	}
	
	/**
	 * add external symbol to uselist given relative address and the symbol
	 * @param relAd relative address of symbol
	 * @param key string representation of the symbol
	 */
	public void addExSymbols(int relAd, String key){
		this.useList.put(relAd, key);
		String symbol = this.useList.get(relAd);
	}
	/**
	 * add a symbol of symbols
	 * @param symbol symbol object
	 */
	public void addSymbols(Symbol symbol) {
		this.symbols.add(symbol);
	}
	/**
	 * return count of definition in a  module 
	 * @return int count of definition in a module
	 */
	public int getCountND() {
		return countND;
	}
	/**
	 * set number of definition
	 * @param countND number of definition
	 */
	public void setCountND(int countND) {
		this.countND = countND;
	}
	/**
	 * get count of use list symbols
	 * @return count of external symbols in use list
	 */
	public int getCountNU() {
		return countNU;
	}
	/**
	 * set count of use list symbols
	 * @param countNU number of use list symbols
	 */
	public void setCountNU(int countNU) {
		this.countNU = countNU;
	}
	/**
	 * return number of program text in module
	 * @return number of project text in module
	 */
	public int getPrgTextCount() {
		return prgTextCount;
	}
	/**
	 * set program text count
	 * @param prgTextCount program text count
	 */
	public void setPrgTextCount(int prgTextCount) {
		this.prgTextCount = prgTextCount;
	}
	/**
	 * return base address of module 
	 * @return base address of module
	 */
	public int getBaseAddress() {
		return baseAddress;
	}
	
	/**
	 * set base address of module 
	 * @param baseAddress base address of module
	 */
	public void setBaseAddress(int baseAddress) {
		this.baseAddress = baseAddress;
	}

	/**
	 * This method returns Hashmap representation of symbol list 
	 * @return hasmap representation of symbol list
	 */
	public HashMap<String, Integer> getMultipleSymbolsList() {
		return this.multipleSymbolsList;
	}
	/**
	 * add symbols to the multiple symbols list
	 * @param symbol symbol object
	 * @param bool true if multiply used
	 */
	public void addMultipleSymbolsList(String symbol, int bool) {
		this.multipleSymbolsList.put(symbol, bool);
	}
	/**
	 * check if symbol been multiply used 
	 * @param symbol symbol object
	 * @return int decided as boolean value later in program 
	 */
	public int checkMultipleSymbolsList(String symbol) {
		return this.multipleSymbolsList.get(symbol);
	}

}
