import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 
 * @author adithepnarula
 * Collaborated with Adisa Narula and Avika Narula. 
 * We designed our program together but all the code/implementation was by me. 
 */

public class LinkerV2 {
	
	public static void main(String[] args){
		
		String fileText = readFile(args);
		
		
		//pass 1
		//create an array list of initialized modules
		ArrayList<Module> modulesArray = createModulesArray(fileText);

		
		//create symbol table
		SymbolTableObj symbolTable = createSymbolTable(modulesArray);
		
		System.out.println("Symbol Table");
		printSymbolTable(symbolTable);
		System.out.println();
	
		System.out.println("Memory Map");
		
		//pass 2
		passTwo(modulesArray, symbolTable);
		
		//print symbols that did not get used
		printGotUsed(symbolTable);
		
		//print smybols in uselist that exceed size of module
		printSymbolsExceedUseList(modulesArray);
		

		
	}
	
	/**
	 * This method prints symbols with relative address of symbol exceed module size
	 * @param modulesArray all the modules located in array
	 */
	public static void printSymbolsExceedUseList(ArrayList<Module> modulesArray){
		
		//for each module get the use list 
		for (int i = 0; i < modulesArray.size(); i++) {
			
			//uselist
			HashMap<Integer, String> useList = modulesArray.get(i).useList;
			
			//get size of module - 1
			int sizeModule = modulesArray.get(i).getPrgTextCount() - 1;
			
			//loop through uselist to check which relative address exceed then print
			for(Integer key: useList.keySet()) {
				
				//if relative address exceeds size
				if (key > sizeModule) {
					System.out.println("Error: Use of " + useList.get(key) + " in module "
							+ i + " exceeds module size; use ignored");
				}
				
			}
			
		}
		
	}
	
	/**
	 * This method prints symbol that has been defined but not used 
	 * @param symTabObj symbol table object
	 */
	public static void printGotUsed(SymbolTableObj symTabObj) {
		System.out.println();
		HashMap<String, Symbol> symTab = symTabObj.getSymbolTable();
		//loop through all the symbols in the symbol table
		for (String key: symTab.keySet()) {
			Symbol symbol = symTab.get(key);
			//if symbol never got used
			if(!symbol.getGotUsed()) {
				System.out.println("Warning: " + symbol.getSymbol() + 
						" was defined in module " + symbol.getModuleDefined() + " but never used.");
			}
		}//end for
		
	}
	
	/**
	 * This method prints symbol table
	 * @param symTable symbol table object
	 */
	public static void printSymbolTable(SymbolTableObj symTable){
		HashMap<String,Symbol> symTab = symTable.getSymbolTable();
		for (String key: symTab.keySet()) {
			Symbol symbol = symTab.get(key);
			System.out.print(key + " = " + symbol.getAbsoluteAddress() + " ");
			if (symbol.getMultiplyDefined()) {
				System.out.print(" Error: This variable is multiply defined; last value used.");
			}
			if (symbol.getRelativeExceed()) {
				System.out.print(" Error: Definition exceeds module size; last wod in module used.");
			}
			System.out.println();
		}
		
		
	}
	
	/**
	 * This method prints the final result (but it has not been used)
	 * @param modules
	 */
	public static void printMemoryMap(ArrayList<Module> modules) {
		int count = 0;
		
		
		//for each module, get its memory map and print
		
		for (int i = 0; i < modules.size(); i++){
			
			//get memory map for each module
			
			ArrayList<Integer> map = modules.get(i).getMemoryMap();
			
			for (int j = 0; j < map.size(); j++) {
			
				int address = map.get(j);
				System.out.println(address);
				count ++;
				
				
			}//end for
		}//end for
	}
	
	/**
	 * This method checks if the given 5-digit address ends with 2
	 * and if it does, it checks whether absolute address exceeds size of 
	 * machine
	 * @param number absolute address
	 * @return boolean value if 5-digit address exceeds machine size of 300
	 */
	public static boolean checkAbsExceedSize(int number){
		//convert to string for easy processing
		boolean exceedFlag = false;
		String numTemp = ""+number;
		String threeDigitsStr = numTemp.substring(1, 4);
		int threeDigitsInt = Integer.parseInt(threeDigitsStr);
		if (threeDigitsInt >= 300 && numTemp.endsWith("2")) {
			exceedFlag = true;
		}
		
		return exceedFlag;
		
		
	}
	
