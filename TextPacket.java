/**
 * @author Thomas Moroney
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents acknowledgements
 *
 */
public class TextPacket extends PacketContent {

	String text;

	/**
	 * Constructor that takes in string.
	 * @param text Text string.
	 */
	TextPacket(String text) {
		type= TEXTPACKET; // equal to 10 (value to identify)
		this.text = text;
	}

	/**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet that contains a string.
	 */
	protected TextPacket(ObjectInputStream oin) { // when receive a packet it comes as ObjectInputStream (string)
		try {
			type= TEXTPACKET;    // just assigning
			text= oin.readUTF(); // takes content and turns it back into original form - must change to readbytes etc to read data
		}
		catch(Exception e) {e.printStackTrace();}
	}

	/**
	 * Writes the content into an ObjectOutputStream
	 *
	 */
	protected void toObjectOutputStream(ObjectOutputStream oout) { // converts to UTF8 for transfer
		try {
			oout.writeUTF(text); // UTF8 encoding
		}
		catch(Exception e) {e.printStackTrace();} //
	}



	/**
	 * Returns the content of the packet as String.
	 *
	 * @return Returns the content of the packet as String.
	 */
	public String toString() {
		return "Content:" + text;
	}

	/**
	 * Returns the info contained in the packet.
	 *
	 * @return Returns the info contained in the packet.
	 */
	public String getPacketInfo() {
		return text;
	}
}
