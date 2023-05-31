
# Project Description
 
## Project Overview

“ Processor design is the design engineering task of creating a processor, a key component of computer
hardware. The design process involves choosing an instruction set and a certain execution paradigm,
and results in a microarchitecture. The mode of operation of any processor is the execution of lists of
instructions. Instructions typically include those to compute or manipulate data values using registers,
change or retrieve values in read/write memory, perform relational tests between data values and to
control program ﬂow. ”

` `In this project, I simulated a ﬁctional processor design and architecture using Java. You are asked
to choose one of four processor packages described in the upcoming sections.

Harvard

.1 Memory Architecture

a) Architecture: Harvard

• Harvard Architecture is the digital computer architecture whose design is based on the concept
 where there are separate storage and separate buses (signal path) for instruction and
 data. It was basically developed to overcome the bottleneck of Von Neumann Architecture.

b) Instruction Memory Size: 1024 \* 16

Instruction Memory
 16 Bits / Row

1024 Rows

• The instruction memory addresses are from 0 to 210 − 1 (0 to 1023).
 • Each memory block (row) contains 1 word which is 16 bits (2 bytes).

10




<a name="br11"></a>• The instruction memory is word addressable.

• The program instructions are stored in the instruction memory.

c) Data Memory Size: 2048 \* 8

Data Memory
 8 Bits / Row

2048 Rows

• The data memory addresses are from 0 to 211 − 1 (0 to 2047).
 • Each memory block (row) contains 1 word which is 8 bits (1 byte).

• The data memory is word/byte addressable (1 word = 1 byte).

• The data is stored in the data memory.

d) Registers: 66

• Size: 8 bits

• 64 General-Purpose Registers (GPRS)

– Names: R0 to R63

• 1 Status Register

7 6 5 4 3 2 1 0 0 0 0 C V N S Z

– Name: SREG

– A status register, ﬂag register, or condition code register (CCR) is a collection of status

ﬂag bits for a processor.

– The status register has 5 ﬂags updated after the execution of speciﬁc instructions:

∗ Carry Flag (C): Indicates when an arithmetic carry or borrow has been generated out

of the most signiﬁcant bit position.

· Check on 9th bit (bit 8) of UNSIGNED[VALUE1] OP UNSIGNED[VALUE2] ==

1 or not.

· Example: <https://piazza.com/class/le8zgqxowmd6e7/post/17>

∗ Two’s Complement Overﬂow Flag (V): Indicates when the result of a signed number

operation is too large, causing the high-order bit to overﬂow into the sign bit.

· If 2 numbers are added, and they both have the same sign (both positive or both
 negative), then overﬂow occurs (V = 1) if and only if the result has the opposite
 sign. Overﬂow never occurs when adding operands with diﬀerent signs.

· If 2 numbers are subtracted, and their signs are diﬀerent, then overﬂow occurs (V
 = 1) if and only if the result has the same sign as the subtrahend.

· The diﬀerence between carry and overﬂow is explained in: [https://piazza.com/](https://piazza.com/class/le8zgqxowmd6e7/post/18)

[class/le8zgqxowmd6e7/post/18](https://piazza.com/class/le8zgqxowmd6e7/post/18)

∗ Negative Flag (N): Indicates a negative result in an arithmetic or logic operation.
 · N = 1 if result is negative.

· N = 0 if result is positive or zero.

∗ Sign Flag (S): Indicates the expected sign of the result (not the actual sign).

` `· S = N ⊕ V (XORing the negative and overﬂow ﬂags will calculate the sign ﬂag).
∗ Zero Flag (Z): Indicates that the result of an arithmetic or logical operation was zero.

· Z = 1 if result is 0.

11




<a name="br12"></a>· Z = 0 if result is not 0.

∗ Since all registers are 8 bits, and we are only using 5 bits in the Status Register for

the ﬂags, you are required to keep Bits7:5 cleared “0” at all times in the register. • 1 Program Counter

– Name: PC

– Type: Special-purpose register with a size of 16 bits (not 8 bits).
– A program counter is a register in a computer processor that contains the address (loca-
 tion) of the instruction being executed at the current time.

– As each instruction gets fetched, the program counter is incremented to point to the next

instruction to be executed.

4\.2 Instruction Set Architecture

a) Instruction Size: 16 bits

b) Instruction Types: 2

