

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {

	public static void main(String[] args) {
		
		String fileName = "";
		
		boolean verboseFlag = false;
		//validate existence of command line arguments
		if (args.length < 1 ) {
			System.err.printf("Error: invalid number of arguments.\n");
			System.exit(1);
		}
		//only give name given
		else if(args.length == 1) {
			fileName = args[0];
			System.out.println("args[0]: " + args[0]);
		}
		else if(args.length == 2) {
			verboseFlag = true;
			fileName = args[1];
			System.out.println("args[0]: " + args[0]);
			System.out.println("args[1]: " + args[1]);
		}
		
		
		System.out.println();

		
		//use to store number of process, solely for printing
		int[] numProcess = new int[1];
		
		//unsorted array list
		ArrayList<Process> arrayProcessesUnsorted = createProcesses(fileName, numProcess);
		
		//print original input
		System.out.print("The original input was: [" + numProcess[0] + "] ");
		printArrayList(arrayProcessesUnsorted);
		
		
		
		//sorted array list by arrival time
		ArrayList<Process> arrayProcess = sortByArrivalTime(arrayProcessesUnsorted);
	
		//print sorted input
		System.out.print("The (sorted) input is: [" + numProcess[0] + "] ");
		printArrayList(arrayProcess);
		
		System.out.println();
		System.out.println("This detailed printout gives the state and remaining burst for each process");
		System.out.println();

		FCFS_V3_test algFCFS = new FCFS_V3_test(arrayProcess, verboseFlag);
		RR_V2 algRR = new RR_V2(arrayProcess, verboseFlag);
		Uniprogram uniAlg = new Uniprogram(arrayProcess, verboseFlag);
		SFJ algSFJ = new SFJ(arrayProcess, verboseFlag);
		
	}
	
	public static void printArrayList(ArrayList<Process> arrayProcess){
		for (int i = 0; i < arrayProcess.size(); i ++){
			System.out.print("Process" + arrayProcess.get(i).numberListedInput +": " + arrayProcess.get(i).processToString()+ " ");
		}
		System.out.println();
	}
	
	public static ArrayList<Process> sortByArrivalTime(ArrayList<Process> arrayProcess){
		//array list is quite confusing with bubble sort, I understand
		//converting it to array then converting back to array list is inefficient
		
		Process[] array = new Process[arrayProcess.size()];
		for(int i = 0; i < array.length; i++) {
			array[i] = arrayProcess.get(i);
		}
		
		Process temp;
		for (int i = 0; i < array.length; i++) {
			
			for (int j = 1; j < (array.length-i); j++) {
			
				if (array[j-1].arrivalTime > array[j].arrivalTime){
					temp = array[j-1];
					array[j-1] = array[j];
					array[j] = temp;
					
				}
				
			}
		}
		
		arrayProcess.clear();
		
		for(int i = 0; i < array.length; i++) {
			arrayProcess.add(array[i]);
		}
	
		return arrayProcess;
		
	}
	
	
	public static ArrayList<Process> sortByListedTime(ArrayList<Process> arrayProcess){
		//array list is quite confusing with bubble sort, I understand
		//converting it to array then converting back to array list is inefficient
				
				Process[] array = new Process[arrayProcess.size()];
				for(int i = 0; i < array.length; i++) {
					array[i] = arrayProcess.get(i);
					System.out.println("process " + array[i].processToString() + " arrival time: " + array[i].arrivalTime);
				}
				
				Process temp;
				for (int i = 0; i < array.length; i++) {
					
					for (int j = 1; j < (array.length-i); j++) {
					
						if (array[j-1].numberListedInput > array[j].numberListedInput){
							temp = array[j-1];
							array[j-1] = array[j];
							array[j] = temp;
							
						}
					}
				}
				
				arrayProcess.clear();
				
				for(int i = 0; i < array.length; i++) {
					arrayProcess.add(array[i]);
				}
			
				return arrayProcess;
				
		
		
	}
		
		

	
	public static ArrayList<Process> createProcesses(String fileName, int[] numProcess) {

	
		//verify existence of the input file
		File inputFile = new File(fileName);

		if (!inputFile.canRead()) {
			System.err.printf("Error: cannot read the input file %s\n\n", fileName);
			System.exit(1);
		}


		//open the dictionary file for reading
		Scanner inputIn = null;
		try {
			inputIn = new Scanner(inputFile);
		} catch (FileNotFoundException e) {
			System.err.printf("Error: cannot read the input file %s\n\n", fileName);
			System.exit(1);
		}
		
		
		ArrayList<Process> arrayProcess = new ArrayList<Process>(); 
		int countNP = 0;
		//read in the number of processes and convert to integer
		if(inputIn.hasNext()){
			String countNPString = inputIn.next();
			countNP = Integer.parseInt(countNPString);
			numProcess[0] = countNP;
		}
		
		//read in all the processes 
		for (int i = 0; i < countNP; i++) {
			
			//get arrival time
			int arrivalTime = 0;

			if (inputIn.hasNext()) { 
				String arrivalTimeString = inputIn.next();
				//remove the opening parentheses
				arrivalTime = Integer.parseInt(arrivalTimeString.substring(1));
			}
			
			//get B to calculate burst time
			int B = 0;
			
			if (inputIn.hasNext()){
				String BString = inputIn.next();
				B = Integer.parseInt(BString);
			}
			
			//get total CPU time
			int cpuTime = 0;
			
			if (inputIn.hasNext()) {
				String cpuTimeString = inputIn.next();
				cpuTime = Integer.parseInt(cpuTimeString);	
			}
			
			//get M the multiplying factor
			int M = 0;
			
			if(inputIn.hasNext()){
				String MString = inputIn.next();
				//remove closing parentheses 
				M = Integer.parseInt(MString.substring(0, 1));
			}
			
			//number listed in input corresponds
			int numberListedInput = i;
			
			//create a new process and add it to array list
			Process process = new Process(arrivalTime, B, cpuTime, M, numberListedInput);
			arrayProcess.add(process);
			
		}//end for
		
		
		return arrayProcess;
		
		
	}
	
	

}
