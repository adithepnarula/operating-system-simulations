1. Attached are five source code: banker_final.c, fifo_final.c, main_final.c, _objects.h, header.h

2. To compile my program issue type the following command in terminal:
>> gcc -Wall -g -o program main_final.c fifo_final.c banker_final.c 

3. To run my program type the following command in terminal:
	(for pc) >> program.exe input11.txt
	(for unix) >> ./program input11.txt

4. My program accepts a verbose flag. To run in verbose mode:
	(for pc) >> program.exe input11.txt --verbose
	(for unix) >> ./program input11.txt --verbose

5. Some limitations: 
I set the highest number of activities per task to 50. This means each task can have a maximum of 50 activities (ie. request, release, etc.).
You can change the number of activities by going to line 53 in main_final.c and simply change from "numberOfElements = 50" to "numberOfElements = 100" or
how ever many you'd like. 

I set the highest number of pending tasks to be 100. To change this simply go to banker_final.c and fifo_final.c and on line 11, switch
"sizeQ = 100" to whatever number you'd like.

6. Compile, run, and as usual, sit back, relax, and enjoy. 
