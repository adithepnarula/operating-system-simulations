#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "objects.h"
#include "header.h"
void banker(struct task **taskArray, int numTask, struct resource **resourceArray, int numResourceType, int **max, int verboseFlag){
	int cycle = 0;
	//create array of pending tasks, each value corresponds to task array ID
	//initialize to -1
	int sizeQ = 100;
	int blockQ[sizeQ];
	int i;
	for(i=0; i < sizeQ; i++){
		blockQ[i] = -1;
	}
	//while not all process has terminated, keep running
	while(b_allTerminated(taskArray, numTask) == 0){
		b_runOneCycle(taskArray, numTask, &cycle, resourceArray, numResourceType, blockQ, sizeQ, max, verboseFlag);
	}
	b_printOutput(taskArray, numTask, cycle);
}


void b_printOutput(struct task **taskArray, int numTask, int cycle){
	printf("------------------\n");
	printf("BANKER's\n");
	int totalTime = 0;
	int totalWait = 0;

	int i;
	for(i = 0; i < numTask; i++){
		printf("Task %d: ", taskArray[i] -> id);
		if(taskArray[i] -> aborted == 1){
			printf(" aborted\n");
		}
		else{
			int timeTaken = taskArray[i] -> terminateTime;
			totalTime = totalTime + timeTaken;
			int waitingTime = taskArray[i] -> waitingTime;
			totalWait = totalWait + waitingTime;
			float percentageWait = (waitingTime*1.0/timeTaken)* 100;
			printf("%d  %d  %f\n", timeTaken, waitingTime, percentageWait);
		}

	}

	float overallP = totalWait*1.0/totalTime * 100;
	printf("total: %d %d %f\n", totalTime, totalWait, overallP);

}

int b_allTerminated(struct task **taskArray, int numTask){
	//assume all terminated
	int i;
	for (i = 0; i < numTask; i++){
		if(taskArray[i] -> terminated == 0){
			return 0;
		}
	}
	return 1;
}

