#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "objects.h"
#include "header.h"
void fifo(struct task **taskArray, int numTask, struct resource **resourceArray, int numResourceType, int verboseFlag){

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
	while(allTerminated(taskArray, numTask) == 0){
		runOneCycle(taskArray, numTask, &cycle, resourceArray, numResourceType, blockQ, sizeQ, verboseFlag);
	}
	printOutput(taskArray, numTask, cycle);

}

void printOutput(struct task **taskArray, int numTask, int cycle){
	printf("------------------\n");
	printf("FIFO\n");
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

int allTerminated(struct task **taskArray, int numTask){
	int i;
	for (i = 0; i < numTask; i++){
		if(taskArray[i] -> terminated == 0){
			return 0;
		}
	}
	return 1;
}

void runOneCycle(struct task **taskArray, int numTask, int *ptCycle, struct resource **resourceArray, int numResourceType, int *blockQ, int sizeQ, int verboseFlag){
	int i;
	//raise flag if task terminate, so no cycle will be added
	int taskTerminate = 0;


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
	//create temp array
	for(p = 0; p < numResourceType; p++){
		tempResourceArray[p] = (struct resource*)malloc(sizeof(struct resource));
		tempResourceArray[p]->resourceType = resourceArray[p] -> resourceType;
		tempResourceArray[p]->taskAssigned = -5;
		tempResourceArray[p]->totalUnits = -5;
		tempResourceArray[p]->unitsAvai = 0;
	}//end for

	//check to see if there are any tasks that were released at the end of cycle k-1
	//from blockQ before deadlock
	//if there are, run those first because those were "supposed" to be release at cycle k from pendingrelease
	runCheckFirst(taskArray, numTask, resourceArray, verboseFlag);

	//check for	pending tasks that can be release after every cycle
	//pending tasks request are granted in order, so must check the entire block Q
	pendingRelease(resourceArray, numResourceType, taskArray, numTask, blockQ, sizeQ, verboseFlag);

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
				curTask -> initialClaim = curActivity -> reqUnits;
				curTask->curNumActivity = curTask -> curNumActivity + 1;
				if(verboseFlag == 1){
					printf("Task %d complete its initiation\n", curTask -> id);
				}

			}

			//if activity is request
			else if(curActivity -> action == 1){
				int flag = runRequest(curTask, curActivity, resourceArray, verboseFlag);
				//if request can be satisfied, move to next activity
				if(flag == 1){
					//move to next activity if not terminated
					curTask->curNumActivity = curTask -> curNumActivity + 1;
				}
				//if request cannot be satisfied, put task in block queue and check deadlock
				//task still on the same request activity
				else {
					addToQ(curTask, blockQ, sizeQ);
					curTask -> pending = 1;
					//do not move to next activity since request not satisfied

				}

			}

			//if activity is release
			else if(curActivity -> action == 2){
				runRelease(curTask, curActivity, resourceArray, ptCycle, tempResourceArray, verboseFlag);
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
			taskTerminate = 0;
		}//_end if

		//if task terminates
		if(curTask -> activities[curTask ->curNumActivity].action == 4 && curTask -> releasedFromQ == 0){
			//ensure that we are only looking at task that is terminating on this cylce not previously terminated task
			if(curTask -> alreadyPrintedTerminate == 0){
				curTask -> terminateTime = *ptCycle + 1;
				if(verboseFlag == 1){
					printf("Task %d terminates at %d.\n", curTask -> id, curTask -> terminateTime);
				}

				curTask -> terminated = 1;
				curTask -> alreadyPrintedTerminate = 1;
				//if it is the last task we must add a cycle
				if(curTask -> id == numTask){
					taskTerminate = 0;
				}
				else{
					taskTerminate = 1;
				}

			}
		}

		//if task is block, print that it cannot be granted
		if(curTask -> pending == 1){
			//curTask -> waitingTime = curTask -> waitingTime + 1;
			curTask -> waitingTime = curTask -> waitingTime + 1;
			if(verboseFlag == 1){
						printf("Task %d's request cannot be granted\n", curTask->id);
			}

			taskTerminate = 0;
		}

		//free pointers
		//reset
		curTask -> releasedFromQ = 0;
		
		

	}//_end for


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

	//check for deadlock ONLY after all request within this cycle have processed
	checkWithFutureResource(taskArray, resourceArray, numResourceType, blockQ,sizeQ);
	int state = deadlockDetection(resourceArray, numResourceType, taskArray, numTask);
	while(state == 1){
		int pAborted = abortProcess(taskArray, numTask, resourceArray, numResourceType, blockQ, sizeQ);
		if(verboseFlag == 1){
			printf("According to the spec task %d is aborted now and its resources are availabe at cycle %d\n", pAborted+1, *ptCycle+1);
		}
		//check if other processes are still deadlock
		checkWithFutureResource(taskArray, resourceArray, numResourceType, blockQ,sizeQ);
		state = deadlockDetection(resourceArray, numResourceType, taskArray, numTask);
	}

	//if task not terminate, then add one cycle
	if(taskTerminate == 0){
		*ptCycle = *ptCycle + 1;
	}

	for(p = 0; p < numResourceType; p++){
		free(tempResourceArray[p]);
	}//end for

}//end_runOneCycle

