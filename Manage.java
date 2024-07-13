package afekafy;
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Manage {
	
    // Database connection parameters
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/your_database_name";
    static final String USER = "your_username";
    static final String PASS = "your_password";
    Connection conn = null;
    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
	
    // Method to connect to the database
    public void connect(){
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }
    
    public void closeDBresources() {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void printUsers(int userID) {
    	// Assuming admin is a userID 1
        if (userID != 1) {
            System.out.println("Access denied. Only admin users can print all users.");
            return;
        }
        
        try {
            Class.forName(JDBC_DRIVER);
            // Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            // Execute a query
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user");

            // Print user details
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String name = firstName + " " + lastName;
                String email = rs.getString("email");
                int yearOfBirth = rs.getInt("year_of_birth");
                int age = calculateAge(yearOfBirth);
                String password = rs.getString("password");

                System.out.println("User ID: " + userId);
                System.out.println("Name: " + name);
                System.out.println("Email: " + email);
                System.out.println("Age: " + age);
                System.out.println("Password: " + password);
                System.out.println("--------------------------");
            }
            
            // Close resources
            //rs.close();
            //stmt.close();
            //conn.close();
        } 	catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
        
    }
    
    public int calculateAge(int yearOfBirth) {
        return LocalDate.now().getYear() - yearOfBirth;
    }
    
    public void userRegistration (Scanner s) {
    	
    	String firstName, lastName, email, password;
    	int yearOfBirth;
        // Get user details
        System.out.print("Enter first name: ");
        firstName = s.nextLine();

        System.out.print("Enter last name: ");
        lastName = s.nextLine();

        System.out.print("Enter email: ");
        email = s.nextLine();

        System.out.print("Enter year of birth: ");
        yearOfBirth = s.nextInt();
        s.nextLine();

        System.out.print("Enter password: ");
        password = s.nextLine();

        // Create a new User instance
        User newUser = new User(firstName, lastName, email, yearOfBirth, password);

        // Insert the new user into the database
        insertUserIntoDatabase(newUser);
    }
    
    public void insertUserIntoDatabase(User user) {

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create SQL insert statement
            String sql = "INSERT INTO user (first_name, last_name, email, birth_year, password, registration_date) " +
                         "VALUES (?, ?, ?, ?, ?, NOW())";

            // Set the parameters
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.firstName);
            pstmt.setString(2, user.lastName);
            pstmt.setString(3, user.email);
            pstmt.setInt(4, user.yearOfBirth);
            pstmt.setString(5, user.password);

            // Execute the insert
            pstmt.executeUpdate();

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

    }
    
    public int userLogin(Scanner s) {
    	String email, password;
    	
        System.out.print("Enter your email: ");
        email = s.nextLine();

        System.out.print("Enter your password: ");
        password = s.nextLine();
        
        int userID = authenticateUser(email, password);

        if (userID != -1) {
            System.out.println("User authenticated successfully.");
        } else {
            System.out.println("Invalid email or password. Try again");
        }
        return userID;
    }
    
    public int authenticateUser(String email, String password) {
    	int userID = -1;
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Create SQL select statement and set the parameters
            pstmt = conn.prepareStatement("SELECT password FROM user WHERE email = ?");
            pstmt.setString(1, email);

            // Execute the query
            rs = pstmt.executeQuery();

            // Check if a user was found and if the password matches
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)) {
                    userID = rs.getInt("user_id");
                }
            }

        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
        return userID; // Return false if user not found or any exception occurs
    }
    
    public void deleteUserAccount(int userID) {
    	try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Set the parameters
            pstmt = conn.prepareStatement("DELETE FROM user WHERE user_id = ?");
            pstmt.setInt(1, userID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("Failed to delete user. User ID may not exist.");
            }
        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

    }

}