void b_runOneCycle(struct task **taskArray, int numTask, int *ptCycle, struct resource **resourceArray, int numResourceType, int *blockQ, int sizeQ, int **max, int verboseFlag){
	int i;
	//use tempResourceArray to store resources released by all the task between cycle k to k+1, update this after
	//all tasks have executed its activity
	//thus making resources available at cycle K+1
	if(verboseFlag == 1){
		printf("--------------------------------------------------\n");
		printf("During %d - %d\n", *ptCycle, *ptCycle+1);
		printf("\n");
	}
	struct resource *tempResourceArray[numResourceType];
	int p;
	//create temp array to store units for cycle K
	for(p = 0; p < numResourceType; p++){
		tempResourceArray[p] = (struct resource*)malloc(sizeof(struct resource));
		tempResourceArray[p]->resourceType = resourceArray[p] -> resourceType;
		tempResourceArray[p]->taskAssigned = -5;
		tempResourceArray[p]->totalUnits = -5;
		tempResourceArray[p]->unitsAvai = 0;
	}//end for

	//check for	pending tasks that can be release after every cycle
	//pending tasks request are granted in order, so must check the entire block Q
	b_pendingRelease(resourceArray, numResourceType, taskArray, numTask, max, blockQ, sizeQ, verboseFlag);

	for (i = 0; i < numTask; i++){
		//resources must stay constant throughout cycle k, so use this temp array for request beacuse
		//after release, resourceArray gets changed

		//get current task
		struct task *curTask = taskArray[i];
		//get the task's activity
		struct activity *curActivity = &(curTask -> activities[curTask->curNumActivity]);

		//if task has not terminated, and not waiting, not aborted, not recently released THEN get the next activity
		//if task recently released pending Q, request has already been granted so skip the entire block
		if(curTask-> terminated == 0 && curTask -> pending == 0 && curTask -> aborted == 0 && curTask -> releasedFromQ == 0){
			if(curActivity -> action == 0){
				int resType = curActivity -> resType;
				int initialClaim = curActivity -> reqUnits;
				//abort process if initialClaim exceeds resource present
				if(resourceArray[resType-1] -> unitsAvai < initialClaim){
					curTask -> pending = 0;
					curTask -> waitingFor = -1;
					curTask -> unitWaiting = 0;
					curTask -> terminated = 1;
					curTask -> deadlock = 0;
					curTask -> aborted = 1;
					if(verboseFlag == 1){
						printf("Task %d aborted (claim exceeds total system)\n",curTask -> id);
					}
				}
				else{
					curTask->curNumActivity = curTask -> curNumActivity + 1;
					if(verboseFlag == 1){
						printf("Task %d complete its initiation\n", curTask -> id);
					}
				}
			}

			//if activity is request
			else if(curActivity -> action == 1){
				int flag = b_canRequest(resourceArray, numResourceType, taskArray, numTask, max, curTask, curActivity);

				if(flag == 0){
					//if request cannot be satisfied, put task in block queue and check deadlock
					//task still on the same request activity
					b_addToQ(curTask, blockQ, sizeQ);
					curTask -> pending = 1;
					//do not move to next activity since request not satisfied

				}
				//if request can be satisfied, move to next activity
				else if(flag == 1){
					//move to next activity if not terminated
					b_runRequest(curTask, curActivity, resourceArray, verboseFlag);
					if(verboseFlag == 1){
						printf("\n");
					}
					curTask->curNumActivity = curTask -> curNumActivity + 1;
				}
				else if(flag == 2){
					//asked for too much, error, abort Task
					b_helperRelease(curTask, numResourceType, resourceArray);
					curTask -> aborted = 1;
					if(verboseFlag == 1){
						printf("Task %d aborted (request exceeds claims)\n",curTask -> id);
					}
				}

			}

			//if activity is release
			else if(curActivity -> action == 2){
				b_runRelease(curTask, curActivity, resourceArray, ptCycle, tempResourceArray, verboseFlag);
				//move to next activity if not terminated
				curTask->curNumActivity = curTask -> curNumActivity + 1;

			}

			//if activity is compute
			else if(curActivity -> action == 3){
				//if compute time is -1 as intialized, else will execute
				if(curTask -> computeTime == -1){
					//compute time was saved as res type when file loaded
					curTask -> computeTime = curActivity -> resType;
					//count this as 1 cycle
					curTask -> computeTime = curTask -> computeTime -  1;
					if(verboseFlag == 1){
						printf("Task %d has %d cycles left to compute\n", curTask -> id, curTask -> computeTime);
					}
				}
				else if(curTask -> computeTime > 0){
					curTask -> computeTime = curTask -> computeTime - 1;
					if(verboseFlag == 1){
						printf("Task %d has %d cycles left to compute\n", curTask -> id, curTask -> computeTime);
					}
				}
				//move to next activity if finishes computing
				if(curTask -> computeTime == 0){
					curTask->curNumActivity = curTask -> curNumActivity + 1;
					//reset computeTime to  -1
					curTask->computeTime = -1;
				}

			}

		}//_end if

		//if task terminates
		if(curTask -> activities[curTask ->curNumActivity].action == 4 && curTask -> releasedFromQ == 0){
			//ensure that we are only looking at task that is terminating on this cylce not previously terminated task
			if(curTask -> alreadyPrintedTerminate == 0){
				curTask -> terminateTime = *ptCycle + 1;
				if(verboseFlag == 1){
					printf("Task %d terminates at %d.\n", curTask -> id, curTask -> terminateTime);
				}
				//release all its resources
				b_helperRelease(curTask, numResourceType, resourceArray);
				curTask -> terminated = 1;
				curTask -> alreadyPrintedTerminate = 1;
				curTask -> pending = 0;
				curTask -> waitingFor = -1;
				curTask -> unitWaiting = 0;
			}
		}

		//if task is block, print that it cannot be granted
		if(curTask -> pending == 1){
			//curTask -> waitingTime = curTask -> waitingTime + 1;
			curTask -> waitingTime = curTask -> waitingTime + 1;
			if(verboseFlag == 1){
				printf("Task %d's request cannot be granted (not safe), so task is blocked.\n", curTask->id);
			}
		}

		//free pointers
		//reset
		curTask -> releasedFromQ = 0;


	}//_end for, done looping through every task


	//add release resource from this cycle so it becomes available in cycle k+1
	//step 3: increase the amount of available units in resource
	int p1;
	if(verboseFlag == 1){
		printf("resource available|");
	}

	for(p1 = 0; p1 < numResourceType; p1++){
		resourceArray[p1] -> unitsAvai = resourceArray[p1] -> unitsAvai + tempResourceArray[p1]->unitsAvai;
		if(verboseFlag == 1){
			printf("R%d:%d  ", p1+1, resourceArray[p1]-> unitsAvai);
		}
	}
	if(verboseFlag == 1){

		printf("\n");
	}

	*ptCycle = *ptCycle + 1;

	for(p = 0; p < numResourceType; p++){
		free(tempResourceArray[p]);
	}//end for


}


