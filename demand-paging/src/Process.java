import java.util.ArrayList;


public class Process {
	
	//process ID
	int id;
	double probA, probB, probC;
	
	//get next word to read 
	int nextRef;
	
	//process size
	int size;
	boolean terminated; 
	
	//logic array is virtual memory
	ArrayList<VirtualPage> vpArray;
	
	//number of pages in virtual memory
	int numPages;
	
	//number of page faults for this process
	int numPageFault;
	
	//page size
	int pageSize;
	
	int residentTime;
	
	//num references
	int numRef;
	int maxRef;
	
	//number of evictions
	int numEvict;
	
	
	
	
	public Process(int id, double probA, double probB, double probC, int processSize, int pageSize, int maxRef){
		this.numEvict = 0;
		this.id = id;
		this.probA = probA;
		this.probB = probB;
		this.probC = probC;
		this.size = processSize;
		this.nextRef = (111*this.id)%processSize;
		this.terminated = false;
		this.pageSize = pageSize;
		this.residentTime = 0;
		this.numRef = 0;
		this.maxRef = maxRef;
		
		
		//number of pages in virtual memory
		int numPages = this.size/this.pageSize;
				
		//initialize Virtual Memory
		vpArray = new ArrayList<VirtualPage>();
		for(int i = 0; i < numPages; i++){
			vpArray.add(new VirtualPage(i));
		}
		
		
	
		//num page fault initialized to 0
		this.numPageFault = 0;
		
	

		
		
	}



}
