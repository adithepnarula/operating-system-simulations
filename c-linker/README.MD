## Overview of Program
I implemented a simulation of two-pass linker used in the C compiler. The linker processes the input twice. Pass one determines the base address for each module and the absolute address for each external symbol, storing the later in the symbol table it produces. The first module has base address zero; the base address for module I+1 is equal to the base address of module I plus the length of module I. The absolute address for symbol S defined in module M is the base address of M plus the relative address of S within M. Pass two uses the base addresses and the symbol table computed in pass one to generate the actual output by relocating relative addresses and resolving external references.

## Error detection and arbitrary limits
The following are the errors/warnings which are detected:

* If a symbol is multiply defined, an error message is printed and the second definition is used.
* If a symbol is used but not defined, an error is printed and the value 111 is given.
* If a symbol is defined but not used, a warning message is printed and the program continues.
* If multiple symbols are listed as used in the same instruction, an error message is printed and all symbols are ignored except the last
* If an address appearing in a definition exceeds the size of the module, an error message is printed and the address is treated as the last word in the module.
* If an address appearing in a use list exceeds the size of the module, an error message is printed and this particular use is ignored.
* If an absolute address exceeds the size of the machine, an error message is printed and the largest legal value is used.
* If a relative address exceeds the size of the module, an error message is printed the last address of the module is used. 

## Program Specifications:
1. My program was written in Java using Eclipse IDE
2. There are four classes: Symbol, LinkerV2, Module and SymbolTableObj
3. LinkerV2 contains the main logic, please run the program from there.
4. Compile LinkerV2 on the server along with other classes
5. LinkerV2 takes command line input only.
6. The input only requires the name of the file that has all the modules.
7. Example command line input argument 'C:\\Users\\inputfile9.txt'
