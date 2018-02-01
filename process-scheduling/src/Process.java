

public class Process {
	
	int arrivalTime;
	int B;
	int cpuTime;
	int M;
	String state;
	//think of number listed in input as process number
	int numberListedInput;
	int ioBurst;
	int cpuBurst;
	int cpuRemain;
	
	int completionTime;
	int turnAroundTime;
	int ioTime;
	int waitingTime;
	//use to store preceding cpu time
	int precedingCpuTime;
	
	//for RR
	boolean inPreemptLoop = false;
	
	//for uniprogram
	boolean toSchedule = false;
	
	Process(int arrivalTime, int B, int cpuTime, int M, int numberListedInput){
		this.arrivalTime = arrivalTime;
		this.B = B;;
		this.cpuTime = cpuTime;
		this.cpuRemain = cpuTime;
		this.M = M;
		//when process created goes to ready
		this.state = "created";
		this.numberListedInput = numberListedInput;
	}
	
	public void updateStats(){
		this.turnAroundTime = this.completionTime - this.arrivalTime;
		this.waitingTime = this.turnAroundTime - this.cpuTime - this.ioTime;
	}
	
	public String processToString(){
		String temp = "(" + arrivalTime + " " + B + " " + cpuTime + " " + M + ")";
		return temp;
		
	}
	
	public void reduceCpuRemain(){
		this.cpuRemain = this.cpuRemain-1;
	}
	
	public void reduceCpuBurst(){
		this.cpuBurst = this.cpuBurst-1;
	}
	
	
	
}
