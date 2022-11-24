/**
 * @author Thomas Moroney
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class DServer extends Node {
    static final int DSERVER_PORT = 50005; // current
	static final int CP_PORT = 50003;      // cloud provider
    static final String CP_NODE = "CP";
    InetSocketAddress dstAddress;
	double balance = 100;

	DServer(int port) {
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
				String s = (inPacket.text).substring(3); // remove number of destination and header
				System.out.println(s);

				dstAddress = new InetSocketAddress(CP_NODE, CP_PORT);
				
				if (s.contains("balance")) { // SEND DATA REQUESTED
					System.out.println("Checking Account Balance...");
		        	System.out.println("Balance is: $" + balance);

					returnString = "010" + "Your current balance is $" + balance;
					returnPacket = new TextPacket(returnString);
					sendReturnPacket(returnPacket);
				}
				else if (Character.isDigit(s.charAt(0))) {
					double deposit = Double.parseDouble(s.trim());
					balance = balance + deposit;
					returnString = "010" + "Deposit was successful - new balance is $" + balance;
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
		    System.out.println("Sent Packet w/ current balance\n");
		}
		catch(Exception e) {e.printStackTrace();}
	}
		


	public synchronized void start() throws Exception {
		System.out.println("Waiting for contact");
		this.wait();
	}

	public static void main(String[] args) {
		try {
			(new DServer(DSERVER_PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
    
}
