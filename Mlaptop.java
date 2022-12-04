/**
 * @author Thomas Moroney
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 *
 * Claptop class
 *
 * An instance accepts user input
 *
 */
public class Mlaptop extends Node {
	static final int MLAPTOP_PORT = 50009;
	static final String MLAPTOP_ID = "100";
	static final int GW3_PORT = 50010;
	static final String GW3_NODE = "GW3";
	InetSocketAddress dstAddress;

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	Mlaptop(String dstHost, int dstPort, int srcPort) {
		try {
			dstAddress= new InetSocketAddress(dstHost, dstPort);
			socket= new DatagramSocket(srcPort);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}


	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		try {
			PacketContent content = PacketContent.fromDatagramPacket(packet);
			
			if (content.getType() == PacketContent.TEXTPACKET) {
				TextPacket inPacket = ((TextPacket)content);
				String s = inPacket.text;
				String output = s.substring(3); // remove destination header
				System.out.println(output + "\n");
				notify();
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}


	/**
	 * Sender Method
	 *
	 */
	public synchronized void start() throws Exception {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.print("Would you like to check your balance (1) or make a deposit (2)? ");
		    String input = scanner.next();
			if (input.equals("exit")) {
				scanner.close();
				return;
			}
			else if (input.equals("2")) {
				System.out.print("Enter an amount to deposit: ");
				input = scanner.next();
			}
			else {
				input = "balance";
			}

			int serverNum = 1;
			boolean hasInt = false;
			while(!hasInt) {
				System.out.print("Which bank account would you like to access (1 or 2)?: ");
				if (scanner.hasNextInt()) {
					serverNum = scanner.nextInt();
					hasInt = true;
				}
				else {
					System.out.println("Please enter a number!");
				}
			}

			String dest;
			switch (serverNum) {
				case 1:
					dest = "000"; // DServer
					break;
				case 2:
					dest = "001"; // DServer2
					break;
				default:
					dest = "000";
			}
			
			input = dest + MLAPTOP_ID + input; // add identifier so DServer knows where to send the packet back to
    
		    DatagramPacket request;
            request = new TextPacket(input).toDatagramPacket();
            request.setSocketAddress(dstAddress);
            socket.send(request);
		    this.wait();
		}
	}


	/**
	 * Test method
	 *
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
		try {
			(new Mlaptop(GW3_NODE, GW3_PORT, MLAPTOP_PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