void checkWithFutureResource(struct task **taskArray, struct resource **resourceArray, int numResourceType, int* blockQ, int sizeQ){

		struct resource *tempResourceArray[numResourceType];
		int i;
		for(i = 0; i < numResourceType; i++){
			tempResourceArray[i] = (struct resource*)malloc(sizeof(struct resource));
			tempResourceArray[i]->resourceType = resourceArray[i] -> resourceType;
			tempResourceArray[i]->taskAssigned = resourceArray[i] -> taskAssigned;
			tempResourceArray[i]->totalUnits = resourceArray[i] -> totalUnits;
			tempResourceArray[i]->unitsAvai = resourceArray[i] -> unitsAvai;
		}


		//step1: loop through blockQ, see if any process can get resource
		int c1;
		for(c1 = 0; c1 < sizeQ; c1++){
			//empty!
			if(blockQ[c1] == -1){
				break;
			}
			else{
				int taskId = blockQ[c1];
				struct task *curTask = taskArray[taskId-1];
				struct activity *curActivity = &(curTask -> activities[curTask->curNumActivity]);
				if(curActivity -> action != 1){
					printf("ERROR ACTIVITY NEEDS TO BE 1");
				}
				//check if request can be granted since resources have been recently released -- don't actually grant resources, only checking
				//we actually grant it next cycle when resources become available
				//if yes, change pending so there won't be deadlock
				if(requestHelper(curTask, curActivity, tempResourceArray) == 1){
					//since I pretended to grant the resource, I need to adjust how much I granted so I can check
					//if next task in Array can be unblock next as well
					//get the resource type and units requestd
					int resType = curActivity -> resType;
					int reqUnits = curActivity -> reqUnits;
					tempResourceArray[resType-1] -> unitsAvai = tempResourceArray[resType-1] -> unitsAvai - reqUnits;

					//unblock task from pending
					curTask -> pending = 0;
					//remove from blockQ
					removeFromBlockQ(curTask -> id, sizeQ, blockQ);
					//set check first to true -- because this task is supposed to be release at the beginnig of cycle k+1
					//but we are releasing it at the end of cycle k
					//so this task has to be checked before other activities when cycle k+1 is reached
					curTask -> checkFirst = c1;
				}


			}
		}
		//free
		for(i = 0; i < numResourceType; i++){
			free(tempResourceArray[i]);
		}
}

