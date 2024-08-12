package afekafy;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {
	
	protected String firstName;
	protected String lastName;
	protected String email;
	protected int yearOfBirth;
	protected String password;
	Scanner s = new Scanner(System.in);
	
    protected static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    protected static final Pattern pattern = Pattern.compile(EMAIL_REGEX);
    
    public User(String firstName, String lastName, String email, int yearOfBirth, String password) {
    	this.firstName = firstName;
        this.lastName = lastName;
        this.yearOfBirth = yearOfBirth;
        this.password = password;
    	setEmail(email);
    }
    
    public void setEmail(String email) {
        while (!isValidEmail(email)) {
            System.out.println("Invalid email format. Please enter a valid email: ");
            email = s.nextLine();
        }
        this.email = email;
    }
    
    public boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
}
