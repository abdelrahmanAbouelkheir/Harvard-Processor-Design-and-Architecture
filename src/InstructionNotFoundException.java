@SuppressWarnings("serial")

public class InstructionNotFoundException extends Exception { 
    public InstructionNotFoundException(String errorMessage) {
        super("Instruction "+errorMessage+" Not Found.");
    }
}
