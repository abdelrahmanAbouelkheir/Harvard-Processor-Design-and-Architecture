import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

	
	public static String parse(String[] parts) throws InstructionNotFoundException ,RegisterNotFoundException,ImmediateValueException {
		
		String opcode="";
		boolean r =true;
		//add opcode and return to add to instructions
		switch (parts[0]) {

		case "ADD":

			opcode= "0000";
			break;
			
		case "SUB":
			
			opcode= "0001";
			break;
			
		case "MUL":
			
			opcode = "0010";
			break;
			
		case "MOVI":
			
			opcode = "0011";
			r = false;
			break;
		case "BEQZ":
			
			opcode = "0100";
			r = false;
			break;
			
		case "ANDI":
			
			opcode =  "0101";
			r = false;
			break;
		case "EOR":
			
			opcode = "0110";
			break;
		case "BR":
			
		    opcode = "0111";
		    break;
		    
		case "SAL":
			
			opcode = "1000";
			r = false;
			break;
			
		case "SAR":
			
			opcode = "1001";
			r = false;
			break;
		case "LDR":
			opcode = "1010";
			r = false;
			break;
			
		case "STR":
			opcode = "1011";
			r = false;
			break;
		default: throw new InstructionNotFoundException(parts[0]);
		
		}
		return opcode+parseOprands(parts[1], parts[2] , r);
	}
	
	// parsing instruction operands
	public static String parseOprands(String r1, String r2,boolean r) throws RegisterNotFoundException,ImmediateValueException{
		
		r1=r1.substring(1,r1.length());
		r2=r?r2.substring(1,r2.length()):r2;
		
		String addR1 = To6bitFormat(r1);
		String addR2 = To6bitFormat(r2);
	
		//check if type r 
	
		//convert to integer
		
		int addTheR1 = Integer.parseInt(r1);
		int addTheR2 = Integer.parseInt(r2);
		
		//check if values are valid
		if(addTheR1<0||addTheR1>63) {
			throw new RegisterNotFoundException(addTheR1+""); 
		}
		
		if(r && (addTheR2<0||addTheR2>63)) {
			throw new RegisterNotFoundException(addTheR2+""); 
		}
		else if(!r&&(addTheR2<-32||addTheR2>31)) {
			throw new ImmediateValueException("Immediate "+addTheR2+" must be between -32 and 31 "); 
		}
		//end checking
	
    
		// create instruction
		String addFinalThing = addR1 + addR2;
		return addFinalThing;
	}
	
	
	public static String To6bitFormat(String r2) {
		int i = Integer.parseInt(r2);
		
		if(i<0)
		    return Integer.toBinaryString(i).substring(26,32);
		else
			return pag(i)+Integer.toBinaryString(i);
		
	}


	//add zeros function
	public static String pag(int x) {
		String res=null;
		
		 if (x <= 1) {
			res= "00000";
		} else if (x <= 3) {
			res= "0000";
		} else if (x <= 7) {
			res = "000";
		} else if (x <= 15) {
			res = "00";
		} else if (x <= 31) {
			res = "0";
		}else {
			res="";
		}
	 
		return res;
	}
}