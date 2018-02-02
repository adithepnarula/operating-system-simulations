import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class RandomFile {
	
	Scanner inputIn;
	File inputFile;
	
	
	RandomFile(){
		
		this.inputIn = openFile("random-numbers.txt");

	
	}
	
	
	
	public Scanner openFile(String filePath) {	
		//verify existence of the input file
		this.inputFile = new File(filePath);

		if (!inputFile.canRead()) {
			System.err.printf("Error: cannot read the input file %s\n\n", filePath);
			System.exit(1);
		}

		//open the random file for reading
		this.inputIn = null;
		try {
			this.inputIn = new Scanner(inputFile);
		} catch (FileNotFoundException e) {
			System.err.printf("Error: cannot read the input file %s\n\n", filePath);
			System.exit(1);
		}
		
		return this.inputIn;
		
	}
	
	
	public int getNum() {
		
		int randomNumber = -1;
		
		//get the next line
		if(this.inputIn.hasNext()){
			String randomNumberString = this.inputIn.next();
		
			randomNumber = Integer.parseInt(randomNumberString);		
		}
	
		return randomNumber;
		
	
	}
	
	public void closeScanner(){
		this.inputIn.close();
	}

	
}
