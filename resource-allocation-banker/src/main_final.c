#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "objects.h"
#include "header.h"


int main(int argc, char *argv[]){

	//first argument is the file name you are running, so we need +1 arguments
	//argc - number of arguments
	//argv - array containing arguments

	int verboseFlag = 0;

	if (argc < 2 || argc > 3) {
		printf("Invalid number of parameters, requires filename  %s\n and filename or --verbose flag", argv[0]);
		exit(1);
	}

	if(argc == 3){
		verboseFlag = 1;
	}

	//arg[0] is a string containing file name, setting the mode to read
	FILE *file = fopen(argv[1], "r");

	//exit if file doesn't work
	if (file == 0){
		printf("Invalid file %s\n", argv[1]);
	}

	printf("user input\n");
	printf("----------------------------------------\n");

	printf("verbose Flag: %s %d\n", argv[1], verboseFlag);


	//number of tasks in this program and number of different resource types
	int numTask, numResourceType;

	//get number of tasks and number of resource types
	fscanf(file, "%d %d", &numTask, &numResourceType);

	//resource units store number of available units for each resource at the start of the program
	int resourceUnits[numResourceType];
	int i;
	//read the number of units available for each resource type
	for (i = 0; i < numResourceType; i++){
		fscanf(file, "%d", &(resourceUnits[i]));
	}//end for

	int numberOfElements = 50;



	//create an array of points to resources for FIFO
	struct resource *resourceArray[numResourceType];

	for(i = 0; i < numResourceType; i++){
		resourceArray[i] = (struct resource*)malloc(sizeof(struct resource));
		resourceArray[i]->resourceType = i+1;//to be consistent with professor
		resourceArray[i]->taskAssigned = -1;//not assigned to any task
		resourceArray[i]->totalUnits = resourceUnits[i];
		resourceArray[i]->unitsAvai = resourceUnits[i];

	}

	//create an array of points to resources for BANKERS
	struct resource *resourceArray_b[numResourceType];

	for(i = 0; i < numResourceType; i++){
		resourceArray_b[i] = (struct resource*)malloc(sizeof(struct resource));
		resourceArray_b[i]->resourceType = i+1;//to be consistent with professor
		resourceArray_b[i]->taskAssigned = -1;//not assigned to any task
		resourceArray_b[i]->totalUnits = resourceUnits[i];
		resourceArray_b[i]->unitsAvai = resourceUnits[i];

	}

	//debug pringting resource
	for (i = 0; i < numResourceType; i++){
		printf("resoure %d | total units %d | units avilable %d | task assigned %d \n", resourceArray[i]->resourceType,
		resourceArray[i]->totalUnits, resourceArray[i]->unitsAvai,resourceArray[i]->taskAssigned);
	}




	//create an array of pointers to tasks structs
	struct task *taskArray[numTask];

	//intialize task structures for FIFO
	int j;
	for (j = 0; j < numTask; j++){
		taskArray[j] = (struct task*)malloc(sizeof(struct task));
		taskArray[j]-> id = j+1;
		taskArray[j]-> numActivities = 0;
		taskArray[j]-> computeTime = -1;
		taskArray[j]-> terminated = 0;
		taskArray[j]-> curNumActivity = 0;
		taskArray[j]-> pending = 0;
		taskArray[j]-> waitingFor = -1;
		taskArray[j]-> unitWaiting = 0;
		taskArray[j]-> deadlock = 0;
		taskArray[j]-> terminateTime = -1;
		taskArray[j]-> aborted = 0;
		taskArray[j]-> waitingTime = 0;
		taskArray[j]-> releasedFromQ = 0;
		taskArray[j] -> alreadyPrintedTerminate = 0;
		taskArray[j]-> checkFirst = -1;
		taskArray[j]-> currentlyAllocated = (int*)malloc(numResourceType*sizeof(int));
		//initialize currently allocated to 0 since nothing is allocated yet
		int k;
		for (k = 0; k < numResourceType; k++){
			taskArray[j] -> currentlyAllocated[k] = 0;
		}

		taskArray[j]-> activities = (struct activity*)malloc(numberOfElements*sizeof(struct activity*));
	}//end_for

		//intialize task structures for BANKERS
	struct task *taskArray_b[numTask];
	for (j = 0; j < numTask; j++){
		taskArray_b[j] = (struct task*)malloc(sizeof(struct task));
		taskArray_b[j]-> id = j+1;
		taskArray_b[j]-> numActivities = 0;
		taskArray_b[j]-> computeTime = -1;
		taskArray_b[j]-> terminated = 0;
		taskArray_b[j]-> curNumActivity = 0;
		taskArray_b[j]-> pending = 0;
		taskArray_b[j]-> waitingFor = -1;
		taskArray_b[j]-> unitWaiting = 0;
		taskArray_b[j]-> deadlock = 0;
		taskArray_b[j]-> terminateTime = -1;
		taskArray_b[j]-> aborted = 0;
		taskArray_b[j]-> waitingTime = 0;
		taskArray_b[j]-> releasedFromQ = 0;
		taskArray_b[j] -> alreadyPrintedTerminate = 0;
		taskArray_b[j]-> checkFirst = -1;
		taskArray_b[j]-> currentlyAllocated = (int*)malloc(numResourceType*sizeof(int));
		//initialize currently allocated to 0 since nothing is allocated yet
		int k;
		for (k = 0; k < numResourceType; k++){
			taskArray_b[j] -> currentlyAllocated[k] = 0;
		}

		taskArray_b[j]-> activities = (struct activity*)malloc(numberOfElements*sizeof(struct activity*));
	}//end_for



	//initialize max matrix for bankers
	int count;
	int *max[numTask];
	for(count = 0; count < numTask; count++){
		*(max+count) = (int *)malloc(numResourceType*sizeof(int));
	}

	int count1;
	for(count = 0; count < numTask; count++){
		for(count1 = 0; count1 < numResourceType; count1++){
			*(*(max+count)+count1) = 0;
		}
	}


	//max number of string characters
	char s[20];
	int taskNum;
	int resType;
	int reqUnits;
	int numAct = 0;
	//populate activity structs inside tasks
	while(fscanf(file, "%s %d %d %d", s, &taskNum, &resType, &reqUnits) == 4){


		//taskNum and resourceNum must be subtracted by 1 because professor starts with task 1 and resourceType 1
		taskNum = taskNum - 1;

		//initialize activity
		numAct = taskArray[taskNum] -> numActivities;

		//store resource type (for compute, this is numCycles)
		taskArray[taskNum] -> activities[numAct].resType = resType;
		taskArray_b[taskNum] -> activities[numAct].resType = resType;


		//store resource units (for release, this is units release)
		//(for initiate, this is initial claim)
		taskArray[taskNum] -> activities[numAct].reqUnits = reqUnits;
		taskArray_b[taskNum] -> activities[numAct].reqUnits = reqUnits;

		//store actions
		if (strcmp(s,"initiate") == 0){
			taskArray[taskNum]->activities[numAct].action = 0;
			taskArray_b[taskNum]->activities[numAct].action = 0;
			//populate max array
			*(*(max+taskNum)+(resType-1)) = reqUnits;
		}
		else if (strcmp(s,"request") == 0){
			taskArray[taskNum]->activities[numAct].action = 1;
			taskArray_b[taskNum]->activities[numAct].action = 1;
		}
		else if (strcmp(s,"release") == 0){
			taskArray[taskNum]->activities[numAct].action = 2;
			taskArray_b[taskNum]->activities[numAct].action = 2;
		}
		else if (strcmp(s,"compute") == 0){
			taskArray[taskNum]->activities[numAct].action = 3;
			taskArray_b[taskNum]->activities[numAct].action = 3;
		}
		else if (strcmp(s,"terminate") == 0){
			taskArray[taskNum]->activities[numAct].action = 4;
			taskArray_b[taskNum]->activities[numAct].action = 4;
		}

		taskArray[taskNum] -> numActivities = (taskArray[taskNum] -> numActivities + 1);
		taskArray_b[taskNum] -> numActivities = (taskArray_b[taskNum] -> numActivities + 1);
	}//end_while


	fifo(taskArray,numTask,resourceArray,numResourceType, verboseFlag);
	banker(taskArray_b,numTask,resourceArray_b,numResourceType, max, verboseFlag);


	//free pointers
	for (j = 0; j < numTask; j++){
		//free fifo resources
		free(taskArray_b[j]->currentlyAllocated);
		free(taskArray_b[j]-> activities);

		//free bankers resources
		free(taskArray[j]->currentlyAllocated);
		free(taskArray[j]-> activities);
	}//end_for



	//DONT FORGET TO FREE
	fclose(file);

	return 1;
}
