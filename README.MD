# Simulation of Operating Systems Functions

I created a simulation of the 2-pass linker used in C compilers and simulations of 3 crucial operating system functions. OS-level simulations include process scheduling, resource allocation, and demand paging.

## Linker
When the compiler and assembler have finished processing a module, they produce an object module that is almost runnable. There are two remaining tasks to be accomplished before object modules can be run. Both are involved with linking together multiple object modules. The tasks are relocating relative addresses and resolving external references. The "C-Linker" program simulates how these two remaining tasks work.

## Process Scheduling
One of the responsibilities of the OS is to schedule processes. First come first serve, Round Robin with quantum = 2, Uniprogrammed, and Shortest Job First have been selected for simulation. A summary of these algorithms can be found in the README inside the process scheduling directory.

## Resource Allocation
The simulation performs resource allocation using both optimistic resource manager and banker's algorithm of Dijkstra. A deadlock can occur for the optimistic resource manager and the simulation handles the deadlock by aborting the lowest numbered deadlocked task after releasing all its resources (continues this until there are no more deadlocks).

A deadlock occurs when every member of a set of processes is waiting for an event that can only be caused by a member of the set. Often the event waited for is the release of a resource.

## Demand Paging
Demand paging is a part of memory management. The idea of demand paging is to fetch pages from disk to memory when they are referenced, hoping to get the most actively used pages in memory. With demand paging, an important question is the choice of a victim page to evict. The simulation accepts LIFO, RANDOM, and LRU as replacement algorithms. 

## Credits
These notes were adapted from Professor Allan Gottlieb, an Operating Systems professor at NYU.
