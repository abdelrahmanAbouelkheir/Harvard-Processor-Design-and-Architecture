import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;
public class CPU {
	boolean jump=false;
	ArrayList<String> instructionMemory = new ArrayList<String>(1024);
	Byte dataMemory[] = new Byte[2048];
	Byte[] registers = new Byte[64];
	int[] statusReg = new int[5];
	int programCounter = 0;
	ArrayList<String> fetchToDecodePiplineRegister =null;
	ArrayList<Object> decodeToExecutePiplineRegister =null;
	ArrayList<Object> ExecuteToMemoryPipelineRegister;
	ArrayList<Object> MemoryToWritePipelineRegister;
	
	

	public CPU() {
		
		
		
		parser("ashrabShay.txt");
		start();
	}
	
	private  void parser(String filePath) {
		ArrayList<String[]> lines = new ArrayList<>();

		BufferedReader reader = null;
		String line = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			
			while ((line = reader.readLine()) != null) {

				//parsing(line.split(" "));
				
				instructionMemory.add(Parser.parse(line.split(" ")));
				
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstructionNotFoundException e) {
			
			e.printStackTrace();
		} catch (RegisterNotFoundException e) {
			
			e.printStackTrace();
		} catch (ImmediateValueException e) {
			
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		
	}

	public void start() {
	    decodeToExecutePiplineRegister = null;
	    fetchToDecodePiplineRegister = null;
	    int clockCycles = 0;
	    while (programCounter < instructionMemory.size() + 2) {
	        System.out.println("/------------------------------------------");
	        System.out.println("Current Clock Cycle: " + clockCycles++);
	        System.out.println("/------------------------------------------");
	        if (jump) {
	            decodeToExecutePiplineRegister = null;
	            fetchToDecodePiplineRegister = null;
	            jump = false;
	        }

	        if (decodeToExecutePiplineRegister != null) {
	            execute(decodeToExecutePiplineRegister);
	        }
	        if (fetchToDecodePiplineRegister != null) {
	            decodeToExecutePiplineRegister = decode(fetchToDecodePiplineRegister);
	        }
	        fetchToDecodePiplineRegister = fetch();
	        System.out.println("/------------------------------------------");
	       
	        if (!jump) {
	            programCounter++;
	        }
	        
	       
	    }
	    System.out.println("Register content (non-null values):");
        for (int i = 0; i < registers.length; i++) {
            if (registers[i] != null) {
                System.out.println("Content of R" + i + ": " + registers[i]);
            }
        }
        System.out.println("/------------------------------------------");
        System.out.println("Instruction Memory content (non-null values):");
        for (int i = 0; i < instructionMemory.size(); i++) {
            if (instructionMemory.get(i) != null) {
                System.out.println("Content at " + i + ": " + instructionMemory.get(i));
            }
        }
        System.out.println("/------------------------------------------");
        System.out.println("Data Memory content (non-null values):");
        for (int i = 0; i < dataMemory.length; i++) {
            if (dataMemory[i] != null) {
                System.out.println("Content at " + i + ": " + dataMemory[i]);
            }
        }
	}


	public ArrayList<String> fetch() {
	    ArrayList<String> out = new ArrayList<String>();
	    if (programCounter < instructionMemory.size()) {
	        String instruction = instructionMemory.get(programCounter);
	        System.out.println("/------------------------------------------");
	        System.out.println("Fetch input : pc "+ programCounter+" ---> output instruction: " + instruction);
	        System.out.println("/------------------------------------------");
	        out.add(instruction);
	        out.add(String.valueOf(programCounter));
	        return out;
	    } else {
	        return null;
	    }
	}

	public ArrayList<Object> decode(ArrayList<String> instruction) {
	    System.out.println("/------------------------------------------");
	    System.out.println("Instruction " + instruction.get(0) + " is being decoded");
	    System.out.println("/------------------------------------------");

	    ArrayList<Object> output = new ArrayList<Object>();
	    Byte opcode;
	    Byte r1;
	    Byte r2;
	    Byte imm;
	    short pc;

	    opcode = Byte.parseByte(instruction.get(0).substring(0, 4), 2);
	    r1 = RTypeSixBitsTo8(instruction.get(0).substring(4, 10));
	    r2 = RTypeSixBitsTo8(instruction.get(0).substring(10, 16));
	    imm = ITypeSixBitsTo8(instruction.get(0).substring(10, 16));
	    pc = Short.parseShort(instruction.get(1));

	    output.add(opcode); // pos 0
	    output.add(r1); // pos 1
	    output.add(r2); // pos 2
	    output.add(imm); // pos 3
	    output.add(pc); // pos 4

	    System.out.println("/------------------------------------------");
	    System.out.println("Decoded Instruction:");
	    System.out.println("Opcode: " + opcode);
	    System.out.println("R1: " + r1);
	    System.out.println("R2: " + r2);
	    System.out.println("Immediate: " + imm);
	    System.out.println("Program Counter: " + pc);
	    System.out.println("/------------------------------------------");

	    return output;
	}

	
	public void execute(ArrayList<Object> instruction) {
	    ExecuteToMemoryPipelineRegister = new ArrayList<Object>();
	    MemoryToWritePipelineRegister = new ArrayList<Object>();

	    System.out.println("Executing Input: " + instruction);

	    // Get opcode
	    Byte opcode = (byte) instruction.get(0);

	    // Get two operands of the instruction, it will work for either R or I type instruction
	    Byte r1 = (byte) instruction.get(1);
	    Byte r2 = (byte) instruction.get(2);
	    Byte imm = (byte) instruction.get(3);
	    short pc = (short) instruction.get(4);

	    System.out.println("/------------------------------------------");

	    // Getting operands of the instruction ready to be printed
	    String rOperands = "R" + r1 + " R" + r2;
	    String iOperands = "R" + r1 + " " + imm;

	    byte num1;
	    byte num2;
	    byte ALUOutput;
	    byte writeBackR;
	    byte writeBackValue;
	    byte memPos;
	    
	    switch (opcode) {
	        case 0:
	            System.out.println("Executing: Add " + rOperands);
	            // Read Register
	            num1 = dataRegFetcher(r1);
	            num2 = dataRegFetcher(r2);
	            // Use ALU
	            ALUOutput = add(num1, num2);

	            // Load EX/MEM
	            ExecuteToMemoryPipelineRegister.add(r1);
	            ExecuteToMemoryPipelineRegister.add(ALUOutput);

	            // Memory part
	            // Load extract from pipeline
	            writeBackR = (byte) ExecuteToMemoryPipelineRegister.get(0);
	            writeBackValue = (byte) ExecuteToMemoryPipelineRegister.get(1);
	            // Load MEM/WB
	            MemoryToWritePipelineRegister.add(writeBackR);
	            MemoryToWritePipelineRegister.add(writeBackValue);

	            // Write Register
	            dataRegWriter(MemoryToWritePipelineRegister);
	            break;

	        case 1:
	            System.out.println("Executing: SUB " + rOperands);
	            // Read Register
	            num1 = dataRegFetcher(r1);
	            num2 = dataRegFetcher(r2);
	            // Using ALU
	            ALUOutput = sub(num1, num2);

	            // Load EX/MEM
	            ExecuteToMemoryPipelineRegister.add(r1);
	            ExecuteToMemoryPipelineRegister.add(ALUOutput);

	            // Memory part
	            // Load extract from pipeline
	            writeBackR = (byte) ExecuteToMemoryPipelineRegister.get(0);
	            writeBackValue = (byte) ExecuteToMemoryPipelineRegister.get(1);
	            // Load MEM/WB
	            MemoryToWritePipelineRegister.add(writeBackR);
	            MemoryToWritePipelineRegister.add(writeBackValue);

	            // Write Register
	            dataRegWriter(MemoryToWritePipelineRegister);
	            break;

	        // Other cases for different opcodes...
	        case 2:
	            System.out.println("Executing: MUL " + rOperands);
	            // Read Register
	            num1 = dataRegFetcher(r1);
	            num2 = dataRegFetcher(r2);

	            // Using ALU
	            ALUOutput = mul(num1, num2);
	            ExecuteToMemoryPipelineRegister.add(r1);
	            ExecuteToMemoryPipelineRegister.add(ALUOutput);

	            // Memory part
	            // Load extract from pipeline
	            writeBackR = (byte) ExecuteToMemoryPipelineRegister.get(0);
	            writeBackValue = (byte) ExecuteToMemoryPipelineRegister.get(1);
	            // Load MEM/WB
	            MemoryToWritePipelineRegister.add(writeBackR);
	            MemoryToWritePipelineRegister.add(writeBackValue);

	            // Write Register
	            dataRegWriter(MemoryToWritePipelineRegister);
	            break;

	        case 3:
	            System.out.println("Executing: MOVEI " + iOperands);
	            // Use ALU
	            ALUOutput = moveImm(imm);
	            ExecuteToMemoryPipelineRegister.add(r1);
	            ExecuteToMemoryPipelineRegister.add(ALUOutput);

	            // Memory part
	            // Load extract from pipeline
	            writeBackR = (byte) ExecuteToMemoryPipelineRegister.get(0);
	            writeBackValue = (byte) ExecuteToMemoryPipelineRegister.get(1);
	            // Load MEM/WB
	            MemoryToWritePipelineRegister.add(writeBackR);
	            MemoryToWritePipelineRegister.add(writeBackValue);

	            // Write Register
	            dataRegWriter(MemoryToWritePipelineRegister);
	            break;

	        // Continue with the rest of the cases...
	        case 4:
	            System.out.println("Executing: BEQZ " + iOperands);
	            // Read Register
	            num1 = dataRegFetcher(r1);
	            // Use ALU
	            branchIfEq(num1, imm, pc);
	            break;

	        case 5:
	            System.out.println("Executing: ANDI " + iOperands);
	            // Read Register
	            num1 = dataRegFetcher(r1);
	            // Use ALU
	            ALUOutput = andi(num1, imm);
	            ExecuteToMemoryPipelineRegister.add(r1);
	            ExecuteToMemoryPipelineRegister.add(ALUOutput);

	            // Memory part
	            // Extract from pipeline
	            writeBackR = (byte) ExecuteToMemoryPipelineRegister.get(0);
	            writeBackValue = (byte) ExecuteToMemoryPipelineRegister.get(1);
	            // Load MEM/WB
	            MemoryToWritePipelineRegister.add(writeBackR);
	            MemoryToWritePipelineRegister.add(writeBackValue);

	            // Write Register
	            dataRegWriter(MemoryToWritePipelineRegister);
	            break;

	        case 6:
	            System.out.println("Executing: EOR " + rOperands);
	            // Read Registers
	            num1 = dataRegFetcher(r1);
	            num2 = dataRegFetcher(r2);
	            // Use ALU
	            ALUOutput = eor(num1, num2);
	            ExecuteToMemoryPipelineRegister.add(r1);
	            ExecuteToMemoryPipelineRegister.add(ALUOutput);

	            // Memory part
	            // Extract from pipeline
	            writeBackR = (byte) ExecuteToMemoryPipelineRegister.get(0);
	            writeBackValue = (byte) ExecuteToMemoryPipelineRegister.get(1);
	            // Load MEM/WB
	            MemoryToWritePipelineRegister.add(writeBackR);
	            MemoryToWritePipelineRegister.add(writeBackValue);

	            // Write Register
	            dataRegWriter(MemoryToWritePipelineRegister);
	            break;

	        case 7:
	            System.out.println("Executing: BR " + rOperands);
	            // Read Registers
	            num1 = dataRegFetcher(r1);
	            num2 = dataRegFetcher(r2);

	            // Use ALU
	            branchReg(num1, num2);
	            break;

	        case 8:
	            System.out.println("Executing: SAL " + iOperands);
	            // Read Register
	            num1 = dataRegFetcher(r1);
	            // Use ALU
	            ALUOutput = ShiftArithmeticLeft(num1, imm);
	            ExecuteToMemoryPipelineRegister.add(r1);
	            ExecuteToMemoryPipelineRegister.add(ALUOutput);

	            // Memory part
	            // Extract from pipeline
	            writeBackR = (byte) ExecuteToMemoryPipelineRegister.get(0);
	            writeBackValue = (byte) ExecuteToMemoryPipelineRegister.get(1);
	            // Load MEM/WB
	            MemoryToWritePipelineRegister.add(writeBackR);
	            MemoryToWritePipelineRegister.add(writeBackValue);

	            // Write Register
	            dataRegWriter(MemoryToWritePipelineRegister);
	            break;

	        case 9:
	            System.out.println("Executing: SAR " + iOperands);
	            // Read Register
	            num1 = dataRegFetcher(r1);
	            // Use ALU
	            ALUOutput = ShiftArithmeticRight(num1, imm);
	            ExecuteToMemoryPipelineRegister.add(r1);
	            ExecuteToMemoryPipelineRegister.add(ALUOutput);
	            
	            // Memory part
	            // Extract from pipeline
	            writeBackR = (byte) ExecuteToMemoryPipelineRegister.get(0);
	            writeBackValue = (byte) ExecuteToMemoryPipelineRegister.get(1);
	        
	            // Load MEM/WB
	            MemoryToWritePipelineRegister.add(writeBackR);
	            MemoryToWritePipelineRegister.add(writeBackValue);

	         // Write Register
	         dataRegWriter(MemoryToWritePipelineRegister);
	         break;
	        case 10:

	            System.out.println("Executing: LDR " + iOperands);

	            // ALU
	            ExecuteToMemoryPipelineRegister.add(r1);
	            ExecuteToMemoryPipelineRegister.add(imm);

	            // Memory part
	            // Extract from pipeline
	            writeBackR = (byte) ExecuteToMemoryPipelineRegister.get(0);
	            memPos = (byte) ExecuteToMemoryPipelineRegister.get(1);

	            writeBackValue = dataMemoryFetcher(memPos);

	            // Load MEM/WB
	            MemoryToWritePipelineRegister.add(writeBackR);
	            MemoryToWritePipelineRegister.add(writeBackValue);

	            // Write Register
	            dataRegWriter(MemoryToWritePipelineRegister);

	            break;
	       
	         case 11:
	             System.out.println("Executing: STR " + iOperands);
	             // Read Register
	             num1 = dataRegFetcher(r1);

	             // ALU
	             ExecuteToMemoryPipelineRegister.add(imm);
	             ExecuteToMemoryPipelineRegister.add(num1);

	             // Memory part
	             // Extract from pipeline
	             memPos = (byte) ExecuteToMemoryPipelineRegister.get(0);
	             byte value = (byte) ExecuteToMemoryPipelineRegister.get(1);

	             dataMemoryWriter(memPos, value);

	             // Load MEM/WB
	             MemoryToWritePipelineRegister.add(memPos);
	             MemoryToWritePipelineRegister.add(value);

	             // Register Write

	             break;
	         }

	    System.out.println("/------------------------------------------");
	    System.out.println("/------------------------------------------");
	    System.out.println("Status Register Value is:\n" + PrintStat());
	    System.out.println("/------------------------------------------");
	}

	//ALU Operations
	public byte add(byte num1, byte num2) {

		
		byte temp =  (byte) (num1 + num2);
		// Check Carry
		setCarryFlag(num1, num2);
		// Check Overflow
		setTwoComplementOverflowFlag(num1, num2);
		// Check negative
		setNegativeFlag(temp);
		// Check sign
		setSignFlag();
		// Check Zero
		setZeroFlag(temp);

	//	registers[r1] = (byte) temp1;
		return temp;

		// adds r1 to r2 and then store in r1 location
	}
//public ArrayList<Object> ALU(byte opcode,byte op1,byte op2 ){
//	switch(opcode) {
//	
//		case 0:
//			break;
//		case 1:
//		case 2:
//		case 3:
//		case 4:
//		case 5:
//		case 6:
//		case 7:
//		case 8:
//		case 9:
//		case 10:
//		
//		
//	
//	
//	
//	
//	}
//}
	public byte sub( byte num1, byte num2) {

		
		byte temp =  (byte) (num1 - num2);
		// Check Overflow
		setTwoComplementOverflowFlag(num1, num2);
		// Check negative
		setNegativeFlag(temp);
		// Check sign
		setSignFlag();
		// Check Zero
		setZeroFlag(temp);

	//	registers[r1] = (byte) temp1;
	    return temp; 
	}

	public byte mul(Byte num1 ,Byte num2) {
	
		
		byte temp =  (byte) (num1 * num2);
		// Check Negative Sign 
		setNegativeFlag(temp);
		// Check Zero
		setZeroFlag(temp);

		return temp;
	}
	public byte moveImm(byte num2) {
		
		//check negative
		setNegativeFlag(num2);
		// Check Zero
		setZeroFlag(num2);
		//Write in Register
		
		return num2;
        

	}
	public byte eor(byte op1, byte op2) {
		
		
	    byte temp = (byte)(op1 ^ op2);
		//check negative
		setNegativeFlag(temp);
		// Check Zero
		setZeroFlag(temp);

		return temp;
	}
	public byte andi(byte num1, byte imm) {
		
		
		
		byte temp =  (byte) (num1 & imm);
		// Check Negative Sign 
		setNegativeFlag(temp);
		// Check Zero
		setZeroFlag(temp);

		return temp;
	}
	public void branchIfEq(byte value, byte imm,short pc) {
		
		if (value == 0) {
		
			programCounter = pc + 1 + imm  ;
			
			jump = true;
		}
	}
	public void branchReg(byte value1, byte value2) {
	
	    short pc = (short) ((value1 << 8) | value2);
	    programCounter=(pc);
	    jump=true;
	    
	}
	public byte ShiftArithmeticLeft(byte num1, byte num2) {
		
		
		byte temp =  (byte) (num1 << num2);
		// Check Negative Sign 
		setNegativeFlag(temp);
		// Check Zero
		setZeroFlag(temp);

		return temp;
	}
	public byte ShiftArithmeticRight(byte num1, byte num2) {
		
		
		byte temp =  (byte) (num1 >> num2);
		// Check Negative Sign 
		setNegativeFlag(temp);
		// Check Zero
		setZeroFlag(temp);

		return temp;
	}
	
///Memory
//	public void loadToReg(byte r1, byte address) {
//		byte value = dataMemoryFetcher(address);
//		
//		dataRegWriter(r1, value);
//
//	}
//
//	public void storeFromReg(byte r1, byte address) {
//		
//		byte value = dataRegFetcher(r1);
//
//		dataMemoryWriter(address, value);
//
//	}
	
	/*
	public void loadToReg(short r1, Byte imm) {
	    byte data = memoryFetcher(address);

	    dataRegWriter(r1, data);
	}

*/


	// ----------------------------------------------------------------------------------------------------------------------
	// fetchers and writers.... why? someone would ask, well it's for pipelining.
	// just use them in the program
		public byte dataRegFetcher(byte rPos) {
			return registers[rPos];

		}

		public void dataRegWriter(ArrayList<Object> pipeline) {
		    byte rPos = (byte) pipeline.get(0);
		    byte value = (byte) pipeline.get(1);
		    registers[rPos] = value;
		    System.out.println("Register r" + rPos + " was changed to " + value);
		}

		public byte dataMemoryFetcher(byte mPos) {
		    return dataMemory[mPos].byteValue();
		}

		public void dataMemoryWriter(byte mPos, byte value) {
		    dataMemory[mPos] = value;
		    System.out.println("Data memory was changed at " + mPos + " to " + value);
		}

//----------------------------------------------------------------------------------------------------------------------
	// flags in SREG


	public void setZeroFlag(byte value) {

		if(value==0)
			statusReg[0] = 1;
		else
			statusReg[0] = 0;
		
	}


	public void setSignFlag() {

		statusReg[1] = statusReg[2] ^ statusReg[3];
			
	}
	
	public int negativeFlagFetch() {
		
		return statusReg[2];
	}


	public void setNegativeFlag(byte value) {

		statusReg[2]  = value < 0 ? 1 : 0;
		
			
	}


	public int getTwoComplementOverflowFlagFetch() {
		
		return statusReg[3];
		
	}

	public void setTwoComplementOverflowFlag(byte num1 , byte num2) {
		
		//get bits 8 and 7 from num1 and num2
		boolean isSet7 = ((num1 >> 6) & 1) == 1;
		boolean aisSet7 = ((num2 >> 6) & 1) == 1;
		boolean isSet8 = ((num1 >> 7) & 1) == 1;
		boolean aisSet8 = ((num2 >> 7) & 1) == 1;
		
		//get carry 7 and 8
		boolean carry7 = isSet7&&aisSet7;
		boolean carry8 = ((carry7&&isSet8) || (carry7&&aisSet8) || (aisSet8&&isSet8));

    
		if ( carry7 ^ carry8 )
			statusReg[3] = 1;
		else 
			statusReg[3] = 0;	
 
		
	}
	
	public void setCarryFlag(byte a, byte b) {
		int temp1 = a & 0x000000FF;
		int temp2 = b & 0x000000FF;

		if( ((temp1 + temp2) & 0x00000100) == 0x00000100) {
			statusReg[4] = 1;
		} else {
			statusReg[4] = 0;
		}
			
			
	}
	public byte ITypeSixBitsTo8(String a) {
		if(a.charAt(0)=='0') {
			return (byte)Integer.parseInt(String.format("%32s", a).replace(' ', '0'),2);
		}else {
			return  (byte) (int) Long.parseLong(String.format("%32s", a).replace(' ', '1'),2);
		}
		
	}
	public byte RTypeSixBitsTo8(String a) {
		
		return (byte)Integer.parseInt(String.format("%32s", a).replace(' ', '0'),2);
		
	}
//	public int twoComplementOverflowFlagFetch() {
//	
//	return statusReg[3];
//	
//}
//	public void twoComplementOverflowFlag(short value) {
//
//		statusReg[3] = value;
//			
//	}

//	public void setCarryFlag(short value) {
//		
//		statusReg[4] = value;
//			
//	}
//	public int negativeFlagFetch() {
//	
//	return statusReg[2];
//}

//public void setNegativeFlag(short value) {
//
//	statusReg[2] = value;
//
//}
	
//	public void setSignFlag(short value) {
//
//		statusReg[1] = value;
//		
//			
//	}
//	public void setZeroFlag(short value) {
//
//		statusReg[0] = value;
//
//	}
//	public int loadToReg(short register, int address) {
//  int value = 0;
// 
//  value |= (dataMemory[address]).byteValue();
//  
//  registers[register] = (byte) value;
//  return value;
//}
	
public String PrintStat() {
	
	String s="";
	s+="C V N S Z \n";
	s+=statusReg[4]+" "+statusReg[3]+" "+statusReg[2]+" "+statusReg[1]+" "+statusReg[0];
	
	return s;
}

}
