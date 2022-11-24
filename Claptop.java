/**
 * @author Thomas Moroney
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 *
 * Client class
 *
 * An instance accepts user input
 *
 */
public class Claptop extends Node {
	static final int CLAPTOP_PORT = 50000;
	static final int GW1_PORT = 50001;
	static final String GW1_NODE = "GW1";
	InetSocketAddress dstAddress;
	int packetNum;

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	Claptop(String dstHost, int dstPort, int srcPort) {
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
			
			if (content.getType() == PacketContent.DATAPACKET) {
				MyPacket inPacket = ((MyPacket)content);
				String s = new String(inPacket.byteArray, StandardCharsets.UTF_8);
				String output = s.substring(13); // remove destination and header
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
		    String input = scanner.nextLine();
			if (input.equals("exit")) {
				scanner.close();
				return;
			}
			else if (input.equals("2")) {
				System.out.print("Enter an amount to deposit: ");
				input = scanner.nextLine();
			}
			else {
				input = "balance";
			}

			input = "000" + input;
    
		    DatagramPacket request;
            request = new MyPacket(input.getBytes()).toDatagramPacket();
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
			(new Claptop(GW1_NODE, GW1_PORT, CLAPTOP_PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
