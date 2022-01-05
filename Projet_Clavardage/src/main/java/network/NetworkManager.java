package network;

import manager.Manager;
import java.net.InetAddress ;
import java.net.UnknownHostException;

public class NetworkManager {
	
	// Attributes 
	ServerTCP serverTCP ; 
	ServerUDP serverUDP ;
	static InetAddress myIP ;
	
	// Constructor 
	public NetworkManager() {
		System.out.println("Constructeur du NetworkManager");
		this.serverTCP = new ServerTCP() ; 
		this.serverUDP = new ServerUDP(5001, 50000) ;
		try {
			myIP = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()) ;
		} catch (UnknownHostException e) {
			System.out.println(e);
		}
	}
	
	public InetAddress getMyIP() {
		return myIP ;
	}
	
	//////////////////////////////////////////////////////////////////////
	///////////////////////////MESSAGES FORMAT////////////////////////////
	//////////////////////////////////////////////////////////////////////
		
	// Enum for each message type
	public static enum MessageType {
		USERNAME_BRDCST,
		USERNAME_CONNECTED,
		USERNAME_DISCONNECT,
		GET_USERNAMES,
		MESSAGE
	}
	
	
	// Message formatting
	public static String messageFormatter(MessageType type,String username, String content) {
		return (type + "|" + username+ "|" + content);
	}
	
	
	//////////////////////////////////////////////////////////////////////
	////////////////////PRINCIPAL SENDMESSAGE FUNCTION////////////////////
	//////////////////////////////////////////////////////////////////////
	
	// Classic message sending function
	public void sendMessage(String msg, InetAddress destinationIP) {
		System.out.println("Sending message...");
		// Classic message between users : type 1 ??
		String formatedMsg = messageFormatter(MessageType.MESSAGE, Manager.getUsername(), msg);
		// Sends message and closes socket
		ClientTCP.sendMessage(formatedMsg, destinationIP);
	}
	
	
	//////////////////////////////////////////////////////////////////////
	///////////////////////////////EMISSION///////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	// Availability of the username
	public synchronized boolean usernameAvailable(String username) {
		System.out.println("Calling usernameAvailable(username)");
		long timeElapsed = 0;
		long start = System.currentTimeMillis();
		long finish = 0;
		String msg = messageFormatter(MessageType.USERNAME_BRDCST, username, myIP.toString());
		
		//ask everyone if the username is available 
		ClientUDP.broadcast(msg);
		
		//We wait for an answer (users only answer if their username is the same as the one we want)
		while(timeElapsed<1000) {
			finish = System.currentTimeMillis();
			timeElapsed = finish - start;			
		}
		
		//We check if our TCP server has received a message notifying that 
		//the username is not available or not
		return serverTCP.getAvailable() ;
	}
	
	
	//Once we are connected, we send a broadcast with our username and our IP
	public void notifyConnected(String username) {
		System.out.println("Calling notifyConnected(username)");
		String msg = messageFormatter(MessageType.USERNAME_CONNECTED, username, myIP.toString()) ;
		ClientUDP.broadcast(msg);
	}
	
	
	public void notifyDisconnected(String username) {
		System.out.println("Calling notifyDisonnected()");
		String msg = messageFormatter(MessageType.USERNAME_DISCONNECT, username, myIP.toString()) ;
		ClientUDP.broadcast(msg);
	}

	
	//////////////////////////////////////////////////////////////////////
	///////////////////////////////ANSWER/////////////////////////////////
	//////////////////////////////////////////////////////////////////////

	//Call Manager to know if the username wanted is valid
	public static void usernameRequest(String username, InetAddress destinationIP) {
		Manager.usernameRequest(username, destinationIP);
	}
	
	
	//The username wanted is not available so we notify the user
	public void sendUnavailableUsername(InetAddress destinationIP) {
		System.out.println("Calling sendUnavailableUsername(host)");
		String msg = messageFormatter(MessageType.USERNAME_BRDCST, Manager.getUsername(), "") ;
		ClientTCP.sendMessage(msg, destinationIP);
	}
	
	
	//We add the user to the DB
	public static void newUserConnected(String username, InetAddress IP) {
		Manager.newUserConnected(username, IP);
	}
	
	
	//We delete the user from the DB
	public static void userDisconnected(String username) {
		Manager.userDisconnected(username);
	}
	
	
	//If someone asks our username, we answer with our username and our IP
	public static void sendUsername(InetAddress destinationIP) {
		System.out.println("Calling sendUsername");
		String msg = messageFormatter(MessageType.GET_USERNAMES, Manager.getUsername(), myIP.toString()) ;
		ClientTCP.sendMessage(msg, destinationIP);	
	}
	
	
	//////////////////////////////////////////////////////////////////////
	////////////////////////////DISCONNECTION/////////////////////////////
	//////////////////////////////////////////////////////////////////////
	
	// Principal function of disconnection
	public void disconnection(String username) {
		System.out.println("[NetworkManager] Calling disconnection()");
		notifyDisconnected(username);
		serverUDP.setConnected(false) ;
		serverTCP.setConnected(false) ;
	}
				
	
}