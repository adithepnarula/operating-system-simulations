import java.util.ArrayList;
import java.util.Scanner;


public class Driver {
	
	public static void main(String[] args){
		
		//validate existence of command line arguments
		if (args.length < 1 ) {
			System.err.printf("Error: invalid number of arguments.\n");
			System.exit(1);
		}
		//only give name given
		else if(args.length != 7) {
			System.err.printf("Error: Program accepts argument in the form: M P S J N R 0");
			System.exit(1);
		}
		
		
		
		//read user input
		String[] userInputArray = args;
		
		//convert user input
		int machineSize = Integer.parseInt(userInputArray[0]);
		int pageSize = Integer.parseInt(userInputArray[1]);
		int processSize = Integer.parseInt(userInputArray[2]);
		int jobMix = Integer.parseInt(userInputArray[3]);
		int maxRef = Integer.parseInt(userInputArray[4]);
		String algo = userInputArray[5];
		
		System.out.println("The machine size is " + machineSize);
		System.out.println("The page size is " + pageSize);
		System.out.println("The process size is " + processSize);
		System.out.println("The job mix number is " + jobMix);
		System.out.println("The number of references per process is " + maxRef);
		System.out.println("The replacement algorithm is " + algo);
		
		//number of physical frames
		double numFrames = machineSize*1.0/pageSize;
		
		//create physical memory
		ArrayList<PageFrame> framesArray = new ArrayList<PageFrame>();
		for (int i = 0; i < numFrames; i++){
			PageFrame frame = new PageFrame();
			framesArray.add(frame);
		}
		
		
		
		//assigned correct probability
		double probA, probB, probC;
		ArrayList<Process> processArray = new ArrayList<Process>();
		
		if(jobMix == 1){
			probA = 1;
			probB = 0;
			probC = 0;
			int id = 1;
			Process process = new Process(id, probA, probB, probC, processSize, pageSize, maxRef);
			processArray.add(process);
			
		}
		else if (jobMix == 2){
			probA = 1;
			probB = 0;
			probC = 0;
			for(int i = 1; i <= 4; i++){
				Process process = new Process(i, probA, probB, probC, processSize, pageSize, maxRef);
				processArray.add(process);
			}
		}
		else if (jobMix == 3){
			probA = 0;
			probB = 0;
			probC = 0;
			for(int i = 1; i <= 4; i++){
				Process process = new Process(i, probA, probB, probC, processSize, pageSize, maxRef);
				processArray.add(process);
			}
			
		}
		else if (jobMix == 4){
			//process1 
			probA = 0.75;
			probB = 0.25;
			probC = 0;
			Process process1 = new Process(1, probA, probB, probC, processSize, pageSize, maxRef);
			processArray.add(process1);
			
			//process2
			probA = 0.75;
			probB = 0;
			probC = 0.25;
			Process process2 = new Process(2, probA, probB, probC, processSize, pageSize, maxRef);
			processArray.add(process2);
			
			//process3
			probA = 0.75;
			probB = 0.125;
			probC = 0.125;
			Process process3 = new Process(3, probA, probB, probC, processSize, pageSize, maxRef);
			processArray.add(process3);
			
			//process4
			probA = 0.5;
			probB = 0.125;
			probC = 0.125;
			Process process4 = new Process(4, probA, probB, probC, processSize, pageSize, maxRef);
			processArray.add(process4);
			
			
		}
		
	
		
		if(algo.equals("lru")){
			System.out.println("Entering LRU.....");
			System.out.println();
			LRU lru = new LRU(processArray,framesArray);
		}
		else if(algo.equals("lifo")){
			System.out.println("Entering Lifo.....");
			System.out.println();
			LIFO lifo = new LIFO(processArray,framesArray);
		}
		else{
			System.out.println("Entering Random.....");
			System.out.println();
			Random random = new Random(processArray,framesArray);
		}
	}

}
