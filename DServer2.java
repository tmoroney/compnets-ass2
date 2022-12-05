/**
 * @author Thomas Moroney
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class DServer2 extends Node {
    static final int DSERVER2_PORT = 50008; // current
	static final int CP_PORT = 50006;      // cloud provider
    static final String CP_NODE = "CP";
    InetSocketAddress dstAddress;
	double balance = 200;

	DServer2(int port) {
		try {
			socket= new DatagramSocket(port);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	/**
	 * Assume that incoming packets are request packets.
	 */
	public void onReceipt(DatagramPacket packet) {
		try {
			PacketContent content= PacketContent.fromDatagramPacket(packet);

			if (content.getType()==PacketContent.TEXTPACKET) {
				System.out.println("Received request packet");

		        TextPacket returnPacket;
				String returnString;

		        TextPacket inPacket = ((TextPacket)content);
				String dest = (inPacket.text).substring(3, 6); // isolate return destination
				String s = (inPacket.text).substring(6); // remove header
				System.out.println("Packet content: "+ s);
				System.out.println("Return Destination: " + dest);

				dstAddress = new InetSocketAddress(CP_NODE, CP_PORT);
				
				if (s.contains("balance")) { // SEND DATA REQUESTED
					System.out.println("Checking Account Balance...");
		        	System.out.println("Balance is: $" + balance);

					returnString = dest + "Your current balance is $" + balance + " (Bank Account 2)";
					returnPacket = new TextPacket(returnString);
					sendReturnPacket(returnPacket);
				}
				else if (Character.isDigit(s.charAt(0))) {
					double deposit = Double.parseDouble(s.trim());
					balance = balance + deposit;
					returnString = dest + "Deposit was successful - new balance is $" + balance + " (Bank Account 2)";
					returnPacket = new TextPacket(returnString);
					sendReturnPacket(returnPacket);
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}


	public void sendReturnPacket(TextPacket myPacket) { // change to send byte array packet instead
		try {
			DatagramPacket returnPacket= myPacket.toDatagramPacket();
		    returnPacket.setSocketAddress(dstAddress);
		    socket.send(returnPacket);    // Send packet with file name and length
		    System.out.println("Sent Return Packet\n");
		}
		catch(Exception e) {e.printStackTrace();}
	}
		


	public synchronized void start() throws Exception {
		System.out.println("Waiting for contact");
		this.wait();
	}

	public static void main(String[] args) {
		try {
			(new DServer2(DSERVER2_PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
    
}