/**
 * organize check first array in the same order release tasks were in blockQ
 * checkfirst stores number which one was released first
**/
void runCheckFirst(struct task **taskArray, int numTask, struct resource **resourceArray, int verboseFlag){
	//step 1: create a 2d array with each element as [checkTask value, taskId]
	//count the number of check
	int size = 0;
	int i;
	for(i = 0; i < numTask; i++){
		if(taskArray[i] -> checkFirst >= 0){
			size++;
		}
	}


	//step 1: filter out all the tasks that has check first marked true and store in array
	int array[size][2];
	int b;
	for(b = 0; b < size; b++){
		int b1;
		for(b1 = 0; b1 < 2; b1++){
			array[b][b1] = -1;
		}
	}

	/*
		printf("after initializations\n");
		int k; int count1;
		 for (k = 0; k < 2; k++) {
			  for(count1 = 0; count1 < 2; count1++){
				  printf("%d", array[k][count1]);
			  }
			  printf("\n");
		   }
*/

	int count = 0;
	for(i=0; i < numTask; i++){
		if(taskArray[i] -> checkFirst >= 0){
			array[count][0] = taskArray[i] -> checkFirst;
			array[count][1] = taskArray[i] -> id;
			count++;

		}
	}
	//step 2: sort
	bubbleSort(array,size);
	//step 3: loop through array get task id and run in that order

	for(i = 0; i < size; i++){
		int id = array[i][1];

			//get current task
			struct task *curTask = taskArray[id-1];
			//get the task's activity
			struct activity *curActivity = &(curTask -> activities[curTask->curNumActivity]);
			if(curActivity -> action == 1){


				int flag = runRequest(curTask, curActivity, resourceArray, verboseFlag);
				//if request can be satisfied, move to next activity
				if(flag == 1){
					//move to next activity if not terminated
					curTask->curNumActivity = curTask -> curNumActivity + 1;
					curTask->checkFirst = -1;
					curTask->releasedFromQ = 1;
				}
				//if request cannot be satisfied, put task in block queue and check deadlock
				//task still on the same request activity
				else {
					printf("ERROR REQUEST WAS SUPPOSED TO BE SATISFIED");
				}

			}
		}
}

void bubbleSort(int iarr[][2], int num) {

   int i, j, tempi, tempj;
   for (i = 1; i < num; i++) {
      for (j = 0; j < num - 1; j++) {
         if (iarr[j][0] > iarr[j + 1][0]) {
            tempi = iarr[j][0];
            tempj = iarr[j][1];

            iarr[j][0] = iarr[j+1][0];
            iarr[j][1] = iarr[j+1][1];

            iarr[j+1][0] = tempi;
            iarr[j+1][1] = tempj;



         }
      }
  }

}


/**
 * This function finds first task in blockq that can be granted resource and then removes it form blockq
 * */
void pendingRelease(struct resource **resourceArray, int numResourceType, struct task **taskArray, int numTask, int *blockQ, int sizeQ, int verboseFlag){
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

			int flag = runRequest(curTask, curActivity, resourceArray, verboseFlag);
			//if request can be satisfied, move to next activity
			if(flag == 1){

				//move to next activity if not terminated
				curTask->curNumActivity = curTask -> curNumActivity + 1;
				//change to non pending status and remove id from block q

				curTask->pending = 0;
				curTask->releasedFromQ = 1;
				blockQ[count] = -1;
			}

			//curTask -> waitingTime = curTask -> waitingTime + 1;
		}
		//check the next task
		count++;
		taskId = blockQ[count];
	}//end while loop only after all blocked tasks have been checked

	shiftBlockQ(blockQ, sizeQ);

}

