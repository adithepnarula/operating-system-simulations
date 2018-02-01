import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;


public class SFJ {
	
	//summary data
	int algFinishTime;
	double cpuUtil;
	double ioUtil;
	double throughput;
	double avgTurn;
	double avgWait;
	boolean verboseFlag;
	

	//original sorted list
	//processes already ordered in lab 2 tie break 
	ArrayList<Process> arrayProcess;
	
	//ready Q - lab2 tie break rule implemented
	ArrayList<Process> readyQ;
	
	//Termination array to keep track
	ArrayList<Process> terminateQ;
	
	//totalTime begins at 0 and gets incremented till the end
	int totalTime;
	int totalWait;
	
	//Random file to get burst time
	RandomFile randomFile;
	
	SFJ(ArrayList<Process> arrayProcessInput, boolean verboseFlag){
		

		//make a new copy of the array
		this.arrayProcess = new ArrayList<Process>();
		for (int i = 0; i < arrayProcessInput.size(); i++) {
			
			Process process = arrayProcessInput.get(i);
			
			int arrivalTime = process.arrivalTime;
			int B = process.B;
			int cpuTime = process.cpuTime;
			int M = process.M;
			int listedIndex = process.numberListedInput;
			
			Process newProcess = new Process(arrivalTime, B, cpuTime, M, listedIndex);
			this.arrayProcess.add(newProcess);
		}
		
		this.totalTime = 0;
		this.readyQ = new ArrayList<Process>();
		this.terminateQ = new ArrayList<Process>();
		this.randomFile = new RandomFile();
		this.algFinishTime = -1;
		this.cpuUtil = -1;
		this.ioUtil = -1;
		this.throughput = -1;
		this.avgTurn = -1;
		this.avgWait = -1;
		this.verboseFlag = verboseFlag;
		
		//<<<<<<< PRINT PROCESSES >>>>>>
		
		
		//<<<<<<< SET UP >>>>>>>>
		//print process before cycle 0
		if(this.verboseFlag == true) {
			printProcess();	
		}
		
		
		//move all the processes that have arrived to readyQ
		moveToReadyQ();
		
		
		//<<<<<<< BEGIN RUNNING >>>>>>>
		runToCompletion();
		
		//<<<<<< END RUNS >>>>>>>>>>
		//update needed statistics and print
		updateStats();
		printStats();
		randomFile.closeScanner();
	
		
	}//end_FCFS
	
	public void runToCompletion(){
		
		while(this.terminateQ.size() < this.arrayProcess.size()) {
			
			//<<<<<<<< CPU IS IDLE >>>>>>>>
			//if readyQ is empty, wait for one process to either arrive or get unblock
			while(this.readyQ.size() == 0 && (this.terminateQ.size() < this.arrayProcess.size())) {
				
				//update two things: +1 total time and -1 blocked time for all process
				addTotalTime();
				
				
				//print status after 1 second
				if(this.verboseFlag == true) {
					printProcess();	
				}
				
				reduceBlockedTime(); 
				
			
				//check for arrival or unblock and move 
				moveToReadyQ();
				
			}
			
			//<<<<<<< CPU READY TO EXECUTE: PREPARE >>>>>>>
			
			//get process from front of the Q
			Process process = readyQ.get(0);
			
			//remove that process from Q
			readyQ.remove(0);
			
		
			//execute the process
			cpuExecute(process);
			
			
			
		}
		
	}
	
