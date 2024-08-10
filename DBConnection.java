package afekafy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class DBConnection {

	// Database connection parameters
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/afekafy";
    static final String USER = "postgresuser";
    static final String PASS = "postgres";
    Connection conn = null;
    //Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    // Method to connect to the database
    public void connect(){
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            //stmt = conn.createStatement();
        } catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }
    
    // Method to close the database resources
    public void closeDBresources() {
        try {
            if (rs != null) rs.close();
            //if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Method to execute a SQL query and return the results
    public ResultSet executeQuery(String sqlQuery) {
        if (conn == null) {
            System.out.println("Connection is not properly initialized.");
            return null;
        }
        Statement stmt = null;
        try {
            stmt = conn.createStatement(); // Create a new Statement for each query
            return stmt.executeQuery(sqlQuery);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return null;
        }
    }
    
 // Method to execute a SQL query and return the results in a Map
    public Map<String, Object> executeMultipleQueries(String sqlQuery) {
    	Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            if (conn == null || conn.isClosed()) {
                System.out.println("Connection is null.");
                return resultMap;
            }
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlQuery);
            
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    resultMap.put(columnName, columnValue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultMap;
    }
    
    // Method to execute an INSERT, UPDATE or DELETE statements
    public int executeUpdateQuery(String sqlQuery) {
        if (conn == null) {
            System.out.println("Connection is not properly initialized.");
            return -1;
        }
        Statement stmt = null;
        try {
            stmt = conn.createStatement(); // Create a new Statement for each query
            return stmt.executeUpdate(sqlQuery); // Executes the SQL statement
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return -1;
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
