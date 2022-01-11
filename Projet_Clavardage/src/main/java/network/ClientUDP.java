package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress; 

public class ClientUDP {

	// Constructor => not needed because this class only defines static method 
	// that is to say that they can be called without creating the object


	// Broadcast function
	public static void broadcast (String msg) {
		System.out.println("[ClientUDP]"+ msg);
		
		// Used port 
		int port = 5001;

		try {
			
			DatagramSocket socket = new DatagramSocket();
			byte buffer[] = null;
			InetAddress group = InetAddress.getByName("224.13.31.7");
			buffer = msg.getBytes();
			
			DatagramPacket packet = new DatagramPacket(buffer,buffer.length,group,port);
			socket.send(packet);
			socket.close() ;

		}
		catch (Exception e){
			System.out.println(e);
		}
	}

	public static void main (String [] args) {
		int count=0;
		while(count<5) {
			broadcast("Test du  multicast numero : "+count);
			count++;
		}
	}

}