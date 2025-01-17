package database;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet ; 
import java.sql.DriverManager;
import java.util.ArrayList;

public class LocalDB {
	
	
	// Attributes 
	Connection connection ; 
	Statement statement ;
	
	// Constructor 
	public LocalDB() {
		System.out.println("[LocalDB] Calling LocalDB constructor");
		
		
		// Load the driver class file 
		try {
			System.out.println("[LocalDB] Loading the driver class file");
			//Class.forName("com.mysql.cj.jdbc.Driver") ; 
			Class.forName("org.sqlite.JDBC") ; 
		} 
		catch (ClassNotFoundException e) {
			System.out.println("Error while loading the driver class file" + e) ; 
		}
		
		try {
			// Make a database connection
			System.out.println("[LocalDB] Database connection...");
			this.connection = DriverManager.getConnection("jdbc:sqlite:test.db");
			System.out.println("[LocalDB] Database connected");
			
			// Create a statement object
			System.out.println("[LocalDB] Statement objects creation...");
			this.statement = this.connection.createStatement() ; 

			System.out.println("[LocalDB] Statement objects created");

			
			// Execute the statement 			
			String query = "CREATE TABLE IF NOT EXISTS UsernameToIP " +
			           "(username VARCHAR(255) not NULL, " +
			           " ip VARCHAR(255) not NULL)"; 
			
			System.out.println("[LocalDB] Creating the table...");
			this.statement.executeUpdate(query) ;
			System.out.println("[LocalDB] Table created");
		} 
		
		catch (SQLException e) {
			System.out.println(e);
		}
	}
	
	
	// Add a user to the database
	public void addUser(String username, InetAddress IP) {
		System.out.println("[LocalDB] Adding a user in the table...");
		String IPString = IP.toString();
		if (IPString.charAt(0) == ('/')) {
			IPString = IPString.substring(1);
		}
		String query = "INSERT INTO UsernameToIP (Username, IP) VALUES ('" + username + "', '" + IPString + "') ;" ; 
		
		try {
			// Execute the statement 
			this.statement.executeUpdate(query) ; 
			System.out.println("[LocalDB] User added");
		} 
		catch (SQLException e) {
			System.out.println(e);
		}
	}
	
	// Delete a user to the database
	public void deleteUserByName(String username) {
		System.out.println("[LocalDB] Deleting a user using their name from the table...");
		String query = "DELETE FROM UsernameToIP WHERE Username='" + username + "' ;" ;	
		
		try {
			// Execute the statement 
			this.statement.executeUpdate(query) ; 
			System.out.println("[LocalDB] User deleted");
		} 
		catch (SQLException e) {
			System.out.println(e);
		}
	}
	
	public void deleteUserByIP(InetAddress IP) {
		System.out.println("[LocalDB] Deleting a user using their IP from the table...");
		String IPString = IP.toString();
		if (IPString.charAt(0) == ('/')) {
			IPString = IPString.substring(1);
		}
		String query = "DELETE FROM UsernameToIP WHERE IP='" + IPString + "' ;" ;		
		
		try {
			// Execute the statement 
			this.statement.executeUpdate(query) ; 
			System.out.println("[LocalDB] User deleted");
		} 
		catch (SQLException e) {
			System.out.println(e);
		}
	}
	
	// Get information from the database
	public String getUsername(InetAddress IP) {
		System.out.println("[LocalDB] Getting a username by their IP address...");
		String IPString = IP.toString();
		if (IPString.charAt(0) == ('/')) {
			IPString = IPString.substring(1);
		}
		String query = "SELECT UsernameToIP.Username FROM UsernameToIP WHERE IP = '" + IPString + "' ;";	
		String username = "" ; 
		try {
			// Execute the statement 
			ResultSet rs = this.statement.executeQuery(query) ; 
			if (rs.next()) {
				 username = rs.getString(1);
			}
			rs.close(); 
		} 
		catch (SQLException e) {
			System.out.println(e);
		}

		System.out.println("[LocalDB] username fetched");
		return username ;	
	}
	
	public InetAddress getIP(String username) {
		System.out.println("[LocalDB] Getting an IP address by their username...");
		String query = "SELECT UsernameToIP.IP FROM UsernameToIP WHERE Username = '" + username + "' ;";	
		String IPString = "" ; 
		InetAddress IP = null ; 
		try {
			// Execute the statement 
			ResultSet rs = this.statement.executeQuery(query) ; 
			
			if (rs.next()) {
				 IPString = rs.getString(1);
				 IP = InetAddress.getByName(IPString) ; 
			}
			rs.close(); 
		} 
		catch (Exception e) {
			System.out.println(e);
		}
		
		System.out.println("[LocalDB] IP fetched");
		return IP ; 
	}
	
	
	// Get all usernames of the database 
	public ArrayList<String> getAllUsernames() {
		System.out.println("[LocalDB] Getting all usernames of the database...");
		String query = "SELECT UsernameToIP.Username FROM UsernameToIP ;";	
		ArrayList<String> results = new ArrayList<String>() ; 
		
		try {
			// Execute the statement 
			ResultSet rs = this.statement.executeQuery(query) ;
			
			while(rs.next()) {
				results.add(rs.getString(1));
			}
			rs.close(); 
		} 
		catch ( Exception e) {
			System.out.println(e);
		}
		
		System.out.println("[LocalDB] Usernames fetched");
		return results ; 
	}
	
	// Drop database 
	public void dropDatabase() {
		System.out.println("[LocalDB] Dropping the database...");
		String query = "DROP TABLE UsernameToIP ;" ;
		
		try {
			// Execute the statement 
			this.statement.executeUpdate(query) ;
			System.out.println("[LocalDB] Database dropped");
		} 
		catch (SQLException e) {
			System.out.println(e);
		}
	}
	
	
	// Close the connection of database 
	public void closeConnection() {
		System.out.println("[LocalDB] Closing connection...");
		try {
			this.connection.close() ;
			System.out.println("[LocalDB] Connection closed");
		} 
		catch (SQLException e) {
			System.out.println(e) ; 
		}
		
	}	
	

	public static void main(String[] args) {
		
	}
		

}
