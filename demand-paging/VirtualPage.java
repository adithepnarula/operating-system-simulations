
public class VirtualPage {
	
	//time it got used last
	int timeRef;
	
	//for residency time 
	int loadTime;
	
	//page number in the array
	int pageNum;

	//whether its currently in physical memory
	boolean inMemory;
	
	public VirtualPage(int pageNum){
		this.pageNum = pageNum;
		
		this.loadTime = 0;
		
		this.timeRef = 0;
		this.inMemory = false;
		
	}

}