	public void cpuExecute(Process process) {
		
		//obtain burst time for process
		int initialCpuBurst = randomFile.randomOS(process.B);
		
		//if burst time is more than cpu remaining time, set burst time to cpu remaining time
		if( initialCpuBurst > process.cpuRemain) {
			initialCpuBurst = process.cpuRemain;
			
		}
	
		//update the process current cpu burst time
		process.cpuBurst = initialCpuBurst;
		
		//change state to running
		process.state = "running";
		
		//run till block or termination
		//t is starting CPU burst. Updating cpu burst inside.
		for (int i = 0; i < initialCpuBurst; i++, process.reduceCpuBurst()) {
			//update three things: total time, cpu remaining time for this process, and reduce block time of all process by 1
			addTotalTime();
			process.reduceCpuRemain();
			
			//print out current state of process after 1 second
			//print state while its blocked
			if(this.verboseFlag == true) {
				printProcess();	
			}
			
			
			//reduce block time of all process by 1
			reduceBlockedTime();
				
			//after one second updated, this process may terminate or block
			//check if process terminates
			boolean toTerminate = checkTermination(process);
			if (toTerminate) {
				process.state = "terminated";
				process.completionTime = this.totalTime;
				terminateQ.add(process);
				
			}
			
			//check if process finishes its burst. If it does, block. 
			else if(i == initialCpuBurst-1) {
				//calculate io burst time
				process.ioBurst = initialCpuBurst * process.M;
				process.state = "blocked";
				
			}
			
		
			//States have been updated. So move all the process that recently arrive and recently unblocked to ready Q.
			moveToReadyQ();
			
		}
		
		
	}
	
	public void printProcess(){
		System.out.format("%12s%6d%1s", "Before Cycle", this.totalTime,":");
		for (int i = 0; i < arrayProcess.size(); i ++) {
			Process process = arrayProcess.get(i);
			if (process.state == "created") {
				System.out.format("%13s%3d","unstarted",0);
			}
			else if (process.state == "ready") {
				System.out.format("%13s%3d", "ready", 0);
			}
			else if (process.state == "running") {
				System.out.format("%13s%3d", "running", process.cpuBurst);
			}
			else if (process.state == "blocked") {
				System.out.format("%13s%3d", "blocked", process.ioBurst);
			}
			else if (process.state == "terminated") {
				System.out.format("%13s%3d", "terminated", 0);
				
			}
			System.out.print("  ");
		
			
		}
		System.out.println();
	
		
	}
	
	
	public boolean checkTermination(Process process){
		if (process.cpuRemain == 0) {
			return true;
		}
		else {
			return false;
		}
		
	}
	

	public void moveToReadyQ(){
		
		//move all processes that have recently unblocked 
		//also move all process that have recently arrived
		ArrayList<Process> toProcess = new ArrayList<Process>();

		
		for (int i = 0; i < this.arrayProcess.size(); i++) {
			
			Process process = this.arrayProcess.get(i);
			
			//if process is blocked and i/o is 0, then unblock
			if (process.state == "blocked" && process.ioBurst == 0) {
				process.state = "ready";
				toProcess.add(process);
			
			}
			if (process.state == "created" && this.totalTime >= process.arrivalTime) {
				process.state = "ready";
				toProcess.add(process);	
			}
			
		}//end for
		
		//add it to readyQ FIRST THEN ORGANIZE
		for(int i = 0; i < toProcess.size(); i++) {
			readyQ.add(toProcess.get(i));
		}
		
		//use lab 2 tie break function to organize the array list
		readyQ = tieBreak(readyQ);
		
	
		
		
		
	}//end_function
	