R-Format OPCODE R1 R2

4 6 6

I-Format

OPCODE R1 IMMEDIATE

4 6 6

c) Instruction Count: 12

• The opcodes are from 0 to 11 according to the instructions order in the following table:

` `Name Mnemonic Type Format Operation
Add ADD R ADD R1 R2 R1 = R1 + R2
Subtract SUB R SUB R1 R2 R1 = R1 - R2
Multiply MUL R MUL R1 R2 R1 = R1 \* R2
Move Immediate MOVI I MOVI R1 IMM R1 = IMM

Branch if Equal Zero BEQZ I BEQZ R1 IMM IF(R1 == 0) {
 PC = PC+1+IMM }

And Immediate ANDI I ANDI R1 IMM R1 = R1 & IMM

Exclusive Or EOR R EOR R1 R2 R1 = R1 ⊕ R2

Branch Register BR R BR R1 R2 PC = R1 || R2

` `Shift Arithmetic Left SAL I SAL R1 IMM R1 = R1 << IMM
Shift Arithmetic Right SAR I SAR R1 IMM R1 = R1 >> IMM
Load to Register LDR I LDR R1 ADDRESS R1 = MEM[ADDRESS]
 Store from Register STR I STR R1 ADDRESS MEM[ADDRESS] = R1

“||” symbol indicates concatenation (0100 || 1100 = 01001100).

d) The Status Register (SREG) ﬂags are aﬀected by the following instructions:

• The Carry ﬂag (C) is updated every ADD instruction.

• The Overﬂow ﬂag (V) is updated every ADD and SUB instruction.

• The Negative ﬂag (N) is updated every ADD, SUB, MUL, ANDI, EOR, SAL, and SAR

instruction.

• The Sign ﬂag (S) is updated every ADD and SUB instruction.

• The Zero ﬂag (Z) is updated every ADD, SUB, MUL, ANDI, EOR, SAL, and SAR instruction.
• A ﬂag value can only be updated by the instructions related to it.

12




<a name="br13"></a>4.3 Datapath

a) Stages: 3

• All instructions regardless of their type must pass through all 3 stages.

• Instruction Fetch (IF): Fetches the next instruction from the main memory using the

address in the PC (Program Counter), and increments the PC.

• Instruction Decode (ID): Decodes the instruction and reads any operands required from

the register ﬁle.

• Execute (EX): Executes the instruction. In fact, all ALU operations are done in this stage.
 Moreover, it performs any memory access required by the current instruction. For loads, it
 would load an operand from the main memory, while for stores, it would store an operand into
 the main memory. Finally, for instructions that have a result (a destination register), it writes
 this result back to the register ﬁle.

b) Pipeline: 3 instructions (maximum) running in parallel

 • Number of clock cycles: 3 + ((n − 1) ∗ 1), where n = number of instructions
 – Imagine a program with 7 instructions:

 ∗ 3 + (6 ∗ 1) = 9 clock cycle

 ∗ Instruction Fetch Instruction Decode Execute
 (IF) (ID) (EX)

Cycle 1 Instruction 1

Cycle 2 Instruction 2 Instruction 1

Cycle 3 Instruction 3 Instruction 2 Instruction 1
Cycle 4 Instruction 4 Instruction 3 Instruction 2
Cycle 5 Instruction 5 Instruction 4 Instruction 3
Cycle 6 Instruction 6 Instruction 5 Instruction 4
Cycle 7 Instruction 7 Instruction 6 Instruction 5
Cycle 8 Instruction 7 Instruction 6
Cycle 9 Instruction 7