	/**
	 * This method reads the file and parse them into a string
	 * @param args arguments entered in command line 
	 * @return string representation of the file
	 */
	public static String readFile(String[] args) {

		//validate existence of command line arguments
		if (args.length < 1 ) {
			System.err.printf("Error: invalid number of arguments.\n");
			System.exit(1);
		}


		//verify existence of the input file
		File inputFile = new File(args[0]);

		if (!inputFile.canRead()) {
			System.err.printf("Error: cannot read the input file %s\n\n", args[0]);
			System.exit(1);
		}


		//open the dictionary file for reading
		Scanner inputIn = null;
		try {
			inputIn = new Scanner(inputFile);
		} catch (FileNotFoundException e) {
			System.err.printf("Error: cannot read the input file %s\n\n",args[0]);
			System.exit(1);
		}
		
		String fileText = "";
		while (inputIn.hasNext()) {
			//read in countND and convert to integer
			String countNDString = inputIn.next();
			int countND = Integer.parseInt(countNDString);
			
			
			//Deflist String
			String defList = countND + " ";
			
			//read in (S,R)
			for (int i = 0; i < countND; i++) {
				String symbol = inputIn.next();
				String relAd = inputIn.next();
				defList = defList + symbol + " " + relAd + " ";
					//NOTE: last open quotes, may need to fix
			}
			
			
			//add nextline character for later processing
			defList += "\n";
			
			//read in countNU and convert to integer
			String countNUString = inputIn.next();
			int countNU = Integer.parseInt(countNUString);
			
			//useList String
			String useList = countNU + " ";
			
			//read in (S,U)
			//for each symbol, read until -1
			for (int i = 0; i < countNU; i++) {
				String symbol = inputIn.next();
				useList = useList + symbol + " ";
				
				//cannot be negative if at least one symbol exist
				String relAd = inputIn.next();
				
				//check if its -1
				while (!relAd.equals("-1")){ 
					useList = useList + relAd + " ";
					relAd = inputIn.next();
				}
				
				//insert -1 
				useList = useList + "-1 ";
			
				//Note: last open qoutes, may need to fix
			}
			
			//add nextline character for later processing
			useList += "\n";
			
			//read in 5 digit numbers
			String countPrgtextString = inputIn.next();
			int countPrgText = Integer.parseInt(countPrgtextString);
			
			//prg text string
			String prgText = countPrgText + " ";
			
			//read in 5 digit number
			for (int i = 0; i < countPrgText; i++) {
				String number = inputIn.next();
				prgText = prgText + number + " ";
			}
			
			//add nextline character for later processing
			prgText += "\n";
			
			fileText += defList + useList + prgText; 
		}
		
		return fileText;
		
	}
	
	/**
	 * Takes an input of file as a string and converts into an array list of with each element as a module object
	 * @param fileText string representation of the file 
	 * @return array list of modules
	 */
	public static ArrayList<Module> createModulesArray(String fileText){
		//split into different lines
		String[] moduleFileArray = fileText.split("\n");
		ArrayList<Module> modulesArray = new ArrayList<Module>();
		//find number of modules
		int numModules = moduleFileArray.length/3;
		
		//base address for first module is 0
		int baseAddress = 0;
		
		//initialize each module
		int k = 0;
		for (int i = 0; i < moduleFileArray.length; i=i+3) {
		
			Module module = initializeModule(moduleFileArray[i], moduleFileArray[i+1], moduleFileArray[i+2], baseAddress);
			modulesArray.add(module);
			int moduleLength = module.getPrgTextCount();
		
			baseAddress = baseAddress + moduleLength;
		
			k++;
			
		}//end for
		
		return modulesArray;
	}
	/**
	 * This method creates symbol table object from array of modules
	 * @param modulesArray array of modules
	 * @return symbol table object
	 */
	public static SymbolTableObj createSymbolTable(ArrayList<Module> modulesArray){
		
		SymbolTableObj symbolTable = new SymbolTableObj();
		
		for (int i = 0; i < modulesArray.size(); i++){
			//get a module
			Module module = modulesArray.get(i);
			//get all the symbols in the module
			ArrayList<Symbol> symbols = module.getSymbols();
			
			
			//for each symbol, add it to the symbol string and symbol object
			for (int k = 0; k < symbols.size(); k++) {
				Symbol symbolObj = symbols.get(k);
				String symbolString = symbolObj.getSymbol();
				
				//notify symbol which module its in
				symbolObj.setModuleDefined(i);
				
				//check whether symbol is multiply defined by going to SymbolTable
				//and checking if key already exist
				if (symbolTable.getSymbolTable().containsKey(symbolString)) {
					//true, key exist
					symbolObj.setMultiplyDefined(true);
				}
				
				symbolTable.addSymbol(symbolString, symbolObj);
			}//end for
		}//end for
		
		return symbolTable;
	}
	