	/**
	 * Add one more condition to tie break -- organize by SFJ
	 * @param arrayUn
	 * @return
	 */
	public ArrayList<Process> tieBreak(ArrayList<Process> arrayUn){
		
		//array list is quite confusing with bubble sort, I understand
		//converting it to array then converting back to array list is inefficient
						
		Process[] array = new Process[arrayUn.size()];
		for(int i = 0; i < array.length; i++) {
			array[i] = arrayUn.get(i);
		}
						
		Process temp;
		for (int i = 0; i < array.length; i++) {
							
			for (int j = 1; j < (array.length-i); j++) {
			
			//if cpu remain time of left is more than right, switch
			if(array[j-1].cpuRemain > array[j].cpuRemain) {
				temp = array[j-1];
				array[j-1] = array[j];
				array[j] = temp;	
				
			}
			//CPU remain time is the same - tie break one occurs 
			else if (array[j-1].cpuRemain == array[j].cpuRemain){
		
				if((array[j-1].arrivalTime > array[j].arrivalTime)){
					temp = array[j-1];
					array[j-1] = array[j];
					array[j] = temp;			
					}
				
				//tiebreak two occurs
				//tie break two occurs 
				else if(array[j-1].arrivalTime == array[j].arrivalTime){
										
						if(array[j-1].numberListedInput > array[j].numberListedInput){
								temp = array[j-1];
								array[j-1] = array[j];
								array[j] = temp;
								System.out.println("swap!");
								}
						
						
					}
			}//end_if
						
			}//_endfor
			}//_end outer for
						
		arrayUn.clear();
							
		for(int i = 0; i < array.length; i++) {
			arrayUn.add(array[i]);
		}
		
		
		return arrayUn;
		
		
	}//_end function
	
	public void addTotalTime(){
		this.totalTime++;
	}
	
	public void reduceBlockedTime(){
		
		boolean processWaited = false;
		
		for (int i = 0; i < arrayProcess.size(); i++) {
			Process process = arrayProcess.get(i);
			if (process.state == "blocked") {
				process.ioBurst = process.ioBurst -1;
				//also add the process's total io time
				process.ioTime = process.ioTime + 1;
				processWaited = true;
			}//end if
			
		}//end for
		
		//if processWaited flag raised it means there was a process in block, so increase overall wait time
		if (processWaited) {
			addTotalWait();	
		}
		
	}
	public void updateStats(){
		for (int i = 0; i < arrayProcess.size(); i++){
			Process process = arrayProcess.get(i);
			process.updateStats();
		}
		
		int totalCpuTime = 0;
		
		int totalTurn = 0;
		int tempWait = 0;
		
		this.algFinishTime = this.totalTime;
		
		//find cpu util and io util
		for(int i = 0; i <arrayProcess.size(); i++){
			Process process = arrayProcess.get(i);
			totalCpuTime = totalCpuTime + process.cpuTime;
			
			totalTurn = totalTurn + process.turnAroundTime;
			tempWait = tempWait + process.waitingTime;
			
		}
		
		this.cpuUtil = totalCpuTime*1.0/totalTime;
		this.ioUtil = totalWait*1.0/totalTime;
		this.throughput = (arrayProcess.size()*100)*1.0/this.totalTime;
		this.avgTurn = totalTurn*1.0/arrayProcess.size();
		this.avgWait = tempWait*1.0/arrayProcess.size();
		
	}
	public void printStats(){
		
		System.out.println("The scheduling algorithm used was Shortest Job First");
		System.out.println();
		for (int i = 0; i < arrayProcess.size(); i++){
			Process process = arrayProcess.get(i);
			System.out.println("Process " + i +":");
			
			System.out.println("	(A,B,C,M) = " + process.processToString());
			
			//print finish time
			System.out.println("	Finishing time: " + process.completionTime);
			
			//print turnaround time
			System.out.println("	Turnaround time: " + process.turnAroundTime);
			
			//print i/o time
			System.out.println("	I/O time: " + process.ioTime);
			
			//print waiting time
			System.out.println("	Waiting time: " + process.waitingTime);
			System.out.println();
		}//end_for
		
		System.out.println("Summary Data: ");
		System.out.println("	Finishing time: " + this.algFinishTime);
		System.out.println("	CPU Utilization: " + this.cpuUtil);
		System.out.println("	I/O Utilization: " + this.ioUtil);
		System.out.println("	Throughput: " + this.throughput + " per hundred cycles");
		System.out.println("	Average turnaround time: " + this.avgTurn);
		System.out.println("	Average waiting time: " + this.avgWait);
		System.out.println();

		
	}

	public void addTotalWait(){
		this.totalWait++;
	}
}//end_class
