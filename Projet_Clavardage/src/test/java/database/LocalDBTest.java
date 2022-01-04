package database;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.After;
import java.io.IOException;
import java.sql.DriverManager;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet ; 
import org.junit.Test;

import java.util.Arrays;
import java.util.Random ; 


public class LocalDBTest {
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////// Definition of global variables needed to realize tests ///////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	/* **************************************************************************************** */
	// GLOBAL VARIABLES 	
	
	// Database
	protected static LocalDB db = new LocalDB() ; 
	protected static Connection connection ; 
	protected static Statement statement ;
	protected static String addrDb ;
	protected static String login ;
	protected static String password ;
	
	// Person 
	protected static int nbUsers = 100 ; 
	protected static String [] usernames = new String [nbUsers] ; 
	protected static InetAddress [] IPAddresses = new InetAddress [nbUsers] ;
	
	// Random number 
	Random rand = new Random() ;
	protected static int nbTests = 10 ;
	protected static int [] indexes = new int [nbTests] ; 
	/* **************************************************************************************** */

	
	/* **************************************************************************************** */
	////////////////////////////////// addUSer - global variables ////////////////////////////////
	// Variables needed for the function 
	
	// Expected variables
	
	// Result variables

	/* **************************************************************************************** */
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////// Before Class = realized before the class //////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	
	@BeforeClass 
	public static void initAll() throws IOException {
		
		// Connection to database
		addrDb = "jdbc:mysql://localhost:3306/localdatabase?";
		login = "root" ;
		password = "root" ;
		try {
			db.connection = DriverManager.getConnection(db.addrDb, db.login, db.password);
		} catch (SQLException e)
        {
            fail(e.toString());
        }
		
		// Users and IPAdresses 
		for (int i=0; i<nbUsers ; i++) { 
			// Usernames
			usernames[i]="username"+i ;
			
			//IPAddresses
			byte[] ipAddr = new byte[]{(byte)192, (byte)168, (byte)0, (byte)i};
			IPAddresses[i]= InetAddress.getByAddress(ipAddr); 
		}	
	}
	
	
	
	@Before 
	public void initBeforeTests() throws IOException {
	/* **************************************************************************************** */
	//////////////////////////////////// Add users to database ///////////////////////////////////
		for (int i=0; i<nbUsers; i++) {
			db.addUser(usernames[i], IPAddresses[i]);
		}	
	/* **************************************************************************************** */
	}
	
	@After
	public void resetAfterTests() throws IOException {
		////////////////////////////////////// Drop database /////////////////////////////////////
		try {
			db.statement.executeUpdate("TRUNCATE TABLE UsernameToIP") ; 
		} catch (SQLException e) {
				System.out.println(e);
		}
	}


	//////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////// TESTS ////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////
	

	
	////////////////////////////////////// FUNCTION addUser /////////////////////////////////////	
	@Test
	public void testAddUser() {
		try {			
			for (int i=0; i<nbUsers; i++) {	
				ResultSet rs=db.statement.executeQuery("SELECT * FROM UsernameToIP WHERE Username='" + usernames[i] + "'") ; 
				assertTrue(rs.next());
				// Check usernames
				assertEquals(usernames[i], rs.getString("username")) ; 
				// Check IPs
				assertEquals(IPAddresses[i].toString(), rs.getString("ip")) ; 
			}
			
		} catch (SQLException e) {
			System.out.println(e);
		}
	} 

	
	//////////////////////////////// FUNCTION deleteUserByName //////////////////////////////////	
	@Test
	public void testDeleteUserByName() {
		try {
			// Define an array with 10 different random numbers 
			int nb=0 ; 
			while (nb<10) {
				int j = rand.nextInt(nbUsers) ;
				if (!(Arrays.asList(indexes).contains(j))) { 
					indexes[nb]=j ; 
					nb++ ; 
				}
			}
			// Delete 10 random users by name
			for (int i=0; i<nbTests; i++) {	
				// Delete the user of random index
				db.deleteUserByName(usernames[indexes[i]]) ; 
				
				// Check if one and only one user has been deleted 
				ResultSet rs=db.statement.executeQuery("SELECT COUNT(*) FROM UsernameToIP") ; 
				if (rs.next()) {
					assertEquals((nbUsers-i-1), rs.getInt(1)) ;
				}
				// Check that the good user has been deleted from the database 
				ResultSet rs2=db.statement.executeQuery("SELECT * FROM UsernameToIP WHERE Username='" + usernames[indexes[i]] + "'") ;
				assertFalse(rs2.next());
			}
			
		} catch (SQLException e) {
			System.out.println(e);
		}
	} 

	
	///////////////////////////////// FUNCTION deleteUserByIP //////////////////////////////////	
	@Test
	public void testDeleteUserByIP() {
		try {
			// Define an array with 10 different random numbers 
			int nb=0 ; 
			while (nb<10) {
				int j = rand.nextInt(nbUsers) ;
				if (!(Arrays.asList(indexes).contains(j))) { 
					indexes[nb]=j ; 
					nb++ ; 
				}
			}
			// Delete 10 random users by IP
			for (int i=0; i<nbTests; i++) {	
				// Delete the user of random index
				db.deleteUserByIP(IPAddresses[indexes[i]]) ; 
				
				// Check if one and only one user has been deleted 
				ResultSet rs=db.statement.executeQuery("SELECT COUNT(*) FROM UsernameToIP") ; 
				if (rs.next()) {
					assertEquals((nbUsers-i-1), rs.getInt(1)) ;
				}
				// Check that the good user has been deleted from the database 
				ResultSet rs2=db.statement.executeQuery("SELECT * FROM UsernameToIP WHERE ip='" + IPAddresses[indexes[i]] + "'") ;
				assertFalse(rs2.next());
			}
			
		} catch (SQLException e) {
			System.out.println(e);
		}
		
	} 

	
	/////////////////////////////////// FUNCTION getUsername ///////////////////////////////////	
	@Test
	public void testGetUsername() {
		for (int i=0; i<nbUsers; i++) {
			try {
				ResultSet rs=db.statement.executeQuery("SELECT username FROM UsernameToIP WHERE ip='" + IPAddresses[i] + "'") ;
				if (rs.next()) {
					assertEquals(usernames[i], rs.getString(1)) ; 
				}
			} catch (SQLException e) {
				System.out.println(e) ; 
			}
		}
	} 

	
	////////////////////////////////////// FUNCTION getIP //////////////////////////////////////	
	@Test
	public void testGetIP() {
		for (int i=0; i<nbUsers; i++) {
			try {
				ResultSet rs=db.statement.executeQuery("SELECT ip FROM UsernameToIP WHERE username='" + usernames[i] + "'") ;
				if (rs.next()) {
					assertEquals(IPAddresses[i].toString(), rs.getString(1)) ; 
				}
			} catch (SQLException e) {
				System.out.println(e) ; 
			}
		}
	} 

	
	/////////////////////////////////// FUNCTION dropDatabase //////////////////////////////////	
	/* @Test
	public void testDropDatabase() {
		fail("Not yet implemented");
	} */

	
	///////////////////////////////// FUNCTION closeConnection /////////////////////////////////	
	/* @Test
	public void testCloseConnection() {
		fail("Not yet implemented");
	} */ 


}
