/**
 * @author Thomas Moroney
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Controller extends Node {
    static final int CONTROLLER_PORT = 50004; // current
	static final int GW1_PORT = 50001;
	static final int ISP_PORT = 50002;
	static final int CP_PORT = 50003;
    static final String CP_NODE = "CP";
    InetSocketAddress dstAddress;
	HashMap<String, int[]> map = new HashMap<String, int[]>(); // stores paths to destinations

	Controller(int port) {
		try {
			map.put("000", new int[]{50001, 50002, 50003, 50005}); // DServer
			map.put("001", new int[]{50001, 50002, 50003, 50006}); // DServer1
			map.put("010", new int[]{50003, 50002, 50001, 50000}); // Claptop
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

			if (content.getType()==PacketContent.DATAPACKET) {
				System.out.println("Received request packet");

				MyPacket inPacket = ((MyPacket)content);
				String s = new String(inPacket.byteArray, StandardCharsets.UTF_8);
				String dest = s.substring(10, 13); // isolate destination at beginning of packet
				System.out.println(dest);
				
				JumpPacket nextJump;
				int[] array = map.get(dest);

				for (int i=0; i<array.length; i++) { // find current position and check for next jump
					if (packet.getPort() == array[i]) {
						nextJump = new JumpPacket(array[i+1]);
						System.out.println("Next jump is to port: " + nextJump);
						DatagramPacket returnPacket = nextJump.toDatagramPacket();
				        returnPacket.setSocketAddress(packet.getSocketAddress());
		    	        socket.send(returnPacket);    // Send packet with file name and length
						break;
					}
				}
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
			(new Controller(CONTROLLER_PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
    
}