	/**
	 * This method initializes module object
	 * @param defList definition list as a string
	 * @param useList use list as string
	 * @param prgText program text as a string
	 * @param baseAddress base address for the current module as an int 
	 * @return module object
	 */
	public static Module initializeModule(String defList, String useList, String prgText, int baseAddress) {
		Module module = new Module();
		
		//step0: set base address
		module.setBaseAddress(baseAddress);
		
		//step1: initialize definition list
		//Go to definition list, get the first element which is the number of symbols 
		String[] defListArray = defList.split(" ");
		int countND = Integer.parseInt(defListArray[0]);
		module.setCountND(countND);
		
		//get the definition list and split it
		//note: def list not needed pass 2
		//if a symbol is defined in this module 
		if (countND > 0){
			
			//first element is count, so start at the second element
			for (int x = 1; x < defListArray.length; x = x + 2) {
				
				//get a symbol
				String symbolString = defListArray[x];
				
				//get its relative position definition next to the symbol
				int relAddressDef = Integer.parseInt(defListArray[x+1]);
				
				
				//check ERROR: if address in def exceeds size of module
				//create flag
				boolean relExceed = false;
				
				{
					String[] prgTextArrayStr = prgText.split(" ");
					int prgTextCount = Integer.parseInt(prgTextArrayStr[0]);
					if (relAddressDef > prgTextCount-1) {	
						relExceed = true;
						relAddressDef = prgTextCount - 1;
					}
					
				}
				
				//covert to absolute address and add it to symbol table
				int absAddressDef = relAddressDef + baseAddress;
				
				//create new symbol object
				Symbol symbol = new Symbol(symbolString, absAddressDef);
				//check whether relExceeds
				if (relExceed){
					symbol.setRelativeExceed(true);
				}
			
				module.addSymbols(symbol);
		
				
				
			}//end for
			
		}
		
		//step2: initialize use list 
		//get use list and split it
		String[] useListArray = useList.split(" ");
		//go to uselist and get countNU
		int countNU = Integer.parseInt(useListArray[0]);
		
		
		module.setCountNU(countNU);
	
		
		
		//if use list exist
		if (countNU > 0) {
			
			for (int i = 1; i < useListArray.length; i++){
				
				//get first external symbol, if its a letter then its a symbol
				char symbolChar = useListArray[i].charAt(0);
				
				//safeguard - test whether it is a symbol or a relative address
				if (Character.isLetter(symbolChar)) {
					
					//get symbol
					String symbolString = useListArray[i];
					//get symbol's relative address
					int nextLetter = Integer.parseInt(useListArray[i+1]);
					
					//check whether there is a symbol defined at the rel address
					HashMap<Integer, String> useListTemp = module.getUseList();
					if(useListTemp.containsKey(nextLetter)) {
						//go to module hashmap and mark latest symbol as multiply used on same instruction
						//check in pass 2 if symbol is in array
						module.addMultipleSymbolsList(symbolString, 1);
					}
					else{
						module.addMultipleSymbolsList(symbolString, 0);
					}
					
					//while terminating value is not reached
					while(nextLetter != -1){
				
						//add symbol to use list
						module.addExSymbols(nextLetter, symbolString);
					
						//move to next letter
						i++;
						nextLetter = Integer.parseInt(useListArray[i+1]);
					
					}//end while
					
					
					
				}//end if
						
				
			}//end for
		}
		
		//step3: initialize program text
	
		String[] prgTextArrayStr = prgText.split(" ");
		int prgTextCount = Integer.parseInt(prgTextArrayStr[0]);
		module.setPrgTextCount(prgTextCount);
		
		int[] prgTextArrayInt = new int[prgTextArrayStr.length-1];
		
		//copy every element except count and convert to int
		for (int i = 1; i < prgTextArrayStr.length; i++) {
			prgTextArrayInt[i-1] = Integer.parseInt(prgTextArrayStr[i]); 
		}
		
		module.setPrgText(prgTextArrayInt);
		
		//step4: return module
		return module;
	}
	

