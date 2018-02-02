## Program Overview
This program simulates scheduling in order to see how the time required depends on the scheduling algorithm and the request patterns.

## Scheduling Algorithms
* First Come First Serve
* Round Robin with quantum = 2
* Uniprogrammed (just one process active). When it is blocked, the system waits.
* Shortest Job First (This is not preemptive, but is not run to completion, i.e. switch on I/O bursts).

## Breaking Ties
Many jobs can have the same ‘‘priority’’. For example in RR jobs can become ready at the same cycle, and an order must be decided to insert them onto the FIFO ready queue. Ties are broken by favoring the process with earliest arrival time A. If the arrival times are the same for two processes with the same priority, the process that is listed earliest in the input is favored.

## Output

#### At the end of each run, the following is printed for each process:
* Finishing time.
* Turnaround time (i.e., finishing time - A).
* I/O time (i.e., time in Blocked state).
* Waiting time (i.e., time in Ready state).

#### At the end of the program, the following is printed:
* Finishing time (i.e., when all the processes have finished).
* CPU Utilization (i.e., percentage of time some job is running).
* I/O Utilization (i.e., percentage of time some job is blocked).
* Throughput, expressed in processes completed per hundred time units.
* Average turnaround time.
* Average waiting time.

## Program Specifications
1. My program has 7 classes: Driver, FCFS_V3_test, Processes, RandomFile, RR_V2, SFJ, and Uniprogram.
2. Run the program from Driver class.
3. My program accepts either 1 or 2 arguments from command line.
4. If one argument is given, it will run in normal mode.
5. If two arguments are given, it will run in verbose mode.
6. My program will run all algorithms on a given input file in the following order: FCFS, RR, Uniprocessing, and SJF.
7. The random file is included in the program. The filepath is hardcoded in RandomFile class.
8. Press play, sit back relax and enjoy the statistics.