/**
 * This function finds first task in blockq that can be granted resource and then removes it form blockq
 * */
void b_pendingRelease(struct resource **resourceArray, int numResourceType, struct task **taskArray, int numTask, int **max, int *blockQ, int sizeQ, int verboseFlag){
	//blockQ ends when -1 is found
	int count = 0;
	int taskId = blockQ[count];
	//loop through entire blockq
	while(taskId != -1){
		//printf("in while..\n");
		struct task *curTask = taskArray[taskId-1];
		struct activity *curActivity = &(curTask -> activities[curTask->curNumActivity]);
		//error checking, make sure its pending and activity is request
		if(curActivity -> action != 1){
			printf("ERROR ACTION IS NOT REQUEST\n");
		}
		else if(curTask -> pending != 1){
			printf("ERROR TASK %d IS NOT PENDING\n", curTask -> id);
		}
		//OK - check if request can be granted
		else{

			int flag = b_canRequest(resourceArray, numResourceType, taskArray, numTask, max, curTask, curActivity);
			//if request can be satisfied, move to next activity
			if(flag == 1){
				b_runRequest(curTask, curActivity, resourceArray, verboseFlag);
				//move to next activity if not terminated
				curTask->curNumActivity = curTask -> curNumActivity + 1;
				//change to non pending status and remove id from block q

				curTask->pending = 0;
				curTask->releasedFromQ = 1;
				blockQ[count] = -1;
			}
		}
		//check the next task
		count++;
		taskId = blockQ[count];
	}//end while loop only after all blocked tasks have been checked

	b_shiftBlockQ(blockQ, sizeQ);
}


void b_shiftBlockQ(int *blockQ, int sizeQ){


	//initialize tempQ to all -1's
	int tempQ[sizeQ];
	int counter1;
	for(counter1 = 0; counter1 < sizeQ; counter1++){
		tempQ[counter1] = -1;

	}

	int i;
	int count = 0;
	for(i=0; i < sizeQ; i++){
		if(blockQ[i] != -1){
			tempQ[count] = blockQ[i];
			count++;
		}
	}

	int counter2;
	for(counter2 = 0; counter2 < sizeQ; counter2++){
		blockQ[counter2] = tempQ[counter2];
	}

}

void b_helperRelease(struct task *curTask, int numResourceType, struct resource **resourceArray){
	//step 1: release all the resources from task and add to available
	int i;

	for(i = 0; i < numResourceType; i++){
		int units = curTask -> currentlyAllocated[i];
		resourceArray[i] -> unitsAvai = resourceArray[i] -> unitsAvai + units;
		curTask -> currentlyAllocated[i] = 0;
	}//end_for
	//step 2: mark process aborted and terminated
	curTask -> pending = 0;
	curTask -> waitingFor = -1;
	curTask -> unitWaiting = 0;
	curTask -> terminated = 1;

}

void b_addToQ(struct task *curTask, int *blockQ, int sizeQ){

	//find the index of the next available slot, add id to Q, and mark task pending

	int i;
	for (i = 0; i < sizeQ; i++){
		if(blockQ[i] == -1){
			blockQ[i] = curTask -> id;
			//curTask -> pending = 1;
			break;
		}
	}//end_for

}


void b_runRequest(struct task *curTask, struct activity *curActivity, struct resource **resourceArray, int verboseFlag){

		int resType = curActivity -> resType;
		int reqUnits = curActivity -> reqUnits;

		//step1: allocate resource to task
		struct resource *curResource = resourceArray[resType-1];
		curResource -> taskAssigned = curTask -> id;

		//step2: reduce the amounts of available units
		curResource -> unitsAvai = curResource -> unitsAvai - reqUnits;

		//step3: update allocated units in task
		curTask -> currentlyAllocated[resType-1] = curTask -> currentlyAllocated[resType -1] + reqUnits;
		if(verboseFlag == 1){
			printf("Task %d complete its request (resource[%d]): requested = %d, remaining = %d\n", curTask -> id, curResource -> resourceType, reqUnits,
			curResource -> unitsAvai);
		}

}

