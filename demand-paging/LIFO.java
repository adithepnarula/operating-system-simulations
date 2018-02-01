import java.util.ArrayList;


public class LIFO {
	

	int time;
	
	//Random file to get burst time
	RandomFile randomFile;
	
	

	
	public LIFO(ArrayList<Process> processArray, ArrayList<PageFrame> framesArray){
		this.time = 1;
		this.randomFile = new RandomFile();
		execute(processArray, framesArray);
		printOutput(processArray);
		
	}
	
	public void printOutput(ArrayList<Process> processArray){
		
		int totalFaults = 0;
		double totalAvgRes = 0;
		double totalNumEvict = 0;
		
		for(int i = 0; i < processArray.size(); i++){
			Process process = processArray.get(i);
			double avgRes = process.residentTime*1.0/process.numEvict;
			totalFaults += process.numPageFault;
			totalAvgRes += process.residentTime;
			totalNumEvict += process.numEvict;
			
			if(process.numEvict == 0){
				System.out.println("Process " + process.id + " had " + process.numPageFault + " page faults and no eviction, so the average is undefined");
				
			}
			else{
				System.out.println("Process " + process.id + " had " + process.numPageFault + " page faults and " + avgRes + " average residency.");
				
				
			}
			
		}
		
		totalAvgRes = totalAvgRes/totalNumEvict;
		
		System.out.println("The total number of faults is " + totalFaults + " and the overall average residency is " + totalAvgRes);
		
	}
	
	public void execute(ArrayList<Process> processArray, ArrayList<PageFrame> framesArray){
		
		int processIndex = 0;
		
		//while not all process terminated
		while(!allTerminated(processArray)){
			
			for(int outer = 0; outer < processArray.size(); outer++) {
				Process curProcess = processArray.get(outer);

				//get a process and run till quantum
				for(int i = 0; i < 3 && (curProcess.terminated == false); i++){
					
					
					//<<<<<<<< STEP1: CHECK IF PAGE IN PHYSICAL MEMORY >>>>>>>>>>>
					int curWord = curProcess.nextRef;
					
					//find which logical page curWord is in
		
					int virtualPageNum = (int) Math.floor(curWord/curProcess.pageSize);
					
	
					System.out.println(curProcess.id + " references word " + curProcess.nextRef + " (virtual page "+ virtualPageNum + ") at time " + this.time);
					
					//loop to find if logicPage number of current process is in physical memory
					boolean found = false;
					for(int j = 0; j < framesArray.size(); j++){
						
						//if page in physical memory is owned by current process and is the right page
						if(framesArray.get(j).ownedBy == curProcess.id && framesArray.get(j).virtualPageNum == virtualPageNum){
							System.out.println("Hit in frame: " + j);
							
							//page hit
							found = true;
							
							//update reference time 
							//curProcess.vpArray.get(virtualPageNum).timeRef = this.time;
						
						}
						
					}
					
					///<<<<<<<<<<< STEP 2: PAGE FAULT >>>>>>>>>>>>>
					
					//page fault - could not find page
					if(found == false){
						
						curProcess.numPageFault = curProcess.numPageFault + 1;
			
						//find the highest numbered free frame
						int highest = -1;
						
						//loop from back
						for(int k = framesArray.size()-1; k >= 0; k--){
							
							//found the highest free frame
							if(framesArray.get(k).free == true){
								highest = k;
								
								System.out.println("Fault, using free frame " + k);
								
								//mark the frame taken
								framesArray.get(k).free = false;
								
								//make page resident - tell it what process owns it
								//make page resident - tell it what vp number it is 
								framesArray.get(k).ownedBy = curProcess.id;
								framesArray.get(k).virtualPageNum = virtualPageNum;
								
								
								//update loadTime for virtual page
								curProcess.vpArray.get(virtualPageNum).loadTime = this.time;
								//curProcess.vpArray.get(virtualPageNum).timeRef = this.time;
								
								//break out
								break;
								
								
							}
							
						}//endfor
						
						//no free frames exist, use replacement algorithm
						//LIFO - remove the first page in the array 
						if(highest == -1){
							
							
							//get the first element in the frame
							int lastProcessID = framesArray.get(0).ownedBy;
							int lastProcessVP = framesArray.get(0).virtualPageNum;
							
							
							//get the process that owns the first page
							Process lastProcess = processArray.get(lastProcessID-1);
							
							//get process load time
							int loadTime = lastProcess.vpArray.get(lastProcessVP).loadTime;
							
							//find how long it was in physical memory
							int timeInPm = this.time - loadTime;
							
							System.out.println("Fault, evicting page " + lastProcessVP + " of process " + lastProcessID + " from frame 0");
							
							//add to resident time
							lastProcess.residentTime = lastProcess.residentTime + timeInPm;
							
							//add evict
							lastProcess.numEvict++;
							
							//add page
							framesArray.get(0).ownedBy = curProcess.id;
							framesArray.get(0).virtualPageNum = virtualPageNum;
							
							
							//update loadTime for virtual page
							curProcess.vpArray.get(virtualPageNum).loadTime = this.time;
							//curProcess.vpArray.get(virtualPageNum).timeRef = this.time;
					
							
								
							
						}
						
						
						
						
						
					}//process finish making a reference 
					
					curProcess.numRef = curProcess.numRef + 1;
					
				
				
					
					//get the next ref
					int r = this.randomFile.getNum();
					
					System.out.println(curProcess.id + " uses random number: " + r);
					
					double y = r/(Integer.MAX_VALUE +1d);
					if(y < curProcess.probA){
					
						curProcess.nextRef = (curProcess.nextRef+1)%curProcess.size;
						
					}
					else if(y < curProcess.probA + curProcess.probB){
						curProcess.nextRef = (curProcess.nextRef-5+curProcess.size)%curProcess.size;
						
					}
					else if(y < curProcess.probA + curProcess.probB + curProcess.probC){
						curProcess.nextRef = (curProcess.nextRef+4)%curProcess.size;
						
					}
					else{	
						curProcess.nextRef = this.randomFile.getNum() % curProcess.size;
						
						
					}
					
					
					
					//terminate process
					if(curProcess.numRef == curProcess.maxRef){
						curProcess.terminated = true;
					}
					
					this.time++;
					System.out.println();
					
				}//_end for loop for one quantum
				
	
				
			}
		
	
			
			
		}//all process terminated
		
		randomFile.closeScanner();
		
		
		
		
		
		
		
	}
	
	
	/**
	 * Checks if every process has terminated
	 * @return
	 */
	public static boolean allTerminated(ArrayList<Process> processArray){
		
		for(int i = 0; i < processArray.size(); i++){
			if(processArray.get(i).terminated == false){
				return false;
			}
		}
		
		return true;
		
		
	}
	
	
	
	
	
}