	/**
	 * This method runs pass two and prints out the result
	 * @param modulesArray array of modules 
	 * @param symbolTableObj symbol rable object
	 */
	public static void passTwo(ArrayList<Module> modulesArray, SymbolTableObj symbolTableObj){
		
		
		//for each module get the program text, count the position of the 5 digit number
		//if digit ends with 3 or 4, find matching relative address of 5 digit number then
		//either relocate or resolve 
		
		int countLine = 0;
		
		for (int i = 0; i < modulesArray.size(); i ++) {
			//get a module from array
			Module module = modulesArray.get(i);
			
			//get program text from each module
			int programTextArray[] = module.getProgramText();
			
			//reset relative address to 0 for each module
			int relativeCount = 0;
			
			//for each 5-digit number in a module do:
			for (int j = 0; j < programTextArray.length; j++) {
				
				//convert to string for easy processing
				String numTemp = programTextArray[j] + "";
				
				
				if (numTemp.endsWith("2")) {
					//print out first 4 digits
					String first4digitsTemp = numTemp.substring(0, numTemp.length() - 1);
					boolean exceedFlag = false;
					//check whether abs address exceeds the size of machine of 300 words
					String addressFieldStr = numTemp.substring(1, 4);
					int absAddress;
					int addressFieldInt = Integer.parseInt(addressFieldStr);
					//address field exceeds max size
					if (addressFieldInt >= 300) {
						addressFieldInt = 299;
						String firstDigit = numTemp.substring(0, 1);
						String first4Digits = firstDigit + addressFieldInt;
						absAddress = Integer.parseInt(first4Digits);
						exceedFlag = true;
					}
					//address field is OK
					else {
						
						absAddress = Integer.parseInt(first4digitsTemp);
					}
					System.out.print(countLine + ": " + absAddress);
					if (exceedFlag) {
						System.out.print(" Error: Absolute address exceeds machine size; largest address used");
					}
					System.out.println();
					module.addMemoryMap(absAddress);
					
				}
				
				
				else if (numTemp.endsWith("3")){
					//declare needed variables
					int absAddress;
					String first4digitsTemp;
					int first4digits;
					//check whether relative address exceeds size of module
					boolean exceedRelFlag = checkRelExceedModule(numTemp, module.getPrgTextCount());
					if (exceedRelFlag) {
						//get size of module - 1 and replace it with address field
						int lastAddress = module.getPrgTextCount() - 1;
						String firstLetter = numTemp.substring(0,1);
						String midAddress = "";
						
						//check how many digits is last address
						//if last address has 1 digit add two zeros
						if (lastAddress < 10) {
							midAddress = "00";
						}
						
						else if (lastAddress >= 10 && lastAddress < 100) {
							midAddress = "0";
						}
						
						else if (lastAddress >= 100 & lastAddress < 1000) {
							midAddress = "";
						}
						
						first4digitsTemp = firstLetter + midAddress + lastAddress;
						first4digits = Integer.parseInt(first4digitsTemp);
					}
					
					else {
						first4digitsTemp = numTemp.substring(0, numTemp.length() - 1);
						first4digits = Integer.parseInt(first4digitsTemp);
					}
					
					//get absolute address
					absAddress = first4digits + module.getBaseAddress();
					System.out.print(countLine + ": " + absAddress);
					if (exceedRelFlag) {
						System.out.print(" Error: Relative address exceeds module size; largest module address used");
					}
					System.out.println();					
					module.addMemoryMap(absAddress);
				}
				
				//resolve external symbol
				else if (numTemp.endsWith("4")) {
					String first4digitsTemp = numTemp.substring(0, numTemp.length() - 1);
					
					int first4digits = Integer.parseInt(first4digitsTemp);
					//since it is an external symbol, search hashmap in uselist using current relative position to get String representation of symbol
					String symbolString = module.getExSymbolAd(relativeCount);
					
					
					//use string representation in symbol table to get symbol object
					Symbol symbol = symbolTableObj.getSymbol(symbolString);
					
					//ERROR1: mark symbol got used
					//ERROR2: symbol defined but not used
					boolean errorFlag = false;
					try {
						symbol.setGotUsed(true);
					}
					catch (NullPointerException e) {
						System.out.println(countLine +":" +" 1111 Error: " + symbolString + " is not defined; 111 used.");
						errorFlag = true;
						int absAddress = 1111;
						module.addMemoryMap(absAddress);
						relativeCount++;
						countLine++;
						continue;
					}
					
				
					//absolute address of symbol
					int tempAbsAdd = symbol.getAbsoluteAddress();
					
					
					//append absolute address to first4digits NOT add
					//check to see how many of the first4digits I need
					
					String addressField;
					
					
					//if symbol abs address is one digit, remove the 4th digit only 
					if(tempAbsAdd < 10) {
						
						addressField = "00" + tempAbsAdd;	
						first4digitsTemp = numTemp.substring(0,1) + addressField;
						
					}
					
					//if symbol abs address is two digits, remove 3rd and 4th digits
					else if (tempAbsAdd >= 10 && tempAbsAdd < 100) {
						addressField = "0" + tempAbsAdd;
						first4digitsTemp = numTemp.substring(0,1) + addressField;
					
					}
					
					//if symbol abs address is three digits remove 2nd, 3rd, and 4th digits
					else if (tempAbsAdd >= 100 && tempAbsAdd < 1000) {
						
						addressField = "" + tempAbsAdd;
						first4digitsTemp = numTemp.substring(0,1) + addressField;
						//first4digitsTemp = numTemp.substring(0, numTemp.length()-4);
						//first4digitsTemp = first4digitsTemp + tempAbsAdd;
					}
					
					int absAddress = Integer.parseInt(first4digitsTemp);
					
					
					System.out.print(countLine + ": " + absAddress);
					
					//ERROR: Check if multiple symbols have been defined on this instruction
					int flag = module.checkMultipleSymbolsList(symbolString);
					//mutiply defined if marked as 1
					if (flag == 1) {
						System.out.print(" ERROR: Multiple variables used in instruction; all but last ignored");
			
					}
					
					System.out.println();
					
					module.addMemoryMap(absAddress);
					
					}//end_endsWith4
				
					//take out last digit and print 
					else {
						String first4digitsTemp = numTemp.substring(0, numTemp.length() - 1);
						
						int first4digits = Integer.parseInt(first4digitsTemp);
						int absAddress = first4digits;
						System.out.println(countLine + ": " + absAddress);
						module.addMemoryMap(absAddress);
					}
				
					countLine++;
					relativeCount++;
				
				}
				
				
				
				
			}//end for - finish processing a set of 5-digit number from one module
	}
		
	
	/**
	 * This method checks if relative address of string exceeds module size
	 * @param digits absolute address in string format
	 * @param sizeModule size of the module
	 * @return true if address exceeds size of module 
	 */
	public static boolean checkRelExceedModule(String digits, int sizeModule){
		boolean exceedFlag = false;
		String addressFieldStr = digits.substring(1,4);
		int addressFieldInt = Integer.parseInt(addressFieldStr);
		if (addressFieldInt > sizeModule-1) {
			exceedFlag = true;
		}
		return exceedFlag;
	}

	
	
}
