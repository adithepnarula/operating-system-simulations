## Program Overview
I simulated demand paging to see how the number of page faults depends on page size, program size, replacement algorithms, and job mix.

The idea is to have a driver generate memory references and the demand paging simulator decides if each reference causes a page fault. The driver reads all input, simulates N memory references per program, and produces all output. The driver uses round robin scheduling with quantum q=3. 


## Program Specifications

1. There are 5 "primary" classes: Driver, VirtualPage, RandomFile, Process, and PageFrame
2. There are 3 "algorithms" classes: LIFO, LRU, and Random
3. The driver is the starting point of the program
4. Go to driver and input your command line argument.
5. The command line accepts a string of 7 arguments in the following format:

```
10 10 20 1 10 lru 0
```
The first argument is machine size, second is page size, third is process size, fourth is job mix, fifth is number of references per process, and sixth is replacement algorithm, and the last is just a dummy which is ignored
