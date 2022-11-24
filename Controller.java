/**
 * @author Thomas Moroney
 */

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.ArrayList;

public class Controller extends Node {
    static final int CONTROLLER_PORT = 50004; // current
	static final int GW1_PORT = 50001;
	static final int ISP_PORT = 50002;
	static final int CP_PORT = 50003;
    InetSocketAddress dstAddress;
	HashMap<String, ArrayList<int[]>> map = new HashMap<String, ArrayList<int[]>>(); // stores paths to destinations

	Controller(int port) {
		try {
			ArrayList<int[]> dest1 = new ArrayList<int[]>();
			dest1.add(new int[]{GW1_PORT, ISP_PORT, CP_PORT, 50005}); // DServer
			ArrayList<int[]> dest2 = new ArrayList<int[]>();
			dest2.add(new int[]{GW1_PORT, ISP_PORT, CP_PORT, 50006}); // DServer1
			ArrayList<int[]> dest3 = new ArrayList<int[]>();
			dest3.add(new int[]{CP_PORT, ISP_PORT, GW1_PORT, 50000}); // Claptop

			map.put("000", dest1); // DServer
			map.put("001", dest2); // DServer1
			map.put("010", dest3); // Claptop

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

				TextPacket inPacket = ((TextPacket)content);
				String dest = (inPacket.text).substring(0, 3); // isolate destination at beginning of packet
				System.out.println(dest);

				ArrayList<int[]> paths = map.get(dest);
				boolean foundJump = false;

				for (int i=0; i<paths.size() && !foundJump; i++) 
				{
					int[] array = paths.get(i);
					for (int j=0; j<array.length && !foundJump; j++) // find current position and check for next jump
					{ 
						if (packet.getPort() == array[i]) 
						{
							JumpPacket nextJump = new JumpPacket(array[i+1]);
							System.out.println("Next jump is to port: " + nextJump);
							DatagramPacket returnPacket = nextJump.toDatagramPacket();
							returnPacket.setSocketAddress(packet.getSocketAddress());
							socket.send(returnPacket);
							foundJump = true;
						}
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
