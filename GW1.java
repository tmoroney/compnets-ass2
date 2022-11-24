/**
 * @author Thomas Moroney
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class GW1 extends Node {
    static final int CLAPTOP_PORT = 50000;
	static final int GW1_PORT = 50001;      // current node
	static final int ISP_PORT = 50002;
	static final String CLAPTOP_NODE = "Claptop";
	static final String ISP_NODE = "ISP";
	static final int CONTROLLER_PORT = 50004;
	static final String CONTROLLER_NODE = "Controller";

	InetSocketAddress dstAddress;
	HashMap<String, Integer> nextJump = new HashMap<>(); // stores the jump after asking the controller so there is no need to ask again
	HashMap<Integer, String> nodeList = new HashMap<>();  // map each port to a node name
	DatagramPacket currentPacket;
	String dest; // destination of incoming packet

	GW1(int port) {
		try {
			nodeList.put(CLAPTOP_PORT, CLAPTOP_NODE);
			nodeList.put(ISP_PORT, ISP_NODE);
			socket= new DatagramSocket(port);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	// handle incoming packets
	public void onReceipt(DatagramPacket packet) {
		try {
			System.out.println("Received packet");

			PacketContent content= PacketContent.fromDatagramPacket(packet);

			if (content.getType()==PacketContent.TEXTPACKET) {
				currentPacket = packet;
				TextPacket inPacket = ((TextPacket)content);
			    dest = (inPacket.text).substring(0, 3); // isolate destination at beginning of packet
			
				if (nextJump.containsKey(dest)) {
					int destPort = nextJump.get(dest);
					System.out.println("Found next jump in forwarding table.");
					System.out.println("Forwarded packet to " + nodeList.get(destPort));
					dstAddress = new InetSocketAddress(nodeList.get(destPort), destPort);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
				}
				else {
					System.out.println("Next jump not stored. Requesting next jump from Controller...");
					dstAddress = new InetSocketAddress(CONTROLLER_NODE, CONTROLLER_PORT);
					packet.setSocketAddress(dstAddress);
					socket.send(packet);
				}
			}
			else if (content.getType()==PacketContent.NEXTNODE) {
				JumpPacket inPacket = ((JumpPacket)content);
				int port = inPacket.getPort();

				System.out.println("Controller returned port " + port + " as the next jump.");
				System.out.println("Forwarded packet to " + nodeList.get(port));

				nextJump.put(dest, port); // adds next node to hashmap for later use
				dstAddress = new InetSocketAddress(nodeList.get(port), port);
				currentPacket.setSocketAddress(dstAddress);
				socket.send(currentPacket);
			}	
		}
		catch(Exception e) {e.printStackTrace();}
	}


	public synchronized void start() throws Exception {
		System.out.println("Waiting for contact");
		this.wait();
	}

	public static void main(String[] args) {
		try {
			(new GW1(GW1_PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