int b_canRequest(struct resource **resourceArray, int numResourceType, struct task **taskArray, int numTask, int **max, struct task *curTask, struct activity *curActivity){

	//<<<<<<<SET UP>>>>>>>>
	//step 1: create available array - number of available resources of each type
	int available[numResourceType];
	int i;
	for (i = 0; i < numResourceType; i++){
		available[i] = resourceArray[i] -> unitsAvai;
	}


	//step 2: create allocation array - the number of resources of each type currently allocated to each process
	int allocation[numTask][numResourceType];
	int k, g;
	for(k = 0; k < numTask; k++){
		struct task *curTask = taskArray[k];
		for(g = 0; g < numResourceType; g++){
			allocation[k][g] = curTask -> currentlyAllocated[g];
		}
	}

	int j;


	//step 3: create need array - max number resource each task may still request
	int need[numTask][numResourceType];
	for(k = 0; k < numTask; k++){
			struct task *curTask = taskArray[k];
			for(g = 0; g < numResourceType; g++){
				int maxNum = *(*(max+k)+g);
				//if task aborted its claim should be 0, so it wont be accounted for
				if(curTask -> aborted || curTask -> terminated){
					maxNum = 0;
				}
				need[k][g] = maxNum - allocation[k][g];
			}
	}

	//<<<<<<<< ERROR CHECK >>>>>>>>>>>>>>>>>
	//in this lab, can only do one request at a time
	int resType = curActivity -> resType;
	int reqUnits = curActivity -> reqUnits;
	if(reqUnits > need[curTask->id - 1][resType-1]){
		//task's request exceeds its claim return error
		return 2;
	}

	//resources are able to satisfy the request if it reaches this point
	//<<<<<<< ASSUME WE SATISFY THE REQUEST>>>>>>>>>>>>>>

	//step 1: reduce available resource
	available[resType-1] = available[resType-1] - reqUnits;
	//step 2: increase allocation for Task
	allocation[curTask->id-1][resType-1] = allocation[curTask->id-1][resType-1] + reqUnits;
	//step 3: resource grant, so task needs less
	need[curTask->id-1][resType-1] = need[curTask->id-1][resType-1] - reqUnits;

	//check if this would leave us in a safe state: if yes grant, if not cause it to wait
	//<<<<<<<< SAFETY ALGORITHM >>>>>>>>>>>>>>

	//<<SET UP>>
	//step 1: set work array equal to available
	int work[numResourceType];
	for(i = 0; i < numResourceType; i++){
		work[i] = available[i];
	}

	//step 2: create finish array - boolean arrays, true if task i's request can be satisfied=
	int finish[numTask];
	//initializing, assaume all is finished
	for(i=0; i < numTask; i++){
		finish[i] = 0;
	}

	//<<BEGIN CHECK>>
	//step 1: find a process that can complete its request
	int i1;

	for(i = 0; i < numTask; i++){
		//find a task that hasn't finished it request

		if(finish[i] == 0){
			//if task request is less than or equal to what's available
			for(j = 0; j < numResourceType; j++){
				if(need[i][j] > work[j]){
					break;
				}
			}


			//Step 2: we found a satisfyable task if the inner for loop finishes all its iteration
			if(j == numResourceType){

					//request will be granted
					finish[i] = 1;
					//assume this process were to finsih and its allocation back to the available list
					for(i1 = 0; i1 <numResourceType; i1++){
						work[i1] = work[i1] + allocation[i][i1];
					}
					//go back to step 1, so set i = -1 to restart from beginning of the list
					i = -1;
				}
			}
		}//end form

		//step 3: check if the state is safe
		int safe = 1;
		for(i = 0; i < numTask; i++){
			if(finish[i] == 0){

				safe = 0;
				break;
			}
		}

		if(safe == 0){
			return 0;
		}
		else{
			return 1;
		}

}

void b_runRelease(struct task *curTask, struct activity *curActivity, struct resource **resourceArray, int *ptCycle, struct resource **tempResourceArray, int verboseFlag){

	//step 1: get the resource type to release from activity
	int resType = curActivity -> resType;
	int unitsRelease = curActivity -> reqUnits;

	//step 2: release resource from task
	curTask -> currentlyAllocated[resType-1] = curTask -> currentlyAllocated[resType-1] - unitsRelease;


	//step 3: store the amount of units release in tempResourceArray
	struct resource *curResource = tempResourceArray[resType-1];
	curResource -> unitsAvai = curResource -> unitsAvai + unitsRelease;

	if(verboseFlag == 1){
		printf("Task %d complete its release (resource[%d]): released = %d, available next cycle = %d, available at cycle = %d\n", curTask -> id,
		curResource -> resourceType, unitsRelease, resourceArray[resType-1] -> unitsAvai + unitsRelease, *ptCycle+1);
	}



}//_end run release
