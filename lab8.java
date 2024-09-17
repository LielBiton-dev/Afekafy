package afekafy;

//import java.util.Collections;
//import java.util.Enumeration;
//import java.util.HashSet;
//import java.util.Set;
import java.sql.*;

public class lab8 {
	
	public static void main(String args[]) {
		
		Connection conn = null;

		try {
			Class.forName("org.postgresql.Driver");
			String dbUrl = "jdbc:postgresql://localhost:5432/myFirstDataBase";
			conn = DriverManager.getConnection(dbUrl, "postgresuser", "postgres");
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM studenttable");
			while (rs.next()) {
			System.out.println(rs.getInt("studentid") + "- " + rs.getString("studentfirstname"));
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (SQLException ex) {
		ex.printStackTrace();
		}
	}
}
