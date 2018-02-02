//initiate = 0
//request = 1
//release = 2
//compute = 3
//terminate = 4
struct activity {
	int action;
	int resType;
	int reqUnits;
};

//each task will have multiple activities
//set temp number of activities
struct task {
	int id;
	int terminated;
	int numActivities;
	int curNumActivity;
	int computeTime;
	//resource type and units that task is holding
	//array index + i corresponds to resource iS
	int *currentlyAllocated;
	//set pending to 1 if task is in block q
	int pending;
	//set waiting for to equal to resType its waiting for
	int waitingFor;
	//units the task is waiting for 
	int unitWaiting;
	//time spent waiting
	int waitingTime;
	
	int initialClaim;
	int deadlock;
	int terminateTime;
	int aborted;
	//mark if task rcently released from block Q at cycle K due to pending release 
	//if recently released from blocked state at cycle K, don't check its activity and move on to next task since its request
	//has been granted 
	int releasedFromQ;
	
	//I remove an element from block Q if possible before cycle detecion is run
	//run checkFirst in the next activity cycle because they were supposed to be release in cycle k+1
	int checkFirst;
	
	
	//print terminate only once, use as marker
	int alreadyPrintedTerminate;
	struct activity *activities;
};

struct resource {
	//resource ID
	int resourceType;
	//units starting with initially
	int totalUnits;
	//units currently available
	int unitsAvai;
	int taskAssigned;
};
