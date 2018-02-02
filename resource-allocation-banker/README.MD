## Program Overview
The program performs resource allocation  using both an optimistic resource manager and the banker’s algorithm of Dijkstra. The optimistic resource manager follows the simple principle: satisfy a request if possible, if not the task waits; when a release occurs, pending requests are satisfied in a FIFO manner.

My program takes one command line argument, the name of file containing the input. After reading (all) the input, the program performs two simulations: one with the optimistic manager and one with the banker. Output is written to stdout (the screen).

## Error Checks
In the banker’s algorithm, if a task’s initial claim exceeds the resources present or if, during execution, a task’s requests exceed its claims, an informative message is printed, then task gets aborted, and all the resources get released. This does not apply to the optimistic allocator since it does not have the concept of initial claim.

## Deadlock
Deadlock cannot occur for banker’s algorithm, but can for the optimistic resource manager. If deadlock is detected, a message is printed and the lowest numbered deadlocked task is aborted after releasing all its resources. If deadlock remains, an error message is printed and the next lowered numbered deadlocked task is aborted, etc..

## Program Specifications
1. Attached are five source code: banker_final.c, fifo_final.c, main_final.c, objects.h, header.h

2. To compile my program issue type the following command in terminal:
````
gcc -Wall -g -o program main_final.c fifo_final.c banker_final.c
````

3. To run my program type the following command in terminal:
For PC
```
program.exe input11.txt
```
For UNIX
```
 ./program input11.txt
```

4. My program accepts a verbose flag. To run in verbose mode:

For PC
```
program.exe input11.txt --verbose
```
For UNIX
```
./program input11.txt --verbose
```

5. Some limitations:
I set the highest number of activities per task to 50. This means each task can have a maximum of 50 activities (ie. request, release, etc.).
You can change the number of activities by going to line 53 in main_final.c and simply change from "numberOfElements = 50" to "numberOfElements = 100" or how ever many you'd like.

I set the highest number of pending tasks to be 100. To change this simply go to banker_final.c and fifo_final.c and on line 11, switch
"sizeQ = 100" to whatever number you'd like.