void shiftBlockQ(int *blockQ, int sizeQ){
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

int abortProcess(struct task **taskArray, int numTask, struct resource **resourceArray, int numResourceType, int *blockQ, int sizeQ){
	//step 1:find deadlock process within lowest id
	int i;
	for(i = 0; i < numTask; i++){
		if(taskArray[i] -> deadlock == 1){
			break;
		}
	}


	//step 2: index i is the process to abort, so go release its resource
	helperRelease(taskArray[i], numResourceType, resourceArray);


	//step 3: remove aborted process from blockQ and shift Q
	int taskId = taskArray[i] -> id;
	removeFromBlockQ(taskId, sizeQ, blockQ);
	shiftBlockQ(blockQ, sizeQ);

	//printf("leaving abort process....\n");

	return i;
}

void removeFromBlockQ(int taskId, int sizeQ, int *blockQ){

	int found = 0; //for error checking
	int j;
	for(j=0; j < sizeQ; j++){
		if(blockQ[j] == taskId){
			//printf("FOUND!!!\n");
			found = 1;
			blockQ[j] = -1;
			break;
		}
	}//end for
	//for error checking
	if(found == 0){
		printf("ERROR NOT FOUND IN Q\n");
	}

}


void helperRelease(struct task *curTask, int numResourceType, struct resource **resourceArray){
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
	curTask -> deadlock = 0;
	curTask -> aborted = 1;

}






void addToQ(struct task *curTask, int *blockQ, int sizeQ){

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



int runRequest(struct task *curTask, struct activity *curActivity, struct resource **resourceArray, int verboseFlag){

	//check if request can be granted
	//if request can be granted
	if(requestHelper(curTask, curActivity, resourceArray) == 1){
		int resType = curActivity -> resType;
		int reqUnits = curActivity -> reqUnits;

		//else grant the request
		//step1: allocate resource to task
		struct resource *curResource = resourceArray[resType-1];

		//printf("assigned step 1\n");
		curResource -> taskAssigned = curTask -> id;

		//printf("complete step 1\n");
		//step2: reduce the amounts of available units
		curResource -> unitsAvai = curResource -> unitsAvai - reqUnits;

		//printf("complete step 2\n");
		//step3: update allocated units in task
		curTask -> currentlyAllocated[resType-1] = curTask -> currentlyAllocated[resType -1] + reqUnits;
		if(verboseFlag == 1){
			printf("Task %d complete its request (resource[%d]): requested = %d, remaining = %d\n", curTask -> id, curResource -> resourceType, reqUnits,
			curResource -> unitsAvai);
		}

		//step4: return boolean to show request is satisfied
		return 1;
	}
	//request cannot be satisfied
	else{

		return 0;

	}


}
/**
 * This function checks if a request can be granted
 * */
int requestHelper(struct task *curTask, struct activity *curActivity, struct resource **resourceArray){
	//get the resource type and units requestd
	int resType = curActivity -> resType;
	int reqUnits = curActivity -> reqUnits;

	//check if resource has enough units
	//request cannot be granted if there are not enough available units
	if (reqUnits > resourceArray[resType-1] -> unitsAvai) {
		curTask -> waitingFor = resType;
		curTask -> unitWaiting = reqUnits;
		return 0;
	}
	else{
		return 1;
	}
}

void runRelease(struct task *curTask, struct activity *curActivity, struct resource **resourceArray, int *ptCycle, struct resource **tempResourceArray, int verboseFlag){

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

int deadlockDetection(struct resource **resourceArray, int numResourceType, struct task **taskArray, int numTask){
	//if all non terminating proesses are pending
	//if process terminate or abort they do not count, so treat them as pending --> give 1
	//if process pending --> give 1
	//if process is non terminated non aborted and not pending --> flag

	//case 1: check - all the processes have either aborted or terminated, so no deadlock
	int allCompleted = 1;
	int c2;
	for(c2 = 0; c2 < numTask; c2++){

		if(taskArray[c2] -> aborted == 0 && taskArray[c2] -> terminated == 0){
			allCompleted = 0;
			break;

		}
	}
	//all processes completed, no deadlock
	if(allCompleted ==1){
		return 0;
	}
	//not all processes have completed so find out if any non pending process exist
	else{
		int foundDeadlock = 1;
		int c1;
		for(c1 = 0; c1 < numTask; c1++){
			if(taskArray[c1] -> aborted == 0 && taskArray[c1]-> terminated == 0 && taskArray[c1]-> pending == 0){
				foundDeadlock = 0;
				break;
			}
		}

		//deadlock - the first non termating, non aborted and non pending is the process to abort
		if(foundDeadlock == 1){

			int c3;
			for(c3 = 0; c3 < numTask; c3++){
				if(taskArray[c3] -> pending == 1){
					taskArray[c3] -> deadlock = 1;
				}
			}
			return 1;
		}
		//not deadlock
		else{
			return 0;
		}

	}


}
