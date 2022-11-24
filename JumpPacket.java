/**
 * @author Thomas Moroney
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents file information
 *
 */
public class JumpPacket extends PacketContent {

	int port;

	/**
	 * Constructor that takes in port of the next node.
	 * @param port Size of filename.
	 */
	JumpPacket(int port) {
		type= NEXTNODE;
		this.port = port;
	}

	/**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet that contains information about a file.
	 */
	protected JumpPacket(ObjectInputStream oin) {
		try {
			type= NEXTNODE;
			port= oin.readInt();
		}
		catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Writes the content into an ObjectOutputStream
	 *
	 */
	protected void toObjectOutputStream(ObjectOutputStream oout) {
		try {
			oout.writeInt(port);
		}
		catch(Exception e) {e.printStackTrace();}
	}


	/**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String.
	 */
	public String toString() {
		return "Port: " + port;
	}

	/**
	 * Returns the port contained in the packet.
	 *
	 * @return Returns the port contained in the packet.
	 */
	public int getPort() {
		return port;
	}
}
