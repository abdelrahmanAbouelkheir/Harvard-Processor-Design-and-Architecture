
@SuppressWarnings("serial")
public class RegisterNotFoundException extends Exception { 
    public RegisterNotFoundException(String errorMessage) {
        super("Register Not Found "+errorMessage);
    }
}
