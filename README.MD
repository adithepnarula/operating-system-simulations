## Simulation of Operating Systems Functions

I created a simulation of the 2-pass linker used in C compilers and simulations of 3 crucial operating system functions. OS-level simulations include process scheduling, resource allocation, and demand paging.

## Linker
When the compiler and assembler have finished processing a module, they produce an object module that is almost runnable. There are two remaining tasks to be accomplished before object modules can be run. Both are involved with linking together multiple object modules. The tasks are relocating relative addresses and resolving external references. The "C-Linker" program simulates how these two remaining tasks work.

## Process Scheduling
One of the responsibilities of the OS is to schedule processes. First come first serve, Round Robin with quantum = 2, Uniprogrammed, and Shortest Job First have been selected for simulation. Summaries of these algoritms can be found in the README inside the process scheduling directory. 

## Resource Allocation


## Demand Paging

## Credits
These notes were adapted from Professor Allan Gottlieb, an Operating Systems professor at NYU.
