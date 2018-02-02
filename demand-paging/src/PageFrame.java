
public class PageFrame {

	//process ID that owns pageFrame
	int ownedBy;
	
	//if page frame is currently free
	boolean free;
	
	//corresponding virtual page number of the process
	int virtualPageNum;
	
	public PageFrame(){
		this.free = true;
		this.ownedBy = -1;
		this.virtualPageNum = -1;
	}
}
